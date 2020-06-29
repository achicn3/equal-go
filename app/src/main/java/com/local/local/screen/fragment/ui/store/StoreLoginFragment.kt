package com.local.local.screen.fragment.ui.store

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.local.local.R
import com.local.local.screen.fragment.dialog.BaseDialogFragment

class StoreLoginFragment : BaseDialogFragment() {
    interface StoreLoginListener {
        fun onClickLogin(account: String, pwd: String, accountView: TextInputLayout, pwdView: TextInputLayout)
    }

    private var storeLoginListener: StoreLoginListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (targetFragment != null) {
            storeLoginListener = targetFragment as StoreLoginListener
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnConfirm = view.findViewById<Button>(R.id.btn_storeLogin_confrim)
        val btnCancel = view.findViewById<Button>(R.id.btn_storeLogin_cancel)
        val etAccount = view.findViewById<TextInputEditText>(R.id.et_storeLogin_account)
        val etPwd = view.findViewById<TextInputEditText>(R.id.et_storeLogin_pwd)
        val viewGroupAccount = view.findViewById<TextInputLayout>(R.id.viewGroup_storeLogin_account)
        val viewGroupPwd = view.findViewById<TextInputLayout>(R.id.viewGroup_storeLogin_pwd)
        isCancelable = true

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
            storeLoginListener?.onClickLogin(account, pwd, viewGroupAccount, viewGroupPwd)
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_store_login, container, false)
    }
}