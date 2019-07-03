package ro.rachieru.dragos.upit.screens.activities.job

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import ro.rachieru.dragos.base.BaseActivity
import ro.rachieru.dragos.base.ProgressViewDelegate
import ro.rachieru.dragos.upit.R
import ro.rachieru.dragos.upit.databinding.ActivityJobDetailsBinding
import ro.rachieru.dragos.upit.utils.BUNDLE_RESOURCE_ID
import ro.rachieru.dragos.videocall.CallActivity
import ro.rachierudragos.upitapi.UpitApi
import ro.rachierudragos.upitapi.entities.response.CallResponse
import ro.rachierudragos.upitapi.entities.response.OfferResponse

/**
 * Upit
 *
 * @author Dragos
 * @since 17.06.2019
 */
class JobDetailsActivity : BaseActivity<JobDetailsPresenter>(), ProgressViewDelegate {

    private val VIDEO_CALL_REQUEST_CODE = 2
    private val STORAGE_REQUEST_CODE = 1

    private var jobId: Int = 0
    private lateinit var _binding: ActivityJobDetailsBinding
    private lateinit var callButton: MenuItem

    override fun initPresenter(api: UpitApi): JobDetailsPresenter {
        return JobDetailsPresenter(api, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        jobId = intent.getIntExtra(BUNDLE_RESOURCE_ID, 0)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_job_details)
        setSupportActionBar(_binding.toolbar)
        presenter.getDetails(this, jobId)
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
        presenter.startVideoCall(this, _binding.job!!.createdBy!!)
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
            if (requestCode == VIDEO_CALL_REQUEST_CODE) {
                presenter.startVideoCall(this, _binding.job!!.createdBy!!)
            }
        } else {
            Toast.makeText(this, "Please grant permissions", Toast.LENGTH_LONG).show()
        }
    }

    fun onVideoCallCanStart(data: CallResponse) {
        CallActivity.connectToRoom(
            this,
            data.chatRoom!!,
            true,
            false,
            0
        )
    }

    fun onVideoCallRejected() {
    }

    fun onVideoCallError(message: String?) {
        onError(Throwable(message))
    }

    fun onJobDetails(it: OfferResponse) {
        _binding.job = it
        supportActionBar!!.title = it.title
        _binding.toolbar.title = it.title
        _binding.collapse.title = it.title
        Glide.with(this)
            .load(it.documents?.run {
                get(0).path
            })
            .centerCrop()
            .into(_binding.newsImage)
    }

    override fun showProgress() {
        _binding.progressCircular.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        _binding.progressCircular.visibility = View.GONE
    }
}