package ro.rachieru.dragos.upit.screens.activities.imagepreview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_image_preview.*
import ro.rachieru.dragos.upit.R
import ro.rachieru.dragos.upit.utils.BUNDLE_IMAGE_URL

/**
 * Upit
 *
 * @author Dragos
 * @since 30.06.2019
 */
class ImagePreviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_image_preview)
        val imageUrl = intent.getStringExtra(BUNDLE_IMAGE_URL)
        Glide.with(applicationContext)
            .load(imageUrl)
            .into(fullscreen_content)
    }

}

fun Context.showImagePreview(imageUrl: String) {
    startActivity(
        Intent(this, ImagePreviewActivity::class.java)
            .putExtra(BUNDLE_IMAGE_URL, imageUrl)
    )
}