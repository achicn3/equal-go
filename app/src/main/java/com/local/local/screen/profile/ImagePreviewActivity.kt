package com.local.local.screen.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.local.local.R
import kotlinx.android.synthetic.main.activity_picture_preview.*

class ImagePreviewActivity : AppCompatActivity() {
    companion object {
        const val PICTURE_PREVIEW_KEY = "picturePreviewKey"
    }

    private val backImageView by lazy { iv_picturePreview_back }
    private val doneImageView by lazy { iv_picturePreview_done }
    private val fileNameTextView by lazy { tv_picturePreview_fileName }
    private val previewImageView by lazy { iv_picturePreview_picture }
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_preview)
        imageUri = intent.getParcelableExtra(PICTURE_PREVIEW_KEY)
            ?: savedInstanceState?.getParcelable(PICTURE_PREVIEW_KEY)
        if (imageUri != null) {
            fileNameTextView.text = imageUri?.path?.substringAfterLast("/")
            Glide.with(applicationContext).asBitmap().load(imageUri).into(previewImageView)
        }
        backImageView.setOnClickListener { finish() }
        doneImageView.setOnClickListener { onClickDone() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(PICTURE_PREVIEW_KEY, imageUri)
    }

    private fun onClickDone() {
        val intent = Intent().apply {
            putExtra(PICTURE_PREVIEW_KEY, imageUri)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}