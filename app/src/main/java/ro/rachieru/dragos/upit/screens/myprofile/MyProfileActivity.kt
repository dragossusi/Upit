package ro.rachieru.dragos.upit.screens.myprofile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.theartofdev.edmodo.cropper.CropImage
import ro.rachieru.dragos.base.BaseActivity
import ro.rachieru.dragos.base.ProgressViewDelegate
import ro.rachieru.dragos.base.extensions.gone
import ro.rachieru.dragos.base.extensions.visible
import ro.rachieru.dragos.upit.R
import ro.rachieru.dragos.upit.databinding.ActivityMyProfileBinding
import ro.rachieru.dragos.upit.screens.activities.imagepreview.showImagePreview
import ro.rachierudragos.upitapi.UpitApi
import ro.rachierudragos.upitapi.UserDetails
import android.net.Uri
import android.view.Menu
import android.view.MenuItem


/**
 * Upit
 *
 * @author Dragos
 * @since 09.06.2019
 */
class MyProfileActivity : BaseActivity<MyProfilePresenter>(), ProgressViewDelegate {

    private lateinit var editMenu: MenuItem
    private lateinit var cancelMenu: MenuItem
    private lateinit var saveMenu: MenuItem
    private lateinit var _binding: ActivityMyProfileBinding

    override fun initPresenter(api: UpitApi): MyProfilePresenter {
        return MyProfilePresenter(api, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_my_profile)
        _binding.imageProfile.setOnClickListener {
            _binding.user?.profilePic?.let { avatar ->
                PopupMenu(this@MyProfileActivity, _binding.imageProfile, Gravity.BOTTOM).apply {
                    inflate(R.menu.profile_avatar)
                    setOnMenuItemClickListener {
                        when (it?.itemId) {
                            R.id.action_view_image -> showImagePreview(avatar)
                            R.id.action_pick_image -> pickImage()
                        }
                        true
                    }
                    show()
                }
            } ?: pickImage()
        }
        _binding.layoutCv.setOnClickListener {
            _binding.user?.cvPath?.let { cvPath ->
                PopupMenu(this@MyProfileActivity, _binding.layoutCv, Gravity.BOTTOM).apply {
                    inflate(R.menu.profile_cv)
                    setOnMenuItemClickListener {
                        when (it?.itemId) {
                            R.id.action_view_cv -> showCv(cvPath)
                            R.id.action_pick_cv -> pickDoc()
                        }
                        true
                    }
                    show()
                }
            } ?: pickDoc()
        }
        presenter.getDetails(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.profile_options, menu)
        editMenu = menu.findItem(R.id.action_edit_profile)
        cancelMenu = menu.findItem(R.id.action_edit_profile)
        saveMenu = menu.findItem(R.id.action_save_profile)
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit_profile -> {
                setEditable(true)
                true
            }
            R.id.action_cancel_edit_profile -> {
                setEditable(true)
                true
            }
            R.id.action_save_profile -> {
                presenter.saveProfile(
                    this,
                    _binding.editFirstName.text.toString(),
                    _binding.editLastName.text.toString()
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun setEditable(editable: Boolean) {
        editMenu.isVisible = editable
        cancelMenu.isVisible = !editable
        saveMenu.isVisible = !editable
//        _binding.editEmail.isEnabled = editable
        _binding.editFirstName.isEnabled = editable
        _binding.editLastName.isEnabled = editable
    }

    private fun showCv(cvPath: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(cvPath))
        startActivity(browserIntent)
    }

    fun pickImage() = CropImage.activity().start(this)

    fun pickDoc() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setType("application/pdf")

        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.choose_cv)),
            REQUEST_CODE_CV
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    val resultUri = result.uri
                    presenter.changeAvatar(this, resultUri)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.error
                    onError(error)
                }
            }
            REQUEST_CODE_CV -> {
                if (resultCode == Activity.RESULT_OK) {
                    val resultUri = data!!.data!!
                    presenter.changeCV(this, resultUri)
                } else {
                    onError(Throwable())
                }
            }
        }
    }

    fun onImageChanged(avatar: String) {
        _binding.user!!.profilePic = avatar
        localSaving.user = _binding.user!!
        _binding.notifyChange()
    }

    fun onCvChanged(cv: Pair<String, String>) {
        _binding.user!!.run {
            cvPath = cv.first
            cvName = cv.second
        }
        localSaving.user = _binding.user!!
        _binding.textCvName.text = if (cv.second.isEmpty()) cv.second else cv.first
        _binding.notifyChange()
    }

    fun onUserDetails(it: UserDetails) {
        _binding.user = it
        Glide.with(this)
            .load(it.profilePic)
            .centerCrop()
            .into(_binding.imageProfile)
        _binding.textCvName.text = it.cvName ?: it.cvPath ?: getString(R.string.upload_cv)
    }

    override fun showProgress() {
        _binding.progressCircular.visible()
    }

    override fun hideProgress() {
        _binding.progressCircular.gone()
    }

    companion object {
        const val REQUEST_CODE_CV = 1
    }
}