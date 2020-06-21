package com.local.local.screen.fragment.dialog

import android.util.Log
import android.util.Patterns
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class BaseDialogFragment() : DialogFragment() {
    companion object {
        private const val LOADING_TAG = "LoadingDialogFragmentTag"
        private const val DONE_TAG = "DoneDialogFragmentTag"
        private const val ERROR_TAG = "ErrorDialogFragmentTag"
    }

    protected fun showLoadingMsg() {
        val activity = activity ?: return
        activity.supportFragmentManager.findFragmentByTag(LOADING_TAG) ?: run {
            LoadingFragment().showNow(activity.supportFragmentManager,
                LOADING_TAG
            )
        }
    }

    private fun isValidFormat(phoneNumber: String): Boolean =
        (phoneNumber[0] == '0') && (phoneNumber[1] == '9') && phoneNumber.length == 10


    /**
     * Return true when phoneNumber is valid
     * else return false
     * */
    fun isPhoneValid(phoneNumber: String): Boolean =
        Patterns.PHONE.matcher(phoneNumber).matches() && isValidFormat(phoneNumber)

    fun String.toUniversalPhoneNumber() = "+886${this.substring(1)}"

    protected fun dismissLoadingMsg() {
        val activity = activity ?: return
        Log.d("dismiss~~","dismiss loading ~~ ${activity.supportFragmentManager.findFragmentByTag(LOADING_TAG) == null}")
        activity.supportFragmentManager.findFragmentByTag(LOADING_TAG)?.let { fragment ->
            Log.d("dismiss","dismiss loading fragment")
            (fragment as? LoadingFragment)?.dismiss()
        }
    }


    protected fun showDoneMsg(msg: String? = null) {
        val activity = activity ?: return
        activity.supportFragmentManager.findFragmentByTag(DONE_TAG) ?: run {
            DoneFragment(msg).showNow(activity.supportFragmentManager,
                DONE_TAG
            )
        }
        GlobalScope.launch(Dispatchers.Main) {
            delay(1200L)
            dismissDoneMsg()
        }
    }

    protected fun showErrorMsg(msg: String = ""){
        val activity = activity ?: return
        activity.supportFragmentManager.findFragmentByTag(DONE_TAG) ?: run {
            ErrorFragment(msg).showNow(activity.supportFragmentManager,
                ERROR_TAG
            )
        }
    }

    protected fun dismissErrorMsg(){
        val activity = activity ?: return
        activity.supportFragmentManager.findFragmentByTag(ERROR_TAG)?.let { fragment ->
            (fragment as? ErrorFragment)?.dismiss()
        }
    }

    protected fun dismissDoneMsg() {
        val activity = activity ?: return
        activity.supportFragmentManager.findFragmentByTag(DONE_TAG)?.let { fragment ->
            (fragment as? DoneFragment)?.dismiss()
        }
    }
}