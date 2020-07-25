package com.local.local.screen.admin.verification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.local.local.R
import com.local.local.body.StoreLoginRegisterBody
import org.koin.androidx.viewmodel.ext.android.viewModel

class VerificationFragment : Fragment() {
    private val viewModel : VerificationViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_recyclerview,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val context = context ?: return super.onViewCreated(view, savedInstanceState)
        val rvList = view.findViewById<RecyclerView>(R.id.rv_admin_common)
        val storeList = arrayListOf<StoreLoginRegisterBody>()
        val rvAdapter = VerificationAdapter(storeList)
        rvList.apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(context)
        }
        viewModel.verificationStores.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            storeList.clear()
            storeList.addAll(it)
            rvAdapter.notifyDataSetChanged()
        })
    }
}