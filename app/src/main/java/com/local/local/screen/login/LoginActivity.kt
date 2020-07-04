package com.local.local.screen.login

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.transition.Explode
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.lifecycle.Observer
import com.local.local.R
import com.local.local.extensions.Extensions.listenTextAndClearError
import com.local.local.screen.BaseActivity
import com.local.local.screen.FirstActivity
import com.local.local.screen.MainActivity
import com.local.local.screen.admin.AdminActivity
import com.local.local.screen.store.login.StoreLoginFragment
import com.local.local.screen.register.RegisterActivity
import com.local.local.screen.store.StoreMainActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class LoginActivity : BaseActivity(),StoreLoginFragment.Response {
    private val viewModel: LoginViewModel by viewModel {
        parametersOf(this)
    }

    private fun View.addRipple() = with(TypedValue()) {
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
        setBackgroundResource(resourceId)
    }

    companion object{
        private const val StoreLoginTag = "StoreLoginTag"
    }

    private fun initSendCodeBtn() {
        btn_login_sendCode.addRipple()
        btn_login_sendCode.text = getString(R.string.send_code)
        btn_login_sendCode.isClickable = true
        btn_login_sendCode.isEnabled = true
    }

    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(window) {
            // set an exit transition
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
                exitTransition = Explode()
            }
        }
        setContentView(R.layout.activity_login)

        et_login_phone.setText(viewModel.phoneNumber)
        et_login_phone.listenTextAndClearError(viewGroup_login_phone)
        cb_login_rememberMe.isChecked = viewModel.isRemember

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
                    dismissLoadingMsg()
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
            run {
                isPhoneValid(phoneNumber)
            }.let { isValid ->
                when (isValid) {
                    true -> {
                        viewModel.onClickSendSms(phoneNumber.toUniversalPhoneNumber())
                        it.isClickable = false
                        it.isEnabled = false
                        it.setBackgroundColor(Color.GRAY)
                        viewGroup_login_phone.error = null
                        job = GlobalScope.launch(Dispatchers.Main) {
                            for (count in 60 downTo 0) {
                                btn_login_sendCode.text =
                                    getString(R.string.countdown_resend, count)
                                delay(1000)
                            }
                            initSendCodeBtn()
                        }
                    }
                    false -> {
                        viewGroup_login_phone.error =
                            getString(R.string.error_invalid_phone_number)
                    }
                }
            }
        }

        btn_login_submit.setOnClickListener {
            val code = et_login_code.text.toString()
            val phoneNumber = et_login_phone.text.toString()
            val isRemember = cb_login_rememberMe.isChecked
            if(!TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(code))
                viewModel.onClickLogin(code, phoneNumber, isRemember)
        }

        btn_login_register.setOnClickListener {
            val phoneNumber = et_login_phone.text.toString()
            Intent(this, RegisterActivity::class.java).apply {
                putExtra("phoneNumber", phoneNumber)
                startActivity(this)
            }
        }

        btn_login_storeLogin.setOnClickListener {
            supportFragmentManager.findFragmentByTag(StoreLoginTag) ?: run{
                StoreLoginFragment().apply {
                    showNow(supportFragmentManager, StoreLoginTag)
                }
            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this,FirstActivity::class.java))
    }

    override fun storeLoginResponse(response: Boolean) {
        if(response) {
            startActivity(Intent(this, StoreMainActivity::class.java))
            finish()
        }
        else
            Toast.makeText(this,"登入失敗",Toast.LENGTH_SHORT).show()
    }

    override fun adminLoginResponse(response: Boolean) {
        if(response) {
            Toast.makeText(this,"管理員登入成功",Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, AdminActivity::class.java))

        }else
            Toast.makeText(this,"登入失敗",Toast.LENGTH_SHORT).show()
    }
}