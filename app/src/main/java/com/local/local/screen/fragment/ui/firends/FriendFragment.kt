package com.local.local.screen.fragment.ui.firends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.local.local.R
import com.local.local.screen.fragment.dialog.BaseDialogFragment

class FriendFragment : BaseDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_friends, container, false)
        root.findViewById<LinearLayout>(R.id.viewGroup_friends_addFriend).setOnClickListener {
            findNavController().navigate(R.id.action_nav_friends_to_addFriendFragment)
        }


        root.findViewById<LinearLayout>(R.id.viewGroup_friends_friendList).setOnClickListener {

        }

        return root
    }
}
