package com.local.local.screen.fragment.ui.home

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.local.local.R
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {
    companion object {
        const val LOG_TAG = "In Login Fragment"
    }

    private val auth = Firebase.auth.apply {
        setLanguageCode("zh-TW")
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        val activity = activity ?: return
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(LOG_TAG, "signInWithCredential:success")
                    Toast.makeText(context, "登入成功!", Toast.LENGTH_SHORT).show()
                    val user = task.result?.user
                    Log.d(LOG_TAG, "user :$user")
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(LOG_TAG, "signInWithCredential:failure", task.exception)
                    task.exception?.printStackTrace()
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val activity =
            activity ?: return super.onCreateView(inflater, container, savedInstanceState)
        /*val etLoginPhone = view.findViewById<TextInputEditText>(R.id.et_login_phone)
        val btnLoginSendCode = view.findViewById<Button>(R.id.btn_login_sendCode)
        val btnLoginSubmit = view.findViewById<Button>(R.id.btn_login_submit)
        var verificationID: String? = null
        etLoginPhone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewGroup_login_phone.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
        var errorCount = 0

        val loginCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                errorCount += 1
                viewGroup_login_phone.error = ""
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                verificationID = p0
            }

        }
        btnLoginSendCode.setOnClickListener {
            val phoneNumber = etLoginPhone.text.toString()
            val isValid = Patterns.PHONE.matcher(phoneNumber).matches()
            if (isValid) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber, // Phone number to verify
                    60, // Timeout duration
                    TimeUnit.SECONDS, // Unit of timeout
                    activity, // Activity (for callback binding)
                    loginCallback
                ) // OnVerificationStateChangedCallbacks
                //倒數60秒
                GlobalScope.launch(Dispatchers.Main) {
                    btn_login_sendCode.isClickable = false
                    btn_login_sendCode.isEnabled = false
                    btn_login_sendCode.setBackgroundColor(Color.GRAY)
                    for (count in 60 downTo 1) {
                        btn_login_sendCode.text = getString(R.string.login_resend, count)
                        delay(1000)
                    }
                    btn_login_sendCode.isClickable = true
                    btn_login_sendCode.isEnabled = true
                }
            } else {
                viewGroup_login_phone.error = getString(R.string.error_phone_invalid)
            }
        }

        btnLoginSubmit.setOnClickListener {
            val code = et_login_code.text.toString()
            verificationID?.let{ id -> PhoneAuthProvider.getCredential(id,code) }?.apply {
                signInWithPhoneAuthCredential(this)
            }
        }



*/
        return view
    }


}
