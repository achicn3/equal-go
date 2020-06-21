package com.local.local.screen.register

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.util.TypedValue
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import com.local.local.R
import com.local.local.body.UserInfo
import com.local.local.screen.BaseActivity
import com.local.local.screen.MainActivity
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class RegisterActivity : BaseActivity() {
    private val viewModel: RegisterViewModel by viewModel {
        parametersOf(this)
    }

    private var job : Job? = null

    private fun initView() {
        val sexArray = arrayListOf("生理男", "生理女", "不願透漏")
        val sexAdapter by lazy { ArrayAdapter(this, R.layout.register_sex_spinner_item, sexArray) }
        val freqArray = arrayListOf("一週一次", "一週兩~三次", "一週四次以上")
        val freqAdapter by lazy {
            ArrayAdapter(
                this,
                R.layout.register_sex_spinner_item,
                freqArray
            )
        }
        spinner_register_sex.apply {
            adapter = sexAdapter
            setSelection(0)
        }

        spinner_register_freq.apply {
            adapter = freqAdapter
            setSelection(0)
        }

    }

    private fun showView() {
        View.VISIBLE.apply {
            spinner_register_sex.visibility = this
            spinner_register_freq.visibility = this
            viewGroup_register_age.visibility = this
            viewGroup_register_name.visibility = this
            viewGroup_register_sex.visibility = this
            viewGroup_register_freq.visibility = this
            btn_register_send.visibility = this
        }
        et_register_phone.isEnabled = false
        et_register_code.isEnabled = false
    }

    private fun isAgeValid(age : Int) : Boolean
        = age in 6..100

    private fun View.addRipple() = with(TypedValue()) {
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
        setBackgroundResource(resourceId)
    }

    private fun initSendCodeBtn(){
        btn_register_sendCode.addRipple()
        btn_register_sendCode.text = getString(R.string.send_code)
        btn_register_sendCode.isClickable = true
        btn_register_sendCode.isEnabled = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initView()

        btn_register_send.setOnClickListener {
            run {
                val age = et_register_age.text.toString().toInt()
                val phoneNumber = et_register_phone.text.toString().toUniversalPhoneNumber()
                Log.d("phone Number","phone number: $phoneNumber")
                val validAge = isAgeValid(age)
                if(!validAge) {
                    viewGroup_register_age.error = "請輸入正確年紀!"
                    return@setOnClickListener
                }

                val name = et_register_name.text.toString()
                val sex = when(spinner_register_sex.selectedItemPosition){
                    0 ->{
                        getString(R.string.sex_male)
                    }
                    1 ->{
                        getString(R.string.sex_female)
                    }
                    else ->{
                        getString(R.string.sex_unknown)
                    }
                }
                val freq = spinner_register_freq.selectedItemPosition
                UserInfo(phoneNumber, name, sex, age, freq)
            }.let {
                viewModel.onClickSend(it)
            }
        }

        btn_register_confrim.setOnClickListener {
            val code = et_register_code.text.toString()
            viewModel.onClickConfirm(code)
        }

        btn_register_sendCode.setOnClickListener {
            val phoneNumber = et_register_phone.text.toString()
            run {
                isPhoneValid(phoneNumber)
            }.let { isValid ->
                when (isValid) {
                    true -> {
                        Log.d("phone number","phone number: ${phoneNumber.toUniversalPhoneNumber()}")
                        viewModel.onClickSendSmsMessage(phoneNumber.toUniversalPhoneNumber())
                        it.isClickable = false
                        it.isEnabled = false
                        it.setBackgroundColor(Color.GRAY)
                        viewGroup_register_phone.error = null
                        job = GlobalScope.launch(Dispatchers.Main) {
                            for (count in 60 downTo 0) {
                                btn_register_sendCode.text =
                                    getString(R.string.countdown_resend, count)
                                delay(1000)
                            }
                            initSendCodeBtn()
                        }
                    }
                    false -> {
                        viewGroup_register_phone.error =
                            getString(R.string.error_invalid_phone_number)
                    }
                }
            }
        }

        viewModel.eventLiveData.observe(this, Observer { event ->
            event ?: return@Observer
            when (event) {
                is RegisterViewModel.Event.OnSmsSendStart -> {
                    showLoadingMsg()
                }
                is RegisterViewModel.Event.OnSmsSendFinish -> {
                    Toast.makeText(this,"驗證碼發送成功!請留意簡訊",Toast.LENGTH_SHORT).show()
                    dismissLoadingMsg()
                }
                is RegisterViewModel.Event.OnVerificationStart -> {
                    showLoadingMsg()
                }
                is RegisterViewModel.Event.OnVerificationFinish -> {
                    dismissLoadingMsg()
                }
                is RegisterViewModel.Event.OnVerificationSuc -> {
                    showDoneMsg()
                    showView()
                }
                is RegisterViewModel.Event.OnVerificationFail -> {
                    Toast.makeText(this, "驗證失敗!請重新檢查驗證碼是否正確", Toast.LENGTH_SHORT).show()
                }
                is RegisterViewModel.Event.OnRegisterStart -> {
                    showLoadingMsg()
                }
                is RegisterViewModel.Event.OnLRegisterFinish -> {
                    dismissLoadingMsg()
                }
                is RegisterViewModel.Event.OnRegisterSuc -> {
                    showDoneMsg()
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }
                is RegisterViewModel.Event.OnRegisterFail -> {
                    Toast.makeText(this, "註冊失敗!請檢查所有欄位皆有填寫!", Toast.LENGTH_SHORT).show()
                }
                is RegisterViewModel.Event.OnPhoneExisted -> {
                    viewGroup_register_phone.error = getString(R.string.error_phone_existed)
                    initSendCodeBtn()
                    job?.cancel()
                }
                is RegisterViewModel.Event.OnError -> {
                    Toast.makeText(
                        this,
                        getString(R.string.error_unknown_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.also {
                viewModel.onEventConsumed(event)
            }
        })

    }
}