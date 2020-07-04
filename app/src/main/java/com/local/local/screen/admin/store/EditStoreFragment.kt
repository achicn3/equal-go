package com.local.local.screen.admin.store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.local.local.R
import com.local.local.body.StoreInfo
import com.local.local.body.StoreItems
import com.local.local.screen.store.items.AddItemDialogFragment
import com.local.local.screen.store.items.StoreAddItemFragment
import com.local.local.screen.store.items.StoreItemsAdapter
import com.local.local.screen.user.ui.points.transaction.exchange.TransactionAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.random.Random

class EditStoreFragment : Fragment() {
    private val viewModel : EditStoreViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_store_additem,container,false)
    }

    companion object{
        private const val showAddItemDialogTag = "AddItemDialogTag"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val context = context ?: return super.onViewCreated(view, savedInstanceState)
        val activity = activity ?: return super.onViewCreated(view, savedInstanceState)
        view.findViewById<LinearLayout>(R.id.viewGroup_storeAddItems_store).apply {
            visibility = View.VISIBLE
        }
        val tvStores = view.findViewById<TextView>(R.id.tv_storeAddItems_store)
        var randomIndex = 0
        val stores = mutableListOf<StoreInfo>()
        val storeItems = arrayListOf<StoreItems>()
        val arg = Bundle()
        val onClickEditListener = object : StoreItemsAdapter.ClickListener {
            override fun onClickEdit(storeItems: StoreItems) {
                activity.supportFragmentManager.findFragmentByTag(showAddItemDialogTag) ?: run {
                    AddItemDialogFragment().apply {
                        arg.clear()
                        arg.putSerializable("storeItem",storeItems)
                        arg.putSerializable("storeInfo",stores[randomIndex])
                        arguments = arg
                        showNow(
                            activity.supportFragmentManager,
                            showAddItemDialogTag
                        )
                    }
                }
            }
        }
        val rvAdapter = StoreItemsAdapter(context,storeItems).apply {
            listener = onClickEditListener
        }
        view.findViewById<RecyclerView>(R.id.rv_storeAddItems_items).apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(context)
        }

        view.findViewById<ImageView>(R.id.iv_storeAddItems_left).setOnClickListener {
            randomIndex -= 1
            if(randomIndex< 0){
                randomIndex = stores.lastIndex
            }
            viewModel.setIndex(randomIndex)
        }

        view.findViewById<ImageView>(R.id.iv_storeAddItems_right).setOnClickListener {
            randomIndex += 1
            if(randomIndex>=stores.size){
                randomIndex = 0
            }
            viewModel.setIndex(randomIndex)
        }

        view.findViewById<Button>(R.id.btn_storeAddItems_add).setOnClickListener {
            activity.supportFragmentManager.findFragmentByTag(showAddItemDialogTag) ?: run {
                AddItemDialogFragment().apply {
                    arg.clear()
                    arg.putSerializable("storeInfo",stores[randomIndex])
                    arguments = arg
                    showNow(
                        activity.supportFragmentManager,
                        showAddItemDialogTag
                    )
                }
            }
        }

        viewModel.retrieveStoreNames()

        viewModel.storeInfo.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            stores.clear()
            stores.addAll(it.toList())
            if (stores.size > 0) {
                randomIndex = Random.nextInt(0, stores.size)
                viewModel.setIndex(randomIndex)
            }
        })


        viewModel.showingStore.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            tvStores.text = it.storeName
        })

        viewModel.storeItems.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            storeItems.clear()
            storeItems.addAll(it)
            rvAdapter.notifyDataSetChanged()
        })


    }
}