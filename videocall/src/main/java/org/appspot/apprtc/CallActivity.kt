/*
 *  Copyright 2015 The WebRTC Project Authors. All rights reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package org.appspot.apprtc

import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.app.FragmentTransaction
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.widget.Toast
import org.appspot.apprtc.AppRTCAudioManager.AudioDevice
import org.appspot.apprtc.AppRTCClient.RoomConnectionParameters
import org.appspot.apprtc.AppRTCClient.SignalingParameters
import org.appspot.apprtc.PeerConnectionClient.DataChannelParameters
import org.appspot.apprtc.PeerConnectionClient.PeerConnectionParameters
import org.appspot.apprtc.util.sharedPrefGetBoolean
import org.appspot.apprtc.util.sharedPrefGetInteger
import org.appspot.apprtc.util.sharedPrefGetString
import org.appspot.apprtc.util.validateUrl
import org.webrtc.*
import org.webrtc.RendererCommon.ScalingType
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Activity for peer connection call setup, call waiting
 * and call view.
 */
class CallActivity : Activity(), AppRTCClient.SignalingEvents, PeerConnectionClient.PeerConnectionEvents,
    CallFragment.OnCallEvents {

    private val remoteProxyRenderer = ProxyRenderer()
    private val localProxyRenderer = ProxyRenderer()
    private var peerConnectionClient: PeerConnectionClient? = null
    private var appRtcClient: AppRTCClient? = null
    private var signalingParameters: SignalingParameters? = null
    private var audioManager: AppRTCAudioManager? = null
    private var rootEglBase: EglBase? = null
    private var pipRenderer: SurfaceViewRenderer? = null
    private var fullscreenRenderer: SurfaceViewRenderer? = null
    private var videoFileRenderer: VideoFileRenderer? = null
    private val remoteRenderers = ArrayList<VideoRenderer.Callbacks>()
    private var logToast: Toast? = null
    private var commandLineRun: Boolean = false
    private var runTimeMs: Int = 0
    private var activityRunning: Boolean = false
    private var roomConnectionParameters: RoomConnectionParameters? = null
    private var peerConnectionParameters: PeerConnectionParameters? = null
    private var iceConnected: Boolean = false
    private var isError: Boolean = false
    private var callControlFragmentVisible = true
    private var callStartedTimeMs: Long = 0
    private var micEnabled = true
    private var screencaptureEnabled = false
    // True if local view is in the fullscreen renderer.
    private var isSwappedFeeds: Boolean = false

    // Controls
    private var callFragment: CallFragment? = null
    private var hudFragment: HudFragment? = null
    private var cpuMonitor: CpuMonitor? = null

    private val displayMetrics: DisplayMetrics
        @TargetApi(17)
        get() {
            val displayMetrics = DisplayMetrics()
            val windowManager = application.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
            return displayMetrics
        }

    private inner class ProxyRenderer : VideoRenderer.Callbacks {
        private var target: VideoRenderer.Callbacks? = null

        @Synchronized
        override fun renderFrame(frame: VideoRenderer.I420Frame) {
            if (target == null) {
                Logging.d(TAG, "Dropping frame in proxy because target is null.")
                VideoRenderer.renderFrameDone(frame)
                return
            }

            target!!.renderFrame(frame)
        }

        @Synchronized
        fun setTarget(target: VideoRenderer.Callbacks?) {
            this.target = target
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(UnhandledExceptionHandler(this))

        // Set window styles for fullscreen-window size. Needs to be done before
        // adding content.
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(
            LayoutParams.FLAG_FULLSCREEN or LayoutParams.FLAG_KEEP_SCREEN_ON
                    or LayoutParams.FLAG_DISMISS_KEYGUARD or LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or LayoutParams.FLAG_TURN_SCREEN_ON
        )
        window.decorView.systemUiVisibility = systemUiVisibility
        setContentView(R.layout.activity_call)

        iceConnected = false
        signalingParameters = null

        // Create UI controls.
        pipRenderer = findViewById<View>(R.id.pip_video_view) as SurfaceViewRenderer
        fullscreenRenderer = findViewById<View>(R.id.fullscreen_video_view) as SurfaceViewRenderer
        callFragment = CallFragment()
        hudFragment = HudFragment()

        // Show/hide call control fragment on view click.
        val listener = View.OnClickListener { toggleCallControlFragmentVisibility() }

        // Swap feeds on pip view click.
        pipRenderer!!.setOnClickListener { setSwappedFeeds(!isSwappedFeeds) }

        fullscreenRenderer!!.setOnClickListener(listener)
        remoteRenderers.add(remoteProxyRenderer)

        val intent = intent

        // Create video renderers.
        rootEglBase = EglBase.create()
        pipRenderer!!.init(rootEglBase!!.eglBaseContext, null)
        pipRenderer!!.setScalingType(ScalingType.SCALE_ASPECT_FIT)
        val saveRemoteVideoToFile = intent.getStringExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE)

        // When saveRemoteVideoToFile is set we save the video from the remote to a file.
        if (saveRemoteVideoToFile != null) {
            val videoOutWidth = intent.getIntExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, 0)
            val videoOutHeight = intent.getIntExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, 0)
            try {
                videoFileRenderer = VideoFileRenderer(
                    saveRemoteVideoToFile, videoOutWidth, videoOutHeight, rootEglBase!!.eglBaseContext
                )
                remoteRenderers.add(videoFileRenderer!!)
            } catch (e: IOException) {
                throw RuntimeException(
                    "Failed to open video file for output: $saveRemoteVideoToFile", e
                )
            }

        }
        fullscreenRenderer!!.init(rootEglBase!!.eglBaseContext, null)
        fullscreenRenderer!!.setScalingType(ScalingType.SCALE_ASPECT_FILL)

        pipRenderer!!.setZOrderMediaOverlay(true)
        pipRenderer!!.setEnableHardwareScaler(true /* enabled */)
        fullscreenRenderer!!.setEnableHardwareScaler(true /* enabled */)
        // Start with local feed in fullscreen and swap it to the pip when the call is connected.
        setSwappedFeeds(true /* isSwappedFeeds */)

        // Check for mandatory permissions.
        for (permission in MANDATORY_PERMISSIONS) {
            if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                logAndToast("Permission $permission is not granted")
                setResult(RESULT_CANCELED)
                finish()
                return
            }
        }

        val roomUri = intent.data
        if (roomUri == null) {
            logAndToast(getString(R.string.missing_url))
            Log.e(TAG, "Didn't get any URL in intent!")
            setResult(RESULT_CANCELED)
            finish()
            return
        }

        // Get Intent parameters.
        val roomId = intent.getStringExtra(EXTRA_ROOMID)
        Log.d(TAG, "Room ID: $roomId")
        if (roomId == null || roomId.length == 0) {
            logAndToast(getString(R.string.missing_url))
            Log.e(TAG, "Incorrect room ID in intent!")
            setResult(RESULT_CANCELED)
            finish()
            return
        }

        val loopback = intent.getBooleanExtra(EXTRA_LOOPBACK, false)
        val tracing = intent.getBooleanExtra(EXTRA_TRACING, false)

        var videoWidth = intent.getIntExtra(EXTRA_VIDEO_WIDTH, 0)
        var videoHeight = intent.getIntExtra(EXTRA_VIDEO_HEIGHT, 0)

        screencaptureEnabled = intent.getBooleanExtra(EXTRA_SCREENCAPTURE, false)
        // If capturing format is not specified for screencapture, use screen resolution.
        if (screencaptureEnabled && videoWidth == 0 && videoHeight == 0) {
            val displayMetrics = displayMetrics
            videoWidth = displayMetrics.widthPixels
            videoHeight = displayMetrics.heightPixels
        }
        var dataChannelParameters: DataChannelParameters? = null
        if (intent.getBooleanExtra(EXTRA_DATA_CHANNEL_ENABLED, false)) {
            dataChannelParameters = DataChannelParameters(
                intent.getBooleanExtra(EXTRA_ORDERED, true),
                intent.getIntExtra(EXTRA_MAX_RETRANSMITS_MS, -1),
                intent.getIntExtra(EXTRA_MAX_RETRANSMITS, -1), intent.getStringExtra(EXTRA_PROTOCOL),
                intent.getBooleanExtra(EXTRA_NEGOTIATED, false), intent.getIntExtra(EXTRA_ID, -1)
            )
        }
        peerConnectionParameters = PeerConnectionParameters(
            intent.getBooleanExtra(EXTRA_VIDEO_CALL, true), loopback,
            tracing, videoWidth, videoHeight, intent.getIntExtra(EXTRA_VIDEO_FPS, 0),
            intent.getIntExtra(EXTRA_VIDEO_BITRATE, 0), intent.getStringExtra(EXTRA_VIDEOCODEC),
            intent.getBooleanExtra(EXTRA_HWCODEC_ENABLED, true),
            intent.getBooleanExtra(EXTRA_FLEXFEC_ENABLED, false),
            intent.getIntExtra(EXTRA_AUDIO_BITRATE, 0), intent.getStringExtra(EXTRA_AUDIOCODEC),
            intent.getBooleanExtra(EXTRA_NOAUDIOPROCESSING_ENABLED, false),
            intent.getBooleanExtra(EXTRA_AECDUMP_ENABLED, false),
            intent.getBooleanExtra(EXTRA_OPENSLES_ENABLED, false),
            intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_AEC, false),
            intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_AGC, false),
            intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_NS, false),
            intent.getBooleanExtra(EXTRA_ENABLE_LEVEL_CONTROL, false),
            intent.getBooleanExtra(EXTRA_DISABLE_WEBRTC_AGC_AND_HPF, false), dataChannelParameters
        )
        commandLineRun = intent.getBooleanExtra(EXTRA_CMDLINE, false)
        runTimeMs = intent.getIntExtra(EXTRA_RUNTIME, 0)

        Log.d(TAG, "VIDEO_FILE: '" + intent.getStringExtra(EXTRA_VIDEO_FILE_AS_CAMERA) + "'")

        // Create connection client. Use DirectRTCClient if room name is an IP otherwise use the
        // standard WebSocketRTCClient.
        if (loopback || !DirectRTCClient.IP_PATTERN.matcher(roomId).matches()) {
            appRtcClient = WebSocketRTCClient(this)
        } else {
            Log.i(TAG, "Using DirectRTCClient because room name looks like an IP.")
            appRtcClient = DirectRTCClient(this)
        }
        // Create connection parameters.
        val urlParameters = intent.getStringExtra(EXTRA_URLPARAMETERS)
        roomConnectionParameters = RoomConnectionParameters(roomUri.toString(), roomId, loopback, urlParameters)

        // Create CPU monitor
        cpuMonitor = CpuMonitor(this)
        hudFragment!!.setCpuMonitor(cpuMonitor)

        // Send intent arguments to fragments.
        callFragment!!.arguments = intent.extras
        hudFragment!!.arguments = intent.extras
        // Activate call and HUD fragments and start the call.
        val ft = fragmentManager.beginTransaction()
        ft.add(R.id.call_fragment_container, callFragment)
        ft.add(R.id.hud_fragment_container, hudFragment)
        ft.commit()

        // For command line execution run connection for <runTimeMs> and exit.
        if (commandLineRun && runTimeMs > 0) {
            Handler().postDelayed({ disconnect() }, runTimeMs.toLong())
        }

        peerConnectionClient = PeerConnectionClient.getInstance()
        if (loopback) {
            val options = PeerConnectionFactory.Options()
            options.networkIgnoreMask = 0
            peerConnectionClient!!.setPeerConnectionFactoryOptions(options)
        }
        peerConnectionClient!!.createPeerConnectionFactory(
            applicationContext, peerConnectionParameters, this@CallActivity
        )

        if (screencaptureEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startScreenCapture()
        } else {
            startCall()
        }
    }

    @TargetApi(21)
    private fun startScreenCapture() {
        val mediaProjectionManager = application.getSystemService(
            Context.MEDIA_PROJECTION_SERVICE
        ) as MediaProjectionManager
        startActivityForResult(
            mediaProjectionManager.createScreenCaptureIntent(), CAPTURE_PERMISSION_REQUEST_CODE
        )
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode != CAPTURE_PERMISSION_REQUEST_CODE)
            return
        mediaProjectionPermissionResultCode = resultCode
        mediaProjectionPermissionResultData = data
        startCall()
    }

    private fun useCamera2(): Boolean {
        return Camera2Enumerator.isSupported(this) && intent.getBooleanExtra(EXTRA_CAMERA2, true)
    }

    private fun captureToTexture(): Boolean {
        return intent.getBooleanExtra(EXTRA_CAPTURETOTEXTURE_ENABLED, false)
    }

    private fun createCameraCapturer(enumerator: CameraEnumerator): VideoCapturer? {
        val deviceNames = enumerator.deviceNames

        // First, try to find front facing camera
        Logging.d(TAG, "Looking for front facing cameras.")
        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating front facing camera capturer.")
                val videoCapturer = enumerator.createCapturer(deviceName, null)

                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }

        // Front facing camera not found, try something else
        Logging.d(TAG, "Looking for other cameras.")
        for (deviceName in deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating other camera capturer.")
                val videoCapturer = enumerator.createCapturer(deviceName, null)

                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }

        return null
    }

    @TargetApi(21)
    private fun createScreenCapturer(): VideoCapturer? {
        if (mediaProjectionPermissionResultCode != RESULT_OK) {
            reportError("User didn't give permission to capture the screen.")
            return null
        }
        return ScreenCapturerAndroid(
            mediaProjectionPermissionResultData, object : MediaProjection.Callback() {
                override fun onStop() {
                    reportError("User revoked permission to capture the screen.")
                }
            })
    }

    // Activity interfaces
    public override fun onStop() {
        super.onStop()
        activityRunning = false
        // Don't stop the video when using screencapture to allow user to show other apps to the remote
        // end.
        if (peerConnectionClient != null && !screencaptureEnabled) {
            peerConnectionClient!!.stopVideoSource()
        }
        cpuMonitor!!.pause()
    }

    public override fun onStart() {
        super.onStart()
        activityRunning = true
        // Video is not paused for screencapture. See onPause.
        if (peerConnectionClient != null && !screencaptureEnabled) {
            peerConnectionClient!!.startVideoSource()
        }
        cpuMonitor!!.resume()
    }

    override fun onDestroy() {
        Thread.setDefaultUncaughtExceptionHandler(null)
        disconnect()
        if (logToast != null) {
            logToast!!.cancel()
        }
        activityRunning = false
        rootEglBase!!.release()
        super.onDestroy()
    }

    // CallFragment.OnCallEvents interface implementation.
    override fun onCallHangUp() {
        disconnect()
    }

    override fun onCameraSwitch() {
        if (peerConnectionClient != null) {
            peerConnectionClient!!.switchCamera()
        }
    }

    override fun onVideoScalingSwitch(scalingType: ScalingType) {
        fullscreenRenderer!!.setScalingType(scalingType)
    }

    override fun onCaptureFormatChange(width: Int, height: Int, framerate: Int) {
        if (peerConnectionClient != null) {
            peerConnectionClient!!.changeCaptureFormat(width, height, framerate)
        }
    }

    override fun onToggleMic(): Boolean {
        if (peerConnectionClient != null) {
            micEnabled = !micEnabled
            peerConnectionClient!!.setAudioEnabled(micEnabled)
        }
        return micEnabled
    }

    // Helper functions.
    private fun toggleCallControlFragmentVisibility() {
        if (!iceConnected || !callFragment!!.isAdded) {
            return
        }
        // Show/hide call control fragment
        callControlFragmentVisible = !callControlFragmentVisible
        val ft = fragmentManager.beginTransaction()
        if (callControlFragmentVisible) {
            ft.show(callFragment)
            ft.show(hudFragment)
        } else {
            ft.hide(callFragment)
            ft.hide(hudFragment)
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        ft.commit()
    }

    private fun startCall() {
        if (appRtcClient == null) {
            Log.e(TAG, "AppRTC client is not allocated for a call.")
            return
        }
        callStartedTimeMs = System.currentTimeMillis()

        // Start room connection.
        logAndToast(getString(R.string.connecting_to, roomConnectionParameters!!.roomUrl))
        appRtcClient!!.connectToRoom(roomConnectionParameters)

        // Create and audio manager that will take care of audio routing,
        // audio modes, audio device enumeration etc.
        audioManager = AppRTCAudioManager.create(applicationContext)
        // Store existing audio settings and change audio mode to
        // MODE_IN_COMMUNICATION for best possible VoIP performance.
        Log.d(TAG, "Starting the audio manager...")
        audioManager!!.start { audioDevice, availableAudioDevices ->
            // This method will be called each time the number of available audio
            // devices has changed.
            onAudioManagerDevicesChanged(audioDevice, availableAudioDevices)
        }
    }

    // Should be called from UI thread
    private fun callConnected() {
        val delta = System.currentTimeMillis() - callStartedTimeMs
        Log.i(TAG, "Call connected: delay=" + delta + "ms")
        if (peerConnectionClient == null || isError) {
            Log.w(TAG, "Call is connected in closed or error state")
            return
        }
        // Enable statistics callback.
        peerConnectionClient!!.enableStatsEvents(true, STAT_CALLBACK_PERIOD)
        setSwappedFeeds(false /* isSwappedFeeds */)
    }

    // This method is called when the audio manager reports audio device change,
    // e.g. from wired headset to speakerphone.
    private fun onAudioManagerDevicesChanged(
        device: AudioDevice, availableDevices: Set<AudioDevice>
    ) {
        Log.d(
            TAG, "onAudioManagerDevicesChanged: " + availableDevices + ", "
                    + "selected: " + device
        )
        // TODO(henrika): add callback handler.
    }

    // Disconnect from remote resources, dispose of local resources, and exit.
    private fun disconnect() {
        activityRunning = false
        remoteProxyRenderer.setTarget(null)
        localProxyRenderer.setTarget(null)
        if (appRtcClient != null) {
            appRtcClient!!.disconnectFromRoom()
            appRtcClient = null
        }
        if (peerConnectionClient != null) {
            peerConnectionClient!!.close()
            peerConnectionClient = null
        }
        if (pipRenderer != null) {
            pipRenderer!!.release()
            pipRenderer = null
        }
        if (videoFileRenderer != null) {
            videoFileRenderer!!.release()
            videoFileRenderer = null
        }
        if (fullscreenRenderer != null) {
            fullscreenRenderer!!.release()
            fullscreenRenderer = null
        }
        if (audioManager != null) {
            audioManager!!.stop()
            audioManager = null
        }
        if (iceConnected && !isError) {
            setResult(RESULT_OK)
        } else {
            setResult(RESULT_CANCELED)
        }
        finish()
    }

    private fun disconnectWithErrorMessage(errorMessage: String) {
        if (commandLineRun || !activityRunning) {
            Log.e(TAG, "Critical error: $errorMessage")
            disconnect()
        } else {
            AlertDialog.Builder(this)
                .setTitle(getText(R.string.channel_error_title))
                .setMessage(errorMessage)
                .setCancelable(false)
                .setNeutralButton(
                    R.string.ok
                ) { dialog, id ->
                    dialog.cancel()
                    disconnect()
                }
                .create()
                .show()
        }
    }

    // Log |msg| and Toast about it.
    private fun logAndToast(msg: String) {
        Log.d(TAG, msg)
        if (logToast != null) {
            logToast!!.cancel()
        }
        logToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        logToast!!.show()
    }

    private fun reportError(description: String) {
        runOnUiThread {
            if (!isError) {
                isError = true
                disconnectWithErrorMessage(description)
            }
        }
    }

    private fun createVideoCapturer(): VideoCapturer? {
        var videoCapturer: VideoCapturer? = null
        val videoFileAsCamera = intent.getStringExtra(EXTRA_VIDEO_FILE_AS_CAMERA)
        if (videoFileAsCamera != null) {
            try {
                videoCapturer = FileVideoCapturer(videoFileAsCamera)
            } catch (e: IOException) {
                reportError("Failed to open video file for emulated camera")
                return null
            }

        } else if (screencaptureEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return createScreenCapturer()
        } else if (useCamera2()) {
            if (!captureToTexture()) {
                reportError(getString(R.string.camera2_texture_only_error))
                return null
            }

            Logging.d(TAG, "Creating capturer using camera2 API.")
            videoCapturer = createCameraCapturer(Camera2Enumerator(this))
        } else {
            Logging.d(TAG, "Creating capturer using camera1 API.")
            videoCapturer = createCameraCapturer(Camera1Enumerator(captureToTexture()))
        }
        if (videoCapturer == null) {
            reportError("Failed to open camera")
            return null
        }
        return videoCapturer
    }

    private fun setSwappedFeeds(isSwappedFeeds: Boolean) {
        Logging.d(TAG, "setSwappedFeeds: $isSwappedFeeds")
        this.isSwappedFeeds = isSwappedFeeds
        localProxyRenderer.setTarget(if (isSwappedFeeds) fullscreenRenderer else pipRenderer)
        remoteProxyRenderer.setTarget(if (isSwappedFeeds) pipRenderer else fullscreenRenderer)
        fullscreenRenderer!!.setMirror(isSwappedFeeds)
        pipRenderer!!.setMirror(!isSwappedFeeds)
    }

    // -----Implementation of AppRTCClient.AppRTCSignalingEvents ---------------
    // All callbacks are invoked from websocket signaling looper thread and
    // are routed to UI thread.
    private fun onConnectedToRoomInternal(params: SignalingParameters) {
        val delta = System.currentTimeMillis() - callStartedTimeMs

        signalingParameters = params
        logAndToast("Creating peer connection, delay=" + delta + "ms")
        var videoCapturer: VideoCapturer? = null
        if (peerConnectionParameters!!.videoCallEnabled) {
            videoCapturer = createVideoCapturer()
        }
        peerConnectionClient!!.createPeerConnection(
            rootEglBase!!.eglBaseContext, localProxyRenderer,
            remoteRenderers, videoCapturer, signalingParameters
        )

        if (signalingParameters!!.initiator) {
            logAndToast("Creating OFFER...")
            // Create offer. Offer SDP will be sent to answering client in
            // PeerConnectionEvents.onLocalDescription event.
            peerConnectionClient!!.createOffer()
        } else {
            if (params.offerSdp != null) {
                peerConnectionClient!!.setRemoteDescription(params.offerSdp)
                logAndToast("Creating ANSWER...")
                // Create answer. Answer SDP will be sent to offering client in
                // PeerConnectionEvents.onLocalDescription event.
                peerConnectionClient!!.createAnswer()
            }
            if (params.iceCandidates != null) {
                // Add remote ICE candidates from room.
                for (iceCandidate in params.iceCandidates) {
                    peerConnectionClient!!.addRemoteIceCandidate(iceCandidate)
                }
            }
        }
    }

    override fun onConnectedToRoom(params: SignalingParameters) {
        runOnUiThread { onConnectedToRoomInternal(params) }
    }

    override fun onRemoteDescription(sdp: SessionDescription) {
        val delta = System.currentTimeMillis() - callStartedTimeMs
        runOnUiThread(Runnable {
            if (peerConnectionClient == null) {
                Log.e(TAG, "Received remote SDP for non-initilized peer connection.")
                return@Runnable
            }
            logAndToast("Received remote " + sdp.type + ", delay=" + delta + "ms")
            peerConnectionClient!!.setRemoteDescription(sdp)
            if (!signalingParameters!!.initiator) {
                logAndToast("Creating ANSWER...")
                // Create answer. Answer SDP will be sent to offering client in
                // PeerConnectionEvents.onLocalDescription event.
                peerConnectionClient!!.createAnswer()
            }
        })
    }

    override fun onRemoteIceCandidate(candidate: IceCandidate) {
        runOnUiThread(Runnable {
            if (peerConnectionClient == null) {
                Log.e(TAG, "Received ICE candidate for a non-initialized peer connection.")
                return@Runnable
            }
            peerConnectionClient!!.addRemoteIceCandidate(candidate)
        })
    }

    override fun onRemoteIceCandidatesRemoved(candidates: Array<IceCandidate>) {
        runOnUiThread(Runnable {
            if (peerConnectionClient == null) {
                Log.e(TAG, "Received ICE candidate removals for a non-initialized peer connection.")
                return@Runnable
            }
            peerConnectionClient!!.removeRemoteIceCandidates(candidates)
        })
    }

    override fun onChannelClose() {
        runOnUiThread {
            logAndToast("Remote end hung up; dropping PeerConnection")
            disconnect()
        }
    }

    override fun onChannelError(description: String) {
        reportError(description)
    }

    // -----Implementation of PeerConnectionClient.PeerConnectionEvents.---------
    // Send local peer connection SDP and ICE candidates to remote party.
    // All callbacks are invoked from peer connection client looper thread and
    // are routed to UI thread.
    override fun onLocalDescription(sdp: SessionDescription) {
        val delta = System.currentTimeMillis() - callStartedTimeMs
        runOnUiThread {
            if (appRtcClient != null) {
                logAndToast("Sending " + sdp.type + ", delay=" + delta + "ms")
                if (signalingParameters!!.initiator) {
                    appRtcClient!!.sendOfferSdp(sdp)
                } else {
                    appRtcClient!!.sendAnswerSdp(sdp)
                }
            }
            if (peerConnectionParameters!!.videoMaxBitrate > 0) {
                Log.d(TAG, "Set video maximum bitrate: " + peerConnectionParameters!!.videoMaxBitrate)
                peerConnectionClient!!.setVideoMaxBitrate(peerConnectionParameters!!.videoMaxBitrate)
            }
        }
    }

    override fun onIceCandidate(candidate: IceCandidate) {
        runOnUiThread {
            if (appRtcClient != null) {
                appRtcClient!!.sendLocalIceCandidate(candidate)
            }
        }
    }

    override fun onIceCandidatesRemoved(candidates: Array<IceCandidate>) {
        runOnUiThread {
            if (appRtcClient != null) {
                appRtcClient!!.sendLocalIceCandidateRemovals(candidates)
            }
        }
    }

    override fun onIceConnected() {
        val delta = System.currentTimeMillis() - callStartedTimeMs
        runOnUiThread {
            logAndToast("ICE connected, delay=" + delta + "ms")
            iceConnected = true
            callConnected()
        }
    }

    override fun onIceDisconnected() {
        runOnUiThread {
            logAndToast("ICE disconnected")
            iceConnected = false
            disconnect()
        }
    }

    override fun onPeerConnectionClosed() {}

    override fun onPeerConnectionStatsReady(reports: Array<StatsReport>) {
        runOnUiThread {
            if (!isError && iceConnected) {
                hudFragment!!.updateEncoderStatistics(reports)
            }
        }
    }

    override fun onPeerConnectionError(description: String) {
        reportError(description)
    }

    companion object {
        private val TAG = CallActivity::class.java.simpleName

        val EXTRA_ROOMID = "org.appspot.apprtc.ROOMID"
        val EXTRA_URLPARAMETERS = "org.appspot.apprtc.URLPARAMETERS"
        val EXTRA_LOOPBACK = "org.appspot.apprtc.LOOPBACK"
        val EXTRA_VIDEO_CALL = "org.appspot.apprtc.VIDEO_CALL"
        val EXTRA_SCREENCAPTURE = "org.appspot.apprtc.SCREENCAPTURE"
        val EXTRA_CAMERA2 = "org.appspot.apprtc.CAMERA2"
        val EXTRA_VIDEO_WIDTH = "org.appspot.apprtc.VIDEO_WIDTH"
        val EXTRA_VIDEO_HEIGHT = "org.appspot.apprtc.VIDEO_HEIGHT"
        val EXTRA_VIDEO_FPS = "org.appspot.apprtc.VIDEO_FPS"
        val EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED = "org.appsopt.apprtc.VIDEO_CAPTUREQUALITYSLIDER"
        val EXTRA_VIDEO_BITRATE = "org.appspot.apprtc.VIDEO_BITRATE"
        val EXTRA_VIDEOCODEC = "org.appspot.apprtc.VIDEOCODEC"
        val EXTRA_HWCODEC_ENABLED = "org.appspot.apprtc.HWCODEC"
        val EXTRA_CAPTURETOTEXTURE_ENABLED = "org.appspot.apprtc.CAPTURETOTEXTURE"
        val EXTRA_FLEXFEC_ENABLED = "org.appspot.apprtc.FLEXFEC"
        val EXTRA_AUDIO_BITRATE = "org.appspot.apprtc.AUDIO_BITRATE"
        val EXTRA_AUDIOCODEC = "org.appspot.apprtc.AUDIOCODEC"
        val EXTRA_NOAUDIOPROCESSING_ENABLED = "org.appspot.apprtc.NOAUDIOPROCESSING"
        val EXTRA_AECDUMP_ENABLED = "org.appspot.apprtc.AECDUMP"
        val EXTRA_OPENSLES_ENABLED = "org.appspot.apprtc.OPENSLES"
        val EXTRA_DISABLE_BUILT_IN_AEC = "org.appspot.apprtc.DISABLE_BUILT_IN_AEC"
        val EXTRA_DISABLE_BUILT_IN_AGC = "org.appspot.apprtc.DISABLE_BUILT_IN_AGC"
        val EXTRA_DISABLE_BUILT_IN_NS = "org.appspot.apprtc.DISABLE_BUILT_IN_NS"
        val EXTRA_ENABLE_LEVEL_CONTROL = "org.appspot.apprtc.ENABLE_LEVEL_CONTROL"
        val EXTRA_DISABLE_WEBRTC_AGC_AND_HPF = "org.appspot.apprtc.DISABLE_WEBRTC_GAIN_CONTROL"
        val EXTRA_DISPLAY_HUD = "org.appspot.apprtc.DISPLAY_HUD"
        val EXTRA_TRACING = "org.appspot.apprtc.TRACING"
        val EXTRA_CMDLINE = "org.appspot.apprtc.CMDLINE"
        val EXTRA_RUNTIME = "org.appspot.apprtc.RUNTIME"
        val EXTRA_VIDEO_FILE_AS_CAMERA = "org.appspot.apprtc.VIDEO_FILE_AS_CAMERA"
        val EXTRA_SAVE_REMOTE_VIDEO_TO_FILE = "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE"
        val EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH = "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE_WIDTH"
        val EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT = "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT"
        val EXTRA_USE_VALUES_FROM_INTENT = "org.appspot.apprtc.USE_VALUES_FROM_INTENT"
        val EXTRA_DATA_CHANNEL_ENABLED = "org.appspot.apprtc.DATA_CHANNEL_ENABLED"
        val EXTRA_ORDERED = "org.appspot.apprtc.ORDERED"
        val EXTRA_MAX_RETRANSMITS_MS = "org.appspot.apprtc.MAX_RETRANSMITS_MS"
        val EXTRA_MAX_RETRANSMITS = "org.appspot.apprtc.MAX_RETRANSMITS"
        val EXTRA_PROTOCOL = "org.appspot.apprtc.PROTOCOL"
        val EXTRA_NEGOTIATED = "org.appspot.apprtc.NEGOTIATED"
        val EXTRA_ID = "org.appspot.apprtc.ID"

        val EXTRA_NAME = "org.appspot.apprtc.NAME"
        val EXTRA_AVATAR = "org.appspot.apprtc.AVATAR"
        val EXTRA_HAS_ANSWER = "org.appspot.apprtc.HAS_ANSWER"

        fun connectToRoom(
            activity: Activity, roomId: String, hasTimeout: Boolean,
            hasAnswer: Boolean, callingName: String, callingAvatar: String?
        ) {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
            val keyprefResolution = activity.getString(R.string.pref_resolution_key)
            val keyprefFps = activity.getString(R.string.pref_fps_key)
            val keyprefVideoBitrateType = activity.getString(R.string.pref_maxvideobitrate_key)
            val keyprefVideoBitrateValue = activity.getString(R.string.pref_maxvideobitratevalue_key)
            val keyprefAudioBitrateType = activity.getString(R.string.pref_startaudiobitrate_key)
            val keyprefAudioBitrateValue = activity.getString(R.string.pref_startaudiobitratevalue_key)
            val keyprefRoomServerUrl = activity.getString(R.string.pref_room_server_url_key)

            val roomUrl = sharedPref.getString(
                keyprefRoomServerUrl, activity.getString(R.string.pref_room_server_url_default)
            )

            // Get default codecs.
            val videoCodec = sharedPrefGetString(
                activity, sharedPref,
                R.string.pref_videocodec_key,
                R.string.pref_videocodec_default
            )
            val audioCodec = sharedPrefGetString(
                activity, sharedPref,
                R.string.pref_audiocodec_key,
                R.string.pref_audiocodec_default
            )

            // Check HW codec flag.
            val hwCodec = sharedPrefGetBoolean(
                activity, sharedPref,
                R.string.pref_hwcodec_key,
                R.string.pref_hwcodec_default
            )

            // Check Capture to texture.
            val captureToTexture = sharedPrefGetBoolean(
                activity, sharedPref,
                R.string.pref_capturetotexture_key,
                R.string.pref_capturetotexture_default
            )

            // Check FlexFEC.
            val flexfecEnabled = sharedPrefGetBoolean(
                activity, sharedPref,
                R.string.pref_flexfec_key,
                R.string.pref_flexfec_default
            )

            // Check Disable Audio Processing flag.
            val noAudioProcessing = sharedPrefGetBoolean(
                activity, sharedPref,
                R.string.pref_noaudioprocessing_key,
                R.string.pref_noaudioprocessing_default
            )

            // Check Disable Audio Processing flag.
            val aecDump = sharedPrefGetBoolean(
                activity, sharedPref,
                R.string.pref_aecdump_key,
                R.string.pref_aecdump_default
            )

            // Check OpenSL ES enabled flag.
            val useOpenSLES = sharedPrefGetBoolean(
                activity, sharedPref,
                R.string.pref_opensles_key,
                R.string.pref_opensles_default
            )

            // Check Disable built-in AEC flag.
            val disableBuiltInAEC = sharedPrefGetBoolean(
                activity, sharedPref,
                R.string.pref_disable_built_in_aec_key,
                R.string.pref_disable_built_in_aec_default
            )

            // Check Disable built-in AGC flag.
            val disableBuiltInAGC = sharedPrefGetBoolean(
                activity, sharedPref,
                R.string.pref_disable_built_in_agc_key,
                R.string.pref_disable_built_in_agc_default
            )

            // Check Disable built-in NS flag.
            val disableBuiltInNS = sharedPrefGetBoolean(
                activity, sharedPref,
                R.string.pref_disable_built_in_ns_key,
                R.string.pref_disable_built_in_ns_default
            )

            // Check Enable level control.
            val enableLevelControl = sharedPrefGetBoolean(
                activity, sharedPref,
                R.string.pref_enable_level_control_key,
                R.string.pref_enable_level_control_key
            )

            // Check Disable gain control
            val disableWebRtcAGCAndHPF = sharedPrefGetBoolean(
                activity, sharedPref,
                R.string.pref_disable_webrtc_agc_and_hpf_key,
                R.string.pref_disable_webrtc_agc_and_hpf_key
            )

            // Get video resolution from settings.
            var videoWidth = 0
            var videoHeight = 0

            val resolution = sharedPref.getString(
                keyprefResolution,
                activity.getString(R.string.pref_resolution_default)
            )
            val dimensions = resolution.split("[ x]+")
            if (dimensions.size == 2) {
                try {
                    videoWidth = Integer.parseInt(dimensions[0])
                    videoHeight = Integer.parseInt(dimensions[1])
                } catch (e: NumberFormatException) {
                    videoWidth = 0
                    videoHeight = 0
                    Log.e(TAG, "Wrong video resolution setting: " + resolution)
                }
            }

            // Get camera fps from settings.
            var cameraFps = 0

            val fps = sharedPref.getString(keyprefFps, activity.getString(R.string.pref_fps_default))
            val fpsValues = fps.split("[ x]+")
            if (fpsValues.size == 2) {
                try {
                    cameraFps = Integer.parseInt(fpsValues[0])
                } catch (e: NumberFormatException) {
                    cameraFps = 0
                    Log.e(TAG, "Wrong camera fps setting: " + fps)
                }
            }


            // Check capture quality slider flag.
            val captureQualitySlider = sharedPrefGetBoolean(
                activity, sharedPref,
                R.string.pref_capturequalityslider_key,
                R.string.pref_capturequalityslider_default
            )

            // Get video and audio start bitrate.
            var videoStartBitrate = 0

            val bitrateTypeDefault = activity.getString(R.string.pref_maxvideobitrate_default)
            val bitrateType = sharedPref.getString(keyprefVideoBitrateType, bitrateTypeDefault)
            if (bitrateType != bitrateTypeDefault) {
                val bitrateValue = sharedPref.getString(
                    keyprefVideoBitrateValue, activity.getString(R.string.pref_maxvideobitratevalue_default)
                )
                videoStartBitrate = Integer.parseInt(bitrateValue)
            }


            var audioStartBitrate = 0

            val bitrateTypeDefaultAudio = activity.getString(R.string.pref_startaudiobitrate_default)
            val bitrateTypeAudio = sharedPref.getString(keyprefAudioBitrateType, bitrateTypeDefaultAudio)
            if (bitrateTypeAudio != bitrateTypeDefaultAudio) {
                val bitrateValue = sharedPref.getString(
                    keyprefAudioBitrateValue, activity.getString(R.string.pref_startaudiobitratevalue_default)
                )
                audioStartBitrate = Integer.parseInt(bitrateValue)
            }


            // Check statistics display option.
            val displayHud = sharedPrefGetBoolean(
                activity, sharedPref,
                R.string.pref_displayhud_key,
                R.string.pref_displayhud_default
            )

            val tracing = sharedPrefGetBoolean(
                activity, sharedPref,
                R.string.pref_tracing_key,
                R.string.pref_tracing_default
            )

            val dataChannelEnabled = sharedPrefGetBoolean(
                activity, sharedPref,
                R.string.pref_enable_datachannel_key,
                R.string.pref_enable_datachannel_default
            )
            val ordered = sharedPrefGetBoolean(
                activity, sharedPref,
                R.string.pref_ordered_key,
                R.string.pref_ordered_default
            )
            val negotiated = sharedPrefGetBoolean(
                activity, sharedPref,
                R.string.pref_negotiated_key,
                R.string.pref_negotiated_default
            )
            val maxRetrMs = sharedPrefGetInteger(
                activity, sharedPref,
                R.string.pref_max_retransmit_time_ms_key,
                R.string.pref_max_retransmit_time_ms_default
            )
            val maxRetr = sharedPrefGetInteger(
                activity, sharedPref,
                R.string.pref_max_retransmits_key,
                R.string.pref_max_retransmits_default
            )
            val id = sharedPrefGetInteger(
                activity,
                sharedPref,
                R.string.pref_data_id_key,
                R.string.pref_data_id_default
            )

            val protocol = sharedPrefGetString(
                activity, sharedPref,
                R.string.pref_data_protocol_key,
                R.string.pref_data_protocol_default
            )

            // Start AppRTCMobile activity.
            Log.d(TAG, "Connecting to room " + roomId + " at URL " + roomUrl)
            if (validateUrl(activity, roomUrl)) {
                val uri = Uri.parse(roomUrl)
                val intent = Intent(activity, CallActivity::class.java)
                intent.data = uri
                intent.putExtra(EXTRA_ROOMID, roomId)
                intent.putExtra(EXTRA_VIDEO_CALL, true)
                intent.putExtra(EXTRA_SCREENCAPTURE, false)
                intent.putExtra(EXTRA_CAMERA2, true)
                intent.putExtra(EXTRA_VIDEO_WIDTH, videoWidth)
                intent.putExtra(EXTRA_VIDEO_HEIGHT, videoHeight)
                intent.putExtra(EXTRA_VIDEO_FPS, cameraFps)
                intent.putExtra(EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, captureQualitySlider)
                intent.putExtra(EXTRA_VIDEO_BITRATE, videoStartBitrate)
                intent.putExtra(EXTRA_VIDEOCODEC, videoCodec)
                intent.putExtra(EXTRA_HWCODEC_ENABLED, hwCodec)
                intent.putExtra(EXTRA_CAPTURETOTEXTURE_ENABLED, captureToTexture)
                intent.putExtra(EXTRA_FLEXFEC_ENABLED, flexfecEnabled)
                intent.putExtra(EXTRA_NOAUDIOPROCESSING_ENABLED, noAudioProcessing)
                intent.putExtra(EXTRA_AECDUMP_ENABLED, aecDump)
                intent.putExtra(EXTRA_OPENSLES_ENABLED, useOpenSLES)
                intent.putExtra(EXTRA_DISABLE_BUILT_IN_AEC, disableBuiltInAEC)
                intent.putExtra(EXTRA_DISABLE_BUILT_IN_AGC, disableBuiltInAGC)
                intent.putExtra(EXTRA_DISABLE_BUILT_IN_NS, disableBuiltInNS)
                intent.putExtra(EXTRA_ENABLE_LEVEL_CONTROL, enableLevelControl)
                intent.putExtra(EXTRA_DISABLE_WEBRTC_AGC_AND_HPF, disableWebRtcAGCAndHPF)
                intent.putExtra(EXTRA_AUDIO_BITRATE, audioStartBitrate)
                intent.putExtra(EXTRA_AUDIOCODEC, audioCodec)
                intent.putExtra(EXTRA_DISPLAY_HUD, displayHud)
                intent.putExtra(EXTRA_TRACING, tracing)
                if (hasTimeout) {
                    intent.putExtra(EXTRA_RUNTIME, TimeUnit.SECONDS.toMillis(30))
                }
                intent.putExtra(EXTRA_NAME, callingName)
                intent.putExtra(EXTRA_AVATAR, callingAvatar)
                intent.putExtra(EXTRA_HAS_ANSWER, hasAnswer)

                intent.putExtra(EXTRA_DATA_CHANNEL_ENABLED, dataChannelEnabled)

                if (dataChannelEnabled) {
                    intent.putExtra(EXTRA_ORDERED, ordered)
                    intent.putExtra(EXTRA_MAX_RETRANSMITS_MS, maxRetrMs)
                    intent.putExtra(EXTRA_MAX_RETRANSMITS, maxRetr)
                    intent.putExtra(EXTRA_PROTOCOL, protocol)
                    intent.putExtra(EXTRA_NEGOTIATED, negotiated)
                    intent.putExtra(EXTRA_ID, id)
                }

                activity.startActivityForResult(intent, CONNECTION_REQUEST)
            }
        }

        private val CAPTURE_PERMISSION_REQUEST_CODE = 1

        val CONNECTION_REQUEST = 1345

        // List of mandatory application permissions.
        private val MANDATORY_PERMISSIONS = arrayOf(
            "android.permission.MODIFY_AUDIO_SETTINGS",
            "android.permission.RECORD_AUDIO",
            "android.permission.INTERNET"
        )

        // Peer connection statistics callback period in ms.
        private val STAT_CALLBACK_PERIOD = 1000
        private var mediaProjectionPermissionResultData: Intent? = null
        private var mediaProjectionPermissionResultCode: Int = 0


        private val systemUiVisibility: Int
            @TargetApi(19)
            get() {
                var flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    flags = flags or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                }
                return flags
            }

    }

}
