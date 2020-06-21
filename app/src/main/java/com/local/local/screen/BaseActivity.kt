package com.local.local.screen

import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.local.local.screen.fragment.DoneFragment
import com.local.local.screen.fragment.LoadingFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class BaseActivity : AppCompatActivity() {
    companion object{
        private const val LOADING_TAG = "LoadingDialogFragmentTag"
        private const val DONE_TAG = "DoneDialogFragmentTag"
    }

    protected fun showLoadingMsg(){
        supportFragmentManager.findFragmentByTag(LOADING_TAG) ?: run{
            LoadingFragment().showNow(supportFragmentManager, LOADING_TAG)
        }
    }

    private fun isValidFormat(phoneNumber: String): Boolean =
        (phoneNumber[0] == '0') && (phoneNumber[1] == '9') && phoneNumber.length == 10

    fun isPhoneValid(phoneNumber: String): Boolean =
        Patterns.PHONE.matcher(phoneNumber).matches() && isValidFormat(phoneNumber)

    fun String.toUniversalPhoneNumber()
            = "+886${this.substring(1)}"

    protected fun dismissLoadingMsg(){
        supportFragmentManager.findFragmentByTag(LOADING_TAG)?.let { fragment ->
            (fragment as? LoadingFragment)?.dismiss()
        }
    }


    protected fun showDoneMsg(){
        supportFragmentManager.findFragmentByTag(DONE_TAG) ?: run{
            DoneFragment().showNow(supportFragmentManager, DONE_TAG)
        }
        GlobalScope.launch(Dispatchers.Main) {
            delay(1250L)
            dismissDoneMsg()
        }
    }

    protected fun dismissDoneMsg(){
        supportFragmentManager.findFragmentByTag(DONE_TAG)?.let { fragment ->
            (fragment as? DoneFragment)?.dismiss()
        }
    }
}