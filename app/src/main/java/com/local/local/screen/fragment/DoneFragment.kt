package com.local.local.screen.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.local.local.R

class DoneFragment() : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = false
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.fragment_dialogfragment_done,container)
    }
    fun setDoneMessage(msg : String){
        if(!TextUtils.isEmpty(msg))
            view?.findViewById<TextView>(R.id.tv_done_message)?.text = msg
    }

}