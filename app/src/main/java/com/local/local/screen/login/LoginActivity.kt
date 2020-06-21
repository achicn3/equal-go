package com.local.local.screen.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.lifecycle.Observer
import com.local.local.R
import com.local.local.screen.BaseActivity
import com.local.local.screen.MainActivity
import com.local.local.screen.register.RegisterActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class LoginActivity : BaseActivity() {
    private val viewModel: LoginViewModel by viewModel() {
        parametersOf(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        et_login_phone.setText(viewModel.phoneNumber)

        viewModel.eventLiveData.observe(this, Observer { event ->
            event ?: return@Observer
            when (event) {
                is LoginViewModel.Event.OnLoginStart -> {
                    showLoadingMsg()
                }
                is LoginViewModel.Event.OnLoginFinish -> {
                    dismissLoadingMsg()
                }
                is LoginViewModel.Event.OnLoginSuc -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is LoginViewModel.Event.OnLoginFail ->{
                    Toast.makeText(this,getString(R.string.error_login_failed),Toast.LENGTH_SHORT).show()
                }
                is LoginViewModel.Event.OnSmsSendStart -> {
                    showLoadingMsg()
                }
                is LoginViewModel.Event.OnSmsSendFinish -> {
                    dismissLoadingMsg()
                    showDoneMsg()
                }
                is LoginViewModel.Event.OnError -> {
                    Toast.makeText(this,getString(R.string.error_unknown_error),Toast.LENGTH_SHORT).show()
                }
                is LoginViewModel.Event.OnUserNotRegister ->{
                    viewGroup_login_phone.error = getString(R.string.error_phone_not_register)
                }
            }.also {
                viewModel.onEventConsumed(event)
            }
        })

        btn_login_sendCode.setOnClickListener {
            val phoneNumber = et_login_phone.text.toString()
            if(isPhoneValid(phoneNumber)){
                viewModel.onClickSendSms(phoneNumber.toUniversalPhoneNumber())
            }else{
                viewGroup_login_phone.error = getString(R.string.error_invalid_phone_number)
            }
        }

        btn_login_submit.setOnClickListener {
            val code = et_login_code.text.toString()
            viewModel.onClickLogin(code)
        }

        btn_login_register.setOnClickListener {
            val phoneNumber = et_login_phone.text.toString()
            Intent(this, RegisterActivity::class.java).apply {
                putExtra("phoneNumber", phoneNumber)
                startActivity(this)
            }
        }

    }
}