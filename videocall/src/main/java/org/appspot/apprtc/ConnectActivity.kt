/*
 *  Copyright 2014 The WebRTC Project Authors. All rights reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package org.appspot.apprtc

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
import android.widget.*
import org.json.JSONArray
import org.json.JSONException
import java.util.*

/**
 * Handles the initial setup where the user selects which room to join.
 */
class ConnectActivity : Activity() {

    private var connectButton: ImageButton? = null
    private var addFavoriteButton: ImageButton? = null
    private var roomEditText: EditText? = null
    private var roomListView: ListView? = null
    private var sharedPref: SharedPreferences? = null
    private var keyprefVideoCallEnabled: String? = null
    private var keyprefScreencapture: String? = null
    private var keyprefCamera2: String? = null
    private var keyprefResolution: String? = null
    private var keyprefFps: String? = null
    private var keyprefCaptureQualitySlider: String? = null
    private var keyprefVideoBitrateType: String? = null
    private var keyprefVideoBitrateValue: String? = null
    private var keyprefVideoCodec: String? = null
    private var keyprefAudioBitrateType: String? = null
    private var keyprefAudioBitrateValue: String? = null
    private var keyprefAudioCodec: String? = null
    private var keyprefHwCodecAcceleration: String? = null
    private var keyprefCaptureToTexture: String? = null
    private var keyprefFlexfec: String? = null
    private var keyprefNoAudioProcessingPipeline: String? = null
    private var keyprefAecDump: String? = null
    private var keyprefOpenSLES: String? = null
    private var keyprefDisableBuiltInAec: String? = null
    private var keyprefDisableBuiltInAgc: String? = null
    private var keyprefDisableBuiltInNs: String? = null
    private var keyprefEnableLevelControl: String? = null
    private var keyprefDisableWebRtcAGCAndHPF: String? = null
    private var keyprefDisplayHud: String? = null
    private var keyprefTracing: String? = null
    private var keyprefRoomServerUrl: String? = null
    private var keyprefRoom: String? = null
    private var keyprefRoomList: String? = null
    private var roomList: ArrayList<String>? = null
    private var adapter: ArrayAdapter<String>? = null
    private var keyprefEnableDataChannel: String? = null
    private var keyprefOrdered: String? = null
    private var keyprefMaxRetransmitTimeMs: String? = null
    private var keyprefMaxRetransmits: String? = null
    private var keyprefDataProtocol: String? = null
    private var keyprefNegotiated: String? = null
    private var keyprefDataId: String? = null

    private val roomListClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
        val roomId = (view as TextView).text.toString()
        connectToRoom(roomId, false, false, false, 0)
    }

    private val addFavoriteListener = OnClickListener {
        val newRoom = roomEditText!!.text.toString()
        if (newRoom.length > 0 && !roomList!!.contains(newRoom)) {
            adapter!!.add(newRoom)
            adapter!!.notifyDataSetChanged()
        }
    }

    private val connectListener = OnClickListener {
        connectToRoom(
            roomEditText!!.text.toString(),
            false,
            false,
            false,
            0
        )
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get setting keys.
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        keyprefVideoCallEnabled = getString(R.string.pref_videocall_key)
        keyprefScreencapture = getString(R.string.pref_screencapture_key)
        keyprefCamera2 = getString(R.string.pref_camera2_key)
        keyprefResolution = getString(R.string.pref_resolution_key)
        keyprefFps = getString(R.string.pref_fps_key)
        keyprefCaptureQualitySlider = getString(R.string.pref_capturequalityslider_key)
        keyprefVideoBitrateType = getString(R.string.pref_maxvideobitrate_key)
        keyprefVideoBitrateValue = getString(R.string.pref_maxvideobitratevalue_key)
        keyprefVideoCodec = getString(R.string.pref_videocodec_key)
        keyprefHwCodecAcceleration = getString(R.string.pref_hwcodec_key)
        keyprefCaptureToTexture = getString(R.string.pref_capturetotexture_key)
        keyprefFlexfec = getString(R.string.pref_flexfec_key)
        keyprefAudioBitrateType = getString(R.string.pref_startaudiobitrate_key)
        keyprefAudioBitrateValue = getString(R.string.pref_startaudiobitratevalue_key)
        keyprefAudioCodec = getString(R.string.pref_audiocodec_key)
        keyprefNoAudioProcessingPipeline = getString(R.string.pref_noaudioprocessing_key)
        keyprefAecDump = getString(R.string.pref_aecdump_key)
        keyprefOpenSLES = getString(R.string.pref_opensles_key)
        keyprefDisableBuiltInAec = getString(R.string.pref_disable_built_in_aec_key)
        keyprefDisableBuiltInAgc = getString(R.string.pref_disable_built_in_agc_key)
        keyprefDisableBuiltInNs = getString(R.string.pref_disable_built_in_ns_key)
        keyprefEnableLevelControl = getString(R.string.pref_enable_level_control_key)
        keyprefDisableWebRtcAGCAndHPF = getString(R.string.pref_disable_webrtc_agc_and_hpf_key)
        keyprefDisplayHud = getString(R.string.pref_displayhud_key)
        keyprefTracing = getString(R.string.pref_tracing_key)
        keyprefRoomServerUrl = getString(R.string.pref_room_server_url_key)
        keyprefRoom = getString(R.string.pref_room_key)
        keyprefRoomList = getString(R.string.pref_room_list_key)
        keyprefEnableDataChannel = getString(R.string.pref_enable_datachannel_key)
        keyprefOrdered = getString(R.string.pref_ordered_key)
        keyprefMaxRetransmitTimeMs = getString(R.string.pref_max_retransmit_time_ms_key)
        keyprefMaxRetransmits = getString(R.string.pref_max_retransmits_key)
        keyprefDataProtocol = getString(R.string.pref_data_protocol_key)
        keyprefNegotiated = getString(R.string.pref_negotiated_key)
        keyprefDataId = getString(R.string.pref_data_id_key)

        setContentView(R.layout.activity_connect)

        roomEditText = findViewById(R.id.room_edittext) as EditText
        roomEditText!!.setOnEditorActionListener(TextView.OnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                addFavoriteButton!!.performClick()
                return@OnEditorActionListener true
            }
            false
        })
        roomEditText!!.requestFocus()

        roomListView = findViewById(R.id.room_listview) as ListView
        roomListView!!.emptyView = findViewById(android.R.id.empty)
        roomListView!!.onItemClickListener = roomListClickListener
        registerForContextMenu(roomListView)
        connectButton = findViewById(R.id.connect_button) as ImageButton
        connectButton!!.setOnClickListener(connectListener)
        addFavoriteButton = findViewById(R.id.add_favorite_button) as ImageButton
        addFavoriteButton!!.setOnClickListener(addFavoriteListener)

        // If an implicit VIEW intent is launching the app, go directly to that URL.
        val intent = intent
        if ("android.intent.action.VIEW" == intent.action && !commandLineRun) {
            val loopback = intent.getBooleanExtra(CallActivity.EXTRA_LOOPBACK, false)
            val runTimeMs = intent.getIntExtra(CallActivity.EXTRA_RUNTIME, 0)
            val useValuesFromIntent = intent.getBooleanExtra(CallActivity.EXTRA_USE_VALUES_FROM_INTENT, false)
            val room = sharedPref!!.getString(keyprefRoom, "")
            connectToRoom(room, true, loopback, useValuesFromIntent, runTimeMs)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.connect_menu, menu)
        return true
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        if (v.id == R.id.room_listview) {
            val info = menuInfo as AdapterView.AdapterContextMenuInfo
            menu.setHeaderTitle(roomList!![info.position])
            val menuItems = resources.getStringArray(R.array.roomListContextMenu)
            for (i in menuItems.indices) {
                menu.add(Menu.NONE, i, i, menuItems[i])
            }
        } else {
            super.onCreateContextMenu(menu, v, menuInfo)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.itemId == REMOVE_FAVORITE_INDEX) {
            val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
            roomList!!.removeAt(info.position)
            adapter!!.notifyDataSetChanged()
            return true
        }

        return super.onContextItemSelected(item)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items.
        if (item.itemId == R.id.action_settings) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.action_loopback) {
            connectToRoom(null, false, true, false, 0)
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    public override fun onPause() {
        super.onPause()
        val room = roomEditText!!.text.toString()
        val roomListJson = JSONArray(roomList).toString()
        val editor = sharedPref!!.edit()
        editor.putString(keyprefRoom, room)
        editor.putString(keyprefRoomList, roomListJson)
        editor.commit()
    }

    public override fun onResume() {
        super.onResume()
        val room = sharedPref!!.getString(keyprefRoom, "")
        roomEditText!!.setText(room)
        roomList = ArrayList()
        val roomListJson = sharedPref!!.getString(keyprefRoomList, null)
        if (roomListJson != null) {
            try {
                val jsonArray = JSONArray(roomListJson)
                for (i in 0 until jsonArray.length()) {
                    roomList!!.add(jsonArray.get(i).toString())
                }
            } catch (e: JSONException) {
                Log.e(TAG, "Failed to load room list: $e")
            }

        }
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, roomList!!)
        roomListView!!.adapter = adapter
        if (adapter!!.count > 0) {
            roomListView!!.requestFocus()
            roomListView!!.setItemChecked(0, true)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == CONNECTION_REQUEST && commandLineRun) {
            Log.d(TAG, "Return: $resultCode")
            setResult(resultCode)
            commandLineRun = false
            finish()
        }
    }

    /**
     * Get a value from the shared preference or from the intent, if it does not
     * exist the default is used.
     */
    private fun sharedPrefGetString(
        attributeId: Int, intentName: String, defaultId: Int, useFromIntent: Boolean
    ): String {
        val defaultValue = getString(defaultId)
        if (useFromIntent) {
            val value = intent.getStringExtra(intentName)
            return value ?: defaultValue
        } else {
            val attributeName = getString(attributeId)
            return sharedPref!!.getString(attributeName, defaultValue)
        }
    }

    /**
     * Get a value from the shared preference or from the intent, if it does not
     * exist the default is used.
     */
    private fun sharedPrefGetBoolean(
        attributeId: Int, intentName: String, defaultId: Int, useFromIntent: Boolean
    ): Boolean {
        val defaultValue = java.lang.Boolean.valueOf(getString(defaultId))
        if (useFromIntent) {
            return intent.getBooleanExtra(intentName, defaultValue)
        } else {
            val attributeName = getString(attributeId)
            return sharedPref!!.getBoolean(attributeName, defaultValue)
        }
    }

    /**
     * Get a value from the shared preference or from the intent, if it does not
     * exist the default is used.
     */
    private fun sharedPrefGetInteger(
        attributeId: Int, intentName: String, defaultId: Int, useFromIntent: Boolean
    ): Int {
        val defaultString = getString(defaultId)
        val defaultValue = Integer.parseInt(defaultString)
        if (useFromIntent) {
            return intent.getIntExtra(intentName, defaultValue)
        } else {
            val attributeName = getString(attributeId)
            val value = sharedPref!!.getString(attributeName, defaultString)
            try {
                return Integer.parseInt(value)
            } catch (e: NumberFormatException) {
                Log.e(TAG, "Wrong setting for: $attributeName:$value")
                return defaultValue
            }

        }
    }

    private fun connectToRoom(
        roomId: String?, commandLineRun: Boolean, loopback: Boolean,
        useValuesFromIntent: Boolean, runTimeMs: Int
    ) {
        var roomId = roomId
        Companion.commandLineRun = commandLineRun

        // roomId is random for loopback.
        if (loopback) {
            roomId = Integer.toString(Random().nextInt(100000000))
        }

        val roomUrl = sharedPref!!.getString(
            keyprefRoomServerUrl, getString(R.string.pref_room_server_url_default)
        )

        // Video call enabled flag.
        val videoCallEnabled = sharedPrefGetBoolean(
            R.string.pref_videocall_key,
            CallActivity.EXTRA_VIDEO_CALL, R.string.pref_videocall_default, useValuesFromIntent
        )

        // Use screencapture option.
        val useScreencapture = sharedPrefGetBoolean(
            R.string.pref_screencapture_key,
            CallActivity.EXTRA_SCREENCAPTURE, R.string.pref_screencapture_default, useValuesFromIntent
        )

        // Use Camera2 option.
        val useCamera2 = sharedPrefGetBoolean(
            R.string.pref_camera2_key, CallActivity.EXTRA_CAMERA2,
            R.string.pref_camera2_default, useValuesFromIntent
        )

        // Get default codecs.
        val videoCodec = sharedPrefGetString(
            R.string.pref_videocodec_key,
            CallActivity.EXTRA_VIDEOCODEC, R.string.pref_videocodec_default, useValuesFromIntent
        )
        val audioCodec = sharedPrefGetString(
            R.string.pref_audiocodec_key,
            CallActivity.EXTRA_AUDIOCODEC, R.string.pref_audiocodec_default, useValuesFromIntent
        )

        // Check HW codec flag.
        val hwCodec = sharedPrefGetBoolean(
            R.string.pref_hwcodec_key,
            CallActivity.EXTRA_HWCODEC_ENABLED, R.string.pref_hwcodec_default, useValuesFromIntent
        )

        // Check Capture to texture.
        val captureToTexture = sharedPrefGetBoolean(
            R.string.pref_capturetotexture_key,
            CallActivity.EXTRA_CAPTURETOTEXTURE_ENABLED, R.string.pref_capturetotexture_default,
            useValuesFromIntent
        )

        // Check FlexFEC.
        val flexfecEnabled = sharedPrefGetBoolean(
            R.string.pref_flexfec_key,
            CallActivity.EXTRA_FLEXFEC_ENABLED, R.string.pref_flexfec_default, useValuesFromIntent
        )

        // Check Disable Audio Processing flag.
        val noAudioProcessing = sharedPrefGetBoolean(
            R.string.pref_noaudioprocessing_key,
            CallActivity.EXTRA_NOAUDIOPROCESSING_ENABLED, R.string.pref_noaudioprocessing_default,
            useValuesFromIntent
        )

        // Check Disable Audio Processing flag.
        val aecDump = sharedPrefGetBoolean(
            R.string.pref_aecdump_key,
            CallActivity.EXTRA_AECDUMP_ENABLED, R.string.pref_aecdump_default, useValuesFromIntent
        )

        // Check OpenSL ES enabled flag.
        val useOpenSLES = sharedPrefGetBoolean(
            R.string.pref_opensles_key,
            CallActivity.EXTRA_OPENSLES_ENABLED, R.string.pref_opensles_default, useValuesFromIntent
        )

        // Check Disable built-in AEC flag.
        val disableBuiltInAEC = sharedPrefGetBoolean(
            R.string.pref_disable_built_in_aec_key,
            CallActivity.EXTRA_DISABLE_BUILT_IN_AEC, R.string.pref_disable_built_in_aec_default,
            useValuesFromIntent
        )

        // Check Disable built-in AGC flag.
        val disableBuiltInAGC = sharedPrefGetBoolean(
            R.string.pref_disable_built_in_agc_key,
            CallActivity.EXTRA_DISABLE_BUILT_IN_AGC, R.string.pref_disable_built_in_agc_default,
            useValuesFromIntent
        )

        // Check Disable built-in NS flag.
        val disableBuiltInNS = sharedPrefGetBoolean(
            R.string.pref_disable_built_in_ns_key,
            CallActivity.EXTRA_DISABLE_BUILT_IN_NS, R.string.pref_disable_built_in_ns_default,
            useValuesFromIntent
        )

        // Check Enable level control.
        val enableLevelControl = sharedPrefGetBoolean(
            R.string.pref_enable_level_control_key,
            CallActivity.EXTRA_ENABLE_LEVEL_CONTROL, R.string.pref_enable_level_control_key,
            useValuesFromIntent
        )

        // Check Disable gain control
        val disableWebRtcAGCAndHPF = sharedPrefGetBoolean(
            R.string.pref_disable_webrtc_agc_and_hpf_key, CallActivity.EXTRA_DISABLE_WEBRTC_AGC_AND_HPF,
            R.string.pref_disable_webrtc_agc_and_hpf_key, useValuesFromIntent
        )

        // Get video resolution from settings.
        var videoWidth = 0
        var videoHeight = 0
        if (useValuesFromIntent) {
            videoWidth = intent.getIntExtra(CallActivity.EXTRA_VIDEO_WIDTH, 0)
            videoHeight = intent.getIntExtra(CallActivity.EXTRA_VIDEO_HEIGHT, 0)
        }
        if (videoWidth == 0 && videoHeight == 0) {
            val resolution = sharedPref!!.getString(keyprefResolution, getString(R.string.pref_resolution_default))
            val dimensions = resolution!!.split("[ x]+".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            if (dimensions.size == 2) {
                try {
                    videoWidth = Integer.parseInt(dimensions[0])
                    videoHeight = Integer.parseInt(dimensions[1])
                } catch (e: NumberFormatException) {
                    videoWidth = 0
                    videoHeight = 0
                    Log.e(TAG, "Wrong video resolution setting: $resolution")
                }

            }
        }

        // Get camera fps from settings.
        var cameraFps = 0
        if (useValuesFromIntent) {
            cameraFps = intent.getIntExtra(CallActivity.EXTRA_VIDEO_FPS, 0)
        }
        if (cameraFps == 0) {
            val fps = sharedPref!!.getString(keyprefFps, getString(R.string.pref_fps_default))
            val fpsValues = fps!!.split("[ x]+".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            if (fpsValues.size == 2) {
                try {
                    cameraFps = Integer.parseInt(fpsValues[0])
                } catch (e: NumberFormatException) {
                    cameraFps = 0
                    Log.e(TAG, "Wrong camera fps setting: $fps")
                }

            }
        }

        // Check capture quality slider flag.
        val captureQualitySlider = sharedPrefGetBoolean(
            R.string.pref_capturequalityslider_key,
            CallActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED,
            R.string.pref_capturequalityslider_default, useValuesFromIntent
        )

        // Get video and audio start bitrate.
        var videoStartBitrate = 0
        if (useValuesFromIntent) {
            videoStartBitrate = intent.getIntExtra(CallActivity.EXTRA_VIDEO_BITRATE, 0)
        }
        if (videoStartBitrate == 0) {
            val bitrateTypeDefault = getString(R.string.pref_maxvideobitrate_default)
            val bitrateType = sharedPref!!.getString(keyprefVideoBitrateType, bitrateTypeDefault)
            if (bitrateType != bitrateTypeDefault) {
                val bitrateValue = sharedPref!!.getString(
                    keyprefVideoBitrateValue, getString(R.string.pref_maxvideobitratevalue_default)
                )
                videoStartBitrate = Integer.parseInt(bitrateValue)
            }
        }

        var audioStartBitrate = 0
        if (useValuesFromIntent) {
            audioStartBitrate = intent.getIntExtra(CallActivity.EXTRA_AUDIO_BITRATE, 0)
        }
        if (audioStartBitrate == 0) {
            val bitrateTypeDefault = getString(R.string.pref_startaudiobitrate_default)
            val bitrateType = sharedPref!!.getString(keyprefAudioBitrateType, bitrateTypeDefault)
            if (bitrateType != bitrateTypeDefault) {
                val bitrateValue = sharedPref!!.getString(
                    keyprefAudioBitrateValue, getString(R.string.pref_startaudiobitratevalue_default)
                )
                audioStartBitrate = Integer.parseInt(bitrateValue)
            }
        }

        // Check statistics display option.
        val displayHud = sharedPrefGetBoolean(
            R.string.pref_displayhud_key,
            CallActivity.EXTRA_DISPLAY_HUD, R.string.pref_displayhud_default, useValuesFromIntent
        )

        val tracing = sharedPrefGetBoolean(
            R.string.pref_tracing_key, CallActivity.EXTRA_TRACING,
            R.string.pref_tracing_default, useValuesFromIntent
        )

        // Get datachannel options
        val dataChannelEnabled = sharedPrefGetBoolean(
            R.string.pref_enable_datachannel_key,
            CallActivity.EXTRA_DATA_CHANNEL_ENABLED, R.string.pref_enable_datachannel_default,
            useValuesFromIntent
        )
        val ordered = sharedPrefGetBoolean(
            R.string.pref_ordered_key, CallActivity.EXTRA_ORDERED,
            R.string.pref_ordered_default, useValuesFromIntent
        )
        val negotiated = sharedPrefGetBoolean(
            R.string.pref_negotiated_key,
            CallActivity.EXTRA_NEGOTIATED, R.string.pref_negotiated_default, useValuesFromIntent
        )
        val maxRetrMs = sharedPrefGetInteger(
            R.string.pref_max_retransmit_time_ms_key,
            CallActivity.EXTRA_MAX_RETRANSMITS_MS, R.string.pref_max_retransmit_time_ms_default,
            useValuesFromIntent
        )
        val maxRetr = sharedPrefGetInteger(
            R.string.pref_max_retransmits_key, CallActivity.EXTRA_MAX_RETRANSMITS,
            R.string.pref_max_retransmits_default, useValuesFromIntent
        )
        val id = sharedPrefGetInteger(
            R.string.pref_data_id_key, CallActivity.EXTRA_ID,
            R.string.pref_data_id_default, useValuesFromIntent
        )
        val protocol = sharedPrefGetString(
            R.string.pref_data_protocol_key,
            CallActivity.EXTRA_PROTOCOL, R.string.pref_data_protocol_default, useValuesFromIntent
        )

        // Start AppRTCMobile activity.
        Log.d(TAG, "Connecting to room $roomId at URL $roomUrl")
        if (validateUrl(roomUrl)) {
            val uri = Uri.parse(roomUrl)
            val intent = Intent(this, CallActivity::class.java)
            intent.data = uri
            intent.putExtra(CallActivity.EXTRA_ROOMID, roomId)
            intent.putExtra(CallActivity.EXTRA_LOOPBACK, loopback)
            intent.putExtra(CallActivity.EXTRA_VIDEO_CALL, videoCallEnabled)
            intent.putExtra(CallActivity.EXTRA_SCREENCAPTURE, useScreencapture)
            intent.putExtra(CallActivity.EXTRA_CAMERA2, useCamera2)
            intent.putExtra(CallActivity.EXTRA_VIDEO_WIDTH, videoWidth)
            intent.putExtra(CallActivity.EXTRA_VIDEO_HEIGHT, videoHeight)
            intent.putExtra(CallActivity.EXTRA_VIDEO_FPS, cameraFps)
            intent.putExtra(CallActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, captureQualitySlider)
            intent.putExtra(CallActivity.EXTRA_VIDEO_BITRATE, videoStartBitrate)
            intent.putExtra(CallActivity.EXTRA_VIDEOCODEC, videoCodec)
            intent.putExtra(CallActivity.EXTRA_HWCODEC_ENABLED, hwCodec)
            intent.putExtra(CallActivity.EXTRA_CAPTURETOTEXTURE_ENABLED, captureToTexture)
            intent.putExtra(CallActivity.EXTRA_FLEXFEC_ENABLED, flexfecEnabled)
            intent.putExtra(CallActivity.EXTRA_NOAUDIOPROCESSING_ENABLED, noAudioProcessing)
            intent.putExtra(CallActivity.EXTRA_AECDUMP_ENABLED, aecDump)
            intent.putExtra(CallActivity.EXTRA_OPENSLES_ENABLED, useOpenSLES)
            intent.putExtra(CallActivity.EXTRA_DISABLE_BUILT_IN_AEC, disableBuiltInAEC)
            intent.putExtra(CallActivity.EXTRA_DISABLE_BUILT_IN_AGC, disableBuiltInAGC)
            intent.putExtra(CallActivity.EXTRA_DISABLE_BUILT_IN_NS, disableBuiltInNS)
            intent.putExtra(CallActivity.EXTRA_ENABLE_LEVEL_CONTROL, enableLevelControl)
            intent.putExtra(CallActivity.EXTRA_DISABLE_WEBRTC_AGC_AND_HPF, disableWebRtcAGCAndHPF)
            intent.putExtra(CallActivity.EXTRA_AUDIO_BITRATE, audioStartBitrate)
            intent.putExtra(CallActivity.EXTRA_AUDIOCODEC, audioCodec)
            intent.putExtra(CallActivity.EXTRA_DISPLAY_HUD, displayHud)
            intent.putExtra(CallActivity.EXTRA_TRACING, tracing)
            intent.putExtra(CallActivity.EXTRA_CMDLINE, commandLineRun)
            intent.putExtra(CallActivity.EXTRA_RUNTIME, runTimeMs)

            intent.putExtra(CallActivity.EXTRA_DATA_CHANNEL_ENABLED, dataChannelEnabled)

            if (dataChannelEnabled) {
                intent.putExtra(CallActivity.EXTRA_ORDERED, ordered)
                intent.putExtra(CallActivity.EXTRA_MAX_RETRANSMITS_MS, maxRetrMs)
                intent.putExtra(CallActivity.EXTRA_MAX_RETRANSMITS, maxRetr)
                intent.putExtra(CallActivity.EXTRA_PROTOCOL, protocol)
                intent.putExtra(CallActivity.EXTRA_NEGOTIATED, negotiated)
                intent.putExtra(CallActivity.EXTRA_ID, id)
            }

            if (useValuesFromIntent) {
                if (getIntent().hasExtra(CallActivity.EXTRA_VIDEO_FILE_AS_CAMERA)) {
                    val videoFileAsCamera = getIntent().getStringExtra(CallActivity.EXTRA_VIDEO_FILE_AS_CAMERA)
                    intent.putExtra(CallActivity.EXTRA_VIDEO_FILE_AS_CAMERA, videoFileAsCamera)
                }

                if (getIntent().hasExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE)) {
                    val saveRemoteVideoToFile = getIntent().getStringExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE)
                    intent.putExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE, saveRemoteVideoToFile)
                }

                if (getIntent().hasExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH)) {
                    val videoOutWidth = getIntent().getIntExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, 0)
                    intent.putExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, videoOutWidth)
                }

                if (getIntent().hasExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT)) {
                    val videoOutHeight = getIntent().getIntExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, 0)
                    intent.putExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, videoOutHeight)
                }
            }

            startActivityForResult(intent, CONNECTION_REQUEST)
        }
    }

    private fun validateUrl(url: String): Boolean {
        if (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url)) {
            return true
        }

        AlertDialog.Builder(this)
            .setTitle(getText(R.string.invalid_url_title))
            .setMessage(getString(R.string.invalid_url_text, url))
            .setCancelable(false)
            .setNeutralButton(
                R.string.ok
            ) { dialog, id -> dialog.cancel() }
            .create()
            .show()
        return false
    }

    companion object {
        private val TAG = "ConnectActivity"
        private val CONNECTION_REQUEST = 1
        private val REMOVE_FAVORITE_INDEX = 0
        private var commandLineRun = false
    }
}
