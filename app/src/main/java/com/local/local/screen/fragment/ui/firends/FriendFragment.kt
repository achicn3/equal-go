package com.local.local.screen.fragment.ui.firends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import com.local.local.R
import com.local.local.screen.fragment.dialog.BaseDialogFragment

class FriendFragment : BaseDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<LinearLayout>(R.id.viewGroup_friends_addFriend).setOnClickListener {
            findNavController().navigate(R.id.action_nav_friends_to_addFriendFragment)
        }
        view.findViewById<LinearLayout>(R.id.viewGroup_friends_friendList).setOnClickListener {

        }
    }
}
