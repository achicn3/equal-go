package com.local.local.extensions

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.local.local.R

object Extensions {
    fun TextInputEditText.listenTextAndClearError(parentLayout: TextInputLayout){
        this.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                parentLayout.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                TODO("Not yet implemented")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                TODO("Not yet implemented")
            }

        })
    }

    fun ImageView.loadCircleImage(context: Context,url: String?){
        val cp = CircularProgressDrawable(context)
        cp.strokeWidth = 5f
        cp.centerRadius = 30f
        cp.setColorSchemeColors(R.color.colorGreen)
        cp.start()
        Glide
            .with(context)
            .load(url)
            .apply(RequestOptions().circleCrop())
            .placeholder(cp)
            .into(this)
    }
}