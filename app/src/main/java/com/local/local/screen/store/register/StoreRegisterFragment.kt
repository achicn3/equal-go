package com.local.local.screen.store.register

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.local.local.R
import com.local.local.extensions.Extensions.listenTextAndClearError
import com.local.local.screen.fragment.dialog.BaseDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class StoreRegisterFragment : BaseDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = context ?: return super.onCreateDialog(savedInstanceState)
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_store_register, null)
        val viewModel: StoreRegisterViewModel by viewModel()
        val etStoreName = view.findViewById<TextInputEditText>(R.id.et_storeRegister_storeName)
        val viewGroupName =
            view.findViewById<TextInputLayout>(R.id.viewGroup_storeRegister_storeName)
        etStoreName.listenTextAndClearError(viewGroupName)
        val typeArray = arrayListOf("食", "育", "樂")
        val spinner = view.findViewById<Spinner>(R.id.spinner_storeRegister_type).apply {
            adapter = ArrayAdapter(context, R.layout.register_sex_spinner_item, typeArray)
            setSelection(0)
        }
        val etEmail = view.findViewById<TextInputEditText>(R.id.et_storeRegister_email)
        val etPwd = view.findViewById<TextInputEditText>(R.id.et_storeRegister_pwd)
        val viewGroupEmail = view.findViewById<TextInputLayout>(R.id.viewGroup_storeRegister_email)
        val viewGroupPwd = view.findViewById<TextInputLayout>(R.id.viewGroup_storeRegister_pwd)
        etEmail.listenTextAndClearError(viewGroupEmail)
        etPwd.listenTextAndClearError(viewGroupPwd)
        view.findViewById<Button>(R.id.btn_storeRegister_cancel).setOnClickListener {
            dismiss()
        }
        view.findViewById<Button>(R.id.btn_storeRegister_confrim).setOnClickListener {
            val storeName = etStoreName.text.toString()
            val email = etEmail.text.toString()
            val pwd = etPwd.text.toString()
            if (TextUtils.isEmpty(storeName)) {
                viewGroupName.error = "請輸入店家名稱!"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                viewGroupEmail.error = "請輸入信箱!"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(pwd)) {
                viewGroupPwd.error = "請輸入密碼!"
                return@setOnClickListener
            }
            val type: String = typeArray[spinner.selectedItemPosition]
            viewModel.onClickConfirm(storeName, email, pwd, type)
        }

        viewModel.eventLiveData.observe(this, Observer { event ->
            event ?: return@Observer
            when (event) {
                is StoreRegisterViewModel.Event.OnRegisterStart -> {
                    showLoadingMsg()
                }
                is StoreRegisterViewModel.Event.OnRegisterFinish ->{
                    dismissLoadingMsg()
                }
                is StoreRegisterViewModel.Event.OnWaitingConfirm ->{
                    Log.d("status","This is got send...")
                    showErrorMsg("該帳號等待審核中!請勿重複註冊")
                }
                is StoreRegisterViewModel.Event.OnRegisterInfoSendSuc ->{
                    showDoneMsg("註冊成功!請等待審核結果(約1~2天)")
                }
                is StoreRegisterViewModel.Event.OnRegisterInfoSendFail ->{
                    showErrorMsg("註冊失敗!請檢查網路連線狀況!")
                }
            }.also {
                viewModel.onEventConsumed(event)
            }
        })
        return MaterialAlertDialogBuilder(context).setView(view).create()
    }
}