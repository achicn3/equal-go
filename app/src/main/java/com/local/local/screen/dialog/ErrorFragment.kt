package com.local.local.screen.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.local.local.R

class ErrorFragment(private val msg: String) : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = true
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        val view = inflater.inflate(R.layout.fragment_dialogfragment_error, container, false)
        setErrorMsg(msg, view)
        return view
    }

    private fun setErrorMsg(msg: String? = "未知的錯誤", view: View) {
        view.findViewById<TextView>(R.id.tv_error_message).apply {
            text = msg
        }
    }

}