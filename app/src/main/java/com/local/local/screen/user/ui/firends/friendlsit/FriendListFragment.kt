package com.local.local.screen.user.ui.firends.friendlsit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.local.local.R
import com.local.local.body.AddFriendsBody
import com.local.local.body.UserInfo
import com.local.local.screen.dialog.BaseDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class FriendListFragment : BaseDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friend_friendlist,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = context ?: return super.onViewCreated(view, savedInstanceState)
        val friendList = mutableListOf<UserInfo?>()
        val viewModel : FriendListViewModel by viewModel()
        val rvAdapter =
            FriendListAdapter(
                context,
                friendList
            )
        val rvFriends = view.findViewById<RecyclerView>(R.id.rv_friendlist).apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(context)
        }

        viewModel.retrieveFriendList()
        val friendKeyList = arrayListOf<AddFriendsBody>()
        viewModel.friendKeyList.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            friendKeyList.clear()
            friendKeyList.addAll(it)
            friendKeyList.forEach {
                viewModel.searchUserInfoByKey(it.friendKey)
            }
        })

        viewModel.friendList.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            friendList.clear()
            friendList.addAll(it)
            Log.d("status","in observer friend list :$friendList")
            rvAdapter.notifyDataSetChanged()
            rvFriends.scheduleLayoutAnimation()
        })


    }

}