package com.local.local.screen.fragment.ui.firends.addfriend

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.local.local.R
import com.local.local.extensions.Extensions.loadCircleImage
import com.local.local.manager.LoginManager
import com.local.local.screen.fragment.dialog.BaseDialogFragment
import kotlinx.android.synthetic.main.fragment_add_friends.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddFriendFragment : BaseDialogFragment() {
    private val viewModel: AddFriendViewModel by viewModel()
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = context ?: return super.onViewCreated(view, savedInstanceState)
        val etPhone = view.findViewById<TextInputEditText>(R.id.et_addFriend_phone)
        val btnSearch = view.findViewById<ImageView>(R.id.iv_addFriend_search)
        val btnScan = view.findViewById<Button>(R.id.btn_addFriend_scanQRcode)
        val btnAddFriend = view.findViewById<Button>(R.id.btn_addFriend_add)
        val ivFriendAvatar = view.findViewById<ImageView>(R.id.iv_addFriend_friendAvatar)
        val tvFriendName = view.findViewById<TextView>(R.id.tv_addFriend_friendName)
        val viewGroupPhone = view.findViewById<TextInputLayout>(R.id.viewGroup_addFriend_phone)
        etPhone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewGroupPhone.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        btnSearch.setOnClickListener {
            val phoneNumber = etPhone.text.toString()
            val validate =
                isPhoneValid(phoneNumber)
            when {
                phoneNumber.toUniversalPhoneNumber() == LoginManager.instance.userData?.phone -> {
                    viewGroupPhone.error = getString(R.string.error_addFriend_input_self_phone)
                }
                validate -> {
                    viewModel.searchByPhoneNumber(phoneNumber)
                }
                else -> {
                    viewGroupPhone.error = getString(R.string.error_invalid_phone_number)
                }
            }
        }

        btnScan.setOnClickListener {

        }

        btnAddFriend.setOnClickListener {
            viewModel.onClickAdd()
        }

        viewModel.eventLiveData.observe(this, Observer { event ->
            event ?: return@Observer
            when (event) {
                is AddFriendViewModel.Event.OnAddStart -> {
                    showLoadingMsg()
                }
                is AddFriendViewModel.Event.OnAddFinish -> {
                    Log.d("got", "got here add finish..")
                    dismissLoadingMsg()
                }
                is AddFriendViewModel.Event.OnAddFail -> {
                    Log.d("got", "got here add failed..")
                    Toast.makeText(context, "加入好友失敗!", Toast.LENGTH_SHORT).show()
                }
                is AddFriendViewModel.Event.OnAddSuc -> {
                    Log.d("got", "got here add succ..")
                    btnAddFriend.visibility = View.GONE
                    showDoneMsg("加入好友成功!")
                }
                is AddFriendViewModel.Event.OnAddError -> {
                    Log.d("got", "got here add error..")
                    Toast.makeText(
                        context,
                        getString(R.string.error_unknown_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is AddFriendViewModel.Event.OnSearchStart -> {
                    ivFriendAvatar.visibility = View.GONE
                    tvFriendName.visibility = View.GONE
                    btnAddFriend.visibility = View.GONE
                    showLoadingMsg()
                }
                is AddFriendViewModel.Event.OnSearchFinish -> {
                    dismissLoadingMsg()
                }
                is AddFriendViewModel.Event.OnSearchSuc -> {
                    ivFriendAvatar.visibility = View.VISIBLE
                    tvFriendName.visibility = View.VISIBLE
                    iv_addFriend_friendAvatar.loadCircleImage(
                        context,
                        viewModel.searchedUserInfo?.avatarUrl
                    )
                    tv_addFriend_friendName.text = viewModel.searchedUserInfo?.name
                }
                is AddFriendViewModel.Event.OnSearchFail -> {
                    showErrorMsg("搜尋好友失敗!請重新嘗試")
                }
                is AddFriendViewModel.Event.OnSearchError -> {
                    Toast.makeText(
                        context,
                        getString(R.string.error_unknown_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is AddFriendViewModel.Event.OnFriendsAlreadyAdded -> {
                    iv_addFriend_friendAvatar.loadCircleImage(
                        context,
                        viewModel.searchedUserInfo?.avatarUrl
                    )
                    tv_addFriend_friendName.text = viewModel.searchedUserInfo?.name
                }
                is AddFriendViewModel.Event.OnFriendsNotAdded ->{
                    btnAddFriend.visibility = View.VISIBLE
                }

            }.also {
                viewModel.onEventConsumed(event)
            }

        })
    }
}