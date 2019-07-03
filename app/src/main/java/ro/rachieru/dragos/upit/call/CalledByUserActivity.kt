package ro.rachieru.dragos.upit.call

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.os.PowerManager
import android.os.Vibrator
import android.view.View
import android.view.WindowManager
import android.webkit.URLUtil
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import ro.rachieru.dragos.base.BaseActivity
import ro.rachieru.dragos.upit.R
import ro.rachieru.dragos.videocall.CallActivity
import ro.rachierudragos.upitapi.UpitApi
import ro.rachierudragos.upitapi.entities.response.CallResponse
import ro.rachierudragos.upitapi.entities.response.VideoCallBroadcastManager
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Upit
 *
 * @author Dragos
 * @since 13.06.2019
 */
class CalledByUserActivity : BaseActivity<VideoCallPresenter>(), View.OnClickListener {

    private val TIMEOUT = TimeUnit.SECONDS.toMillis(30)
    private val VIDEO_CALL_REQUEST_CODE = 1

    override fun initPresenter(api: UpitApi): VideoCallPresenter {
        return VideoCallPresenter(api, this)
    }

    companion object {

        private val BUNDLE_USER_ID = "bundle-user-id"
        private val BUNDLE_SENDING_USER_ID = "bundle-sending-user-id"
        private val BUNDLE_RECEIVING_USER_ID = "bundle-receiving-user-id"
        private val BUNDLE_NAME = "bundle-username"
        private val BUNDLE_CHAT_ROOM = "bundle-chat-room"
        private val BUNDLE_AVATAR = "bundle-avatar"

        private val TIMEOUT = TimeUnit.SECONDS.toMillis(30);
        private const val VIDEO_CALL_REQUEST_CODE = 1;

        fun startActivity(
            context: Context,
            userId: String,
            sendingUserId: String,
            receivingUserId: String,
            name: String,
            chatRoom: String,
            avatar: String?
        ) {
            val callingActivity = Intent(context, CalledByUserActivity::class.java)
            callingActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            callingActivity.putExtra(BUNDLE_USER_ID, userId)
            callingActivity.putExtra(BUNDLE_SENDING_USER_ID, sendingUserId)
            callingActivity.putExtra(BUNDLE_RECEIVING_USER_ID, receivingUserId)
            callingActivity.putExtra(BUNDLE_NAME, name)
            callingActivity.putExtra(BUNDLE_CHAT_ROOM, chatRoom)
            callingActivity.putExtra(BUNDLE_AVATAR, avatar)
            context.startActivity(callingActivity)
        }
    }

    private var mVibrator: Vibrator? = null
    private var mWakeLock: PowerManager.WakeLock? = null
    private var mRingtone: Ringtone? = null
    private var mTimer: Timer? = null

    private var mChatRoom: String? = null
    private var mName: String? = null
    private var mAvatar: String? = null

    private lateinit var mAvatarImageView: AppCompatImageView
    private lateinit var mUserFullNameTextView: AppCompatTextView

    private lateinit var mSendingUserId: String
    private lateinit var mReceivingUserId: String

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val state = intent.getStringExtra(VideoCallBroadcastManager.VIDEO_CALL_EXTRA)
            if (state != null && !state!!.isEmpty()) {
                when (state) {
                    VideoCallBroadcastManager.CallStatus.DECLINED -> finishAffinity()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calling)

        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        mRingtone = RingtoneManager.getRingtone(this, uri)
        mRingtone?.play()

        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager?
        if (pm != null) {
            mWakeLock = pm.newWakeLock(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        PowerManager.ACQUIRE_CAUSES_WAKEUP or
                        PowerManager.ON_AFTER_RELEASE,
                this.javaClass.name
            )
            if (mWakeLock != null) {
                window.addFlags(
                    (WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
                )
                mWakeLock!!.acquire(TIMEOUT)
            }
        }

        mVibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val pattern = longArrayOf(
            500, // pause 500 mls
            500, // vibrate 500 mls
            500, // pause 500 mls
            500   // vibrate 500 mls
        )
        if (mVibrator != null && mVibrator!!.hasVibrator()) {
            mVibrator!!.vibrate(pattern, 1) // start from second stage, with vibration
        }

        //mUserId = getIntent().getStringExtra(BUNDLE_USER_ID);
        mChatRoom = intent.getStringExtra(BUNDLE_CHAT_ROOM)
        mName = intent.getStringExtra(BUNDLE_NAME)
        mAvatar = intent.getStringExtra(BUNDLE_AVATAR)
        mSendingUserId = intent.getStringExtra(BUNDLE_SENDING_USER_ID)
        mReceivingUserId = intent.getStringExtra(BUNDLE_RECEIVING_USER_ID)

        mTimer = Timer()
        mTimer!!.schedule(object : TimerTask() {
            override fun run() {
                presenter.cancelVideoCall(this@CalledByUserActivity, mSendingUserId!!, mReceivingUserId!!)
            }
        }, TIMEOUT)

        initView()

        if (URLUtil.isNetworkUrl(mAvatar)) {
            Glide.with(this)
                .load(mAvatar)
                .placeholder(R.drawable.ic_default_user)
                .error(R.drawable.ic_default_user)
                .fitCenter()
                .centerCrop()
                .transform(CircleCrop())
                .into(mAvatarImageView)
        }

        if (mName != null) {
            mUserFullNameTextView.text = getString(R.string.video_call_is_calling, mName)
        }
    }

    override fun onResume() {
        super.onResume()
        VideoCallBroadcastManager.registerBroadcast(this, mBroadcastReceiver)
    }

    override fun onPause() {
        super.onPause()
        VideoCallBroadcastManager.unregisterBroadcast(this, mBroadcastReceiver)
        stop()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == VIDEO_CALL_REQUEST_CODE) {
                connect()
            } else {
                presenter.cancelVideoCall(this, mSendingUserId, mReceivingUserId)
            }
        } else {
            presenter.cancelVideoCall(this, mSendingUserId, mReceivingUserId)
        }
    }

    override fun onClick(v: View) {
        stop()
        when (v.getId()) {
            R.id.button_call_disconnect -> {
                v.setEnabled(false)
                presenter.cancelVideoCall(this, mSendingUserId, mReceivingUserId)
            }

            R.id.button_call_connect -> {
                v.setEnabled(false)
                presenter.checkVideoCallPermissions(v.context)
            }
        }
    }

    fun onVideoCallPermissionAvailable() {
        connect()
    }

    fun onVideoCallRequestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
            ),
            VIDEO_CALL_REQUEST_CODE
        )
    }

    fun onVideoCallCanStart(data: CallResponse) {}

    fun onVideoCallRejected() {
        finishAffinity()
    }

    fun showProgress() {

    }

    fun hideProgress() {

    }

    fun showProgressVideoCall() {

    }

    fun hideProgressVideoCall() {

    }

    fun onError(errorMessage: String) {
        //Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        finishAffinity()
    }

    fun onVideoCallError(error: String) {
        presenter.cancelVideoCall(this, mSendingUserId, mReceivingUserId)
    }

    private fun initView() {
        mAvatarImageView = findViewById(R.id.image_user)
        mUserFullNameTextView = findViewById(R.id.text_user_full_name)
        findViewById<View>(R.id.button_call_disconnect).setOnClickListener(this)
        findViewById<View>(R.id.button_call_connect).setOnClickListener(this)
    }

    private fun connect() {
        finishAffinity()
        CallActivity.connectToRoom(this, mChatRoom!!, false, true,0/*, mName!!, mAvatar*/)
    }

    private fun stop() {
        if (mWakeLock != null && mWakeLock!!.isHeld) {
            mWakeLock!!.release()
        }
        mRingtone?.run {
            if (isPlaying) {
                stop()
            }
        }
        mTimer?.cancel()
        mVibrator?.cancel()
    }

}