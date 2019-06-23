package ro.rachieru.dragos.upit.screens.activities.job

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_job_details.*
import org.appspot.apprtc.CallActivity
import ro.rachieru.dragos.base.BaseActivity
import ro.rachieru.dragos.base.ProgressViewDelegate
import ro.rachieru.dragos.upit.R
import ro.rachieru.dragos.upit.utils.BUNDLE_RESOURCE_ID
import ro.rachierudragos.upitapi.UpitApi
import ro.rachierudragos.upitapi.entities.response.CallResponse
import ro.rachierudragos.upitapi.entities.response.JobsResponse

/**
 * Upit
 *
 * @author Dragos
 * @since 17.06.2019
 */
class JobDetailsActivity : BaseActivity<JobDetailsPresenter>(), ProgressViewDelegate {

    private val VIDEO_CALL_REQUEST_CODE = 2
    private val STORAGE_REQUEST_CODE = 1

    private lateinit var jobId: String
    private var _jobDetails: JobsResponse? = null
    private lateinit var callButton: MenuItem

    override fun initPresenter(api: UpitApi): JobDetailsPresenter {
        return JobDetailsPresenter(api, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        jobId = intent.getStringExtra(BUNDLE_RESOURCE_ID)
        setContentView(R.layout.activity_job_details)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.job_details, menu)
        callButton = menu!!.findItem(R.id.action_call)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_call) {
            presenter.checkVideoCallPermissions(this)
            return true
        } else return super.onOptionsItemSelected(item)
    }

    fun showProgressVideoCall() {
        callButton.isVisible = false
    }

    fun hideProgressVideoCall() {
        callButton.isVisible = true
    }

    fun onVideoCallPermissionAvailable() {
        presenter.startVideoCall(this, _jobDetails!!.userId!!)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            if (requestCode == STORAGE_REQUEST_CODE) {
//                presenter.getFiles(mChatRoomViewEntity.getId())
//            }
            if (requestCode == VIDEO_CALL_REQUEST_CODE) {
                presenter.startVideoCall(this, _jobDetails!!.userId!!)
            }
        } else {
            Toast.makeText(this, "Please grant permissions", Toast.LENGTH_LONG).show()
        }
    }

    fun onVideoCallCanStart(data: CallResponse) {
        val user = data.user!!
        CallActivity.connectToRoom(
            this,
            data.chatRoom!!,
            true,
            false,
            user.name!!,
            user.avatarPath
        )
    }

    fun onVideoCallRejected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun onVideoCallError(message: String?) {
        onError(Throwable(message))
    }

    fun onJobDetails(it: JobsResponse) {
        _jobDetails = it
        supportActionBar!!.title = it.title
    }

    override fun showProgress() {
        progress_circular.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progress_circular.visibility = View.GONE
    }
}