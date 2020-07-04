package com.local.local.screen.dialog

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.local.local.R

class DoneFragment(private var showMsg: String? = null) : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = true
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        val view = inflater.inflate(R.layout.fragment_dialogfragment_done,container)
        showMsg?.let { setDoneMessage(it,view) }
        return view
    }
    private fun setDoneMessage(msg : String,view: View){
        if(!TextUtils.isEmpty(msg))
            view.findViewById<TextView>(R.id.tv_done_message)?.text = msg
    }

}