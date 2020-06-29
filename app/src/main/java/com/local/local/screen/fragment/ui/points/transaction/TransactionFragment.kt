package com.local.local.screen.fragment.ui.points.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.local.local.R
import com.local.local.body.StoreInfo
import com.local.local.body.TransactionItems
import com.local.local.manager.LoginManager
import com.local.local.screen.fragment.dialog.LoadingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.random.Random

class TransactionFragment : Fragment() {
    private val viewModel: TransactionViewModel by viewModel()

    companion object {
        private const val loadingDialogFragmentTag = "LoadingDialogFragmentTag"
        private const val TransactionSucTag= "TransactionSuccessTAG"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = context ?: return super.onViewCreated(view, savedInstanceState)
        val userData = LoginManager.instance.userData
                ?: return super.onViewCreated(view, savedInstanceState)
        val activity = activity ?: return super.onViewCreated(view, savedInstanceState)
        val tvStores = view.findViewById<TextView>(R.id.tv_transaction_store)
        val storeItems = mutableListOf<TransactionItems>()
        viewModel.retrieveStoreNames()
        val storeNames = mutableListOf<StoreInfo>()
        var randomIndex = 0
        viewModel.storeInfo.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            storeNames.clear()
            storeNames.addAll(it.toList())
            if (storeNames.size > 0) {
                randomIndex = Random.nextInt(0, storeNames.size)
                viewModel.setIndex(randomIndex)
            }
        })

        viewModel.showingStoreName.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            tvStores.text = it
        })

        val clickListener = object : TransactionAdapter.ClickListener {
            override fun onClickConfirm(position: Int) {
                if (position < storeItems.size) {
                    if (storeItems[position].needPoints > userData.points) {
                        Toast.makeText(context, "點數不足! 請多至戶外活動來累積點數", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.onClickExchange(position)
                    }
                }
            }
        }
        val rvAdapter = TransactionAdapter(context, storeItems).apply {
            this.clickListener = clickListener
        }
        view.findViewById<RecyclerView>(R.id.rv_transaction_items).apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(context)
        }

        view.findViewById<ImageView>(R.id.iv_transaction_left).setOnClickListener {
            randomIndex += 1
            if(randomIndex>=storeNames.size){
                randomIndex = 0
            }
            viewModel.setIndex(randomIndex)
        }

        view.findViewById<ImageView>(R.id.iv_transaction_right).setOnClickListener {
            randomIndex -= 1
            if(randomIndex< 0){
                randomIndex = storeNames.lastIndex
            }
            viewModel.setIndex(randomIndex)
        }

        viewModel.storeItems.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            storeItems.clear()
            storeItems.addAll(it)
            rvAdapter.notifyDataSetChanged()
        })

        viewModel.eventLiveData.observe(viewLifecycleOwner, Observer { event ->
            event ?: return@Observer
            when (event) {
                is TransactionViewModel.Event.OnUpdateStart -> {
                    activity.supportFragmentManager.findFragmentByTag(loadingDialogFragmentTag) ?: run{
                        LoadingFragment().showNow(activity.supportFragmentManager, loadingDialogFragmentTag)
                    }
                }
                is TransactionViewModel.Event.OnUpdateFinish -> {
                    activity.supportFragmentManager.findFragmentByTag(loadingDialogFragmentTag)?.let { fragment ->
                        (fragment as? LoadingFragment)?.dismiss()
                    }
                }
                is TransactionViewModel.Event.OnUpdateSuc -> {
                    activity.supportFragmentManager.findFragmentByTag(TransactionSucTag) ?: kotlin.run {
                        TransactionSucFragment().showNow(activity.supportFragmentManager, TransactionSucTag)
                    }
                }
                is TransactionViewModel.Event.OnUpdateFail -> {
                    Toast.makeText(context,"兌換失敗!請重新嘗試!",Toast.LENGTH_SHORT).show()
                }
            }.also {
                viewModel.onEventConsumed(event)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transaction, container, false)

    }
}