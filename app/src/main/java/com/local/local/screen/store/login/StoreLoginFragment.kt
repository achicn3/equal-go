package com.local.local.screen.store.login

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.local.local.R
import com.local.local.extensions.Extensions.listenTextAndClearError
import com.local.local.screen.fragment.dialog.BaseDialogFragment
import com.local.local.screen.store.register.StoreRegisterFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class StoreLoginFragment : BaseDialogFragment() {
    interface Response{
        fun loginResponse(response: Boolean)
    }

    private var response : Response? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        response = context as Response
    }
    companion object{
        private const val RegisterTag = "showRegisterTag"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context: Context = context ?: return super.onCreateDialog(savedInstanceState)
        val activity = activity ?: return super.onCreateDialog(savedInstanceState)
        val viewModel : StoreLoginViewModel by viewModel(){
            parametersOf(activity)
        }
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_store_login, null)
        val btnConfirm = view.findViewById<Button>(R.id.btn_storeLogin_confrim)
        val btnCancel = view.findViewById<Button>(R.id.btn_storeLogin_cancel)
        val etAccount = view.findViewById<TextInputEditText>(R.id.et_storeLogin_account)
        val etPwd = view.findViewById<TextInputEditText>(R.id.et_storeLogin_pwd)
        val btnRegister = view.findViewById<Button>(R.id.btn_storeLogin_register)
        val viewGroupAccount = view.findViewById<TextInputLayout>(R.id.viewGroup_storeLogin_account)
        val viewGroupPwd = view.findViewById<TextInputLayout>(R.id.viewGroup_storeLogin_pwd)
        isCancelable = true
        etAccount.listenTextAndClearError(viewGroupAccount)
        etPwd.listenTextAndClearError(viewGroupPwd)

        btnRegister.setOnClickListener {
            activity.supportFragmentManager.findFragmentByTag(RegisterTag) ?: run{
                StoreRegisterFragment().showNow(activity.supportFragmentManager, RegisterTag)
            }
            dismiss()
        }

        btnConfirm.setOnClickListener {
            val account = etAccount.text.toString()
            val pwd = etPwd.text.toString()
            if (TextUtils.isEmpty(account) || !Patterns.EMAIL_ADDRESS.matcher(account).matches()) {
                viewGroupAccount.error = "請輸入正確的帳號"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(pwd)) {
                viewGroupPwd.error = "請輸入密碼"
                return@setOnClickListener
            }
            viewModel.onClickLogin(account, pwd)
        }



        btnCancel.setOnClickListener {
            dismiss()
        }

        viewModel.eventLiveData.observe(this, Observer { event ->
            event ?: return@Observer
            when(event){
                is StoreLoginViewModel.Event.OnLoginStart ->{
                    showLoadingMsg()
                }
                is StoreLoginViewModel.Event.OnLoginFinish ->{
                    dismissLoadingMsg()
                }
                is StoreLoginViewModel.Event.OnLoginSuc ->{
                    response?.loginResponse(true)
                    dismiss()
                }
                is StoreLoginViewModel.Event.OnLoginFail ->{
                    showErrorMsg("登入失敗")
                }
            }.also {
                viewModel.onEventConsumed(event)
            }
        })

        return MaterialAlertDialogBuilder(context).setView(view).create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_store_login, container, false)
    }
}