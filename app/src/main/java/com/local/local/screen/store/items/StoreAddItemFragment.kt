package com.local.local.screen.store.items

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.local.local.R
import com.local.local.body.StoreItems
import com.local.local.screen.dialog.LoadingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class StoreAddItemFragment : Fragment() {
    private val viewModel : StoreAddItemViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_store_additem, container, false)
    }

    companion object {
        private const val showAddItemDialogTag = "ShowAddItemDialogTag"
        private const val loadingTag = "ShowLoadingMsgTag"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val context = context ?: return super.onViewCreated(view, savedInstanceState)
        val activity = activity ?: return super.onViewCreated(view, savedInstanceState)
        val arg = Bundle()
        view.findViewById<Button>(R.id.btn_storeAddItems_add).setOnClickListener {
            activity.supportFragmentManager.findFragmentByTag(showAddItemDialogTag) ?: run {
                AddItemDialogFragment().showNow(
                    activity.supportFragmentManager,
                    showAddItemDialogTag
                )
            }
        }
        val storeItemsList = arrayListOf<StoreItems>()
        val onClickEditListener = object : StoreItemsAdapter.ClickListener {
            override fun onClickEdit(storeItems: StoreItems) {
                arg.clear()
                arg.putSerializable("storeItem",storeItems)
                activity.supportFragmentManager.findFragmentByTag(showAddItemDialogTag) ?: run {
                    AddItemDialogFragment().apply {
                        arguments = arg
                        showNow(
                            activity.supportFragmentManager,
                            showAddItemDialogTag
                        )
                    }
                }
            }

            override fun onClickDelete(storeItems: StoreItems) {
                viewModel.onClickDeleteItems(storeItems)
            }
        }

        val rvAdapter = StoreItemsAdapter(context, storeItemsList).apply {
            listener = onClickEditListener
        }
        view.findViewById<RecyclerView>(R.id.rv_storeAddItems_items).apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(context)
        }
        viewModel.retrieveStoreItems()

        viewModel.storeItems.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            storeItemsList.clear()
            storeItemsList.addAll(it)
            rvAdapter.notifyDataSetChanged()
        })

        viewModel.eventLiveData.observe(viewLifecycleOwner, Observer { event ->
            event ?: return@Observer
            when (event) {
                is StoreAddItemViewModel.Event.OnDeleteStart -> {
                    activity.supportFragmentManager.findFragmentByTag(loadingTag) ?: run {
                        LoadingFragment()
                            .showNow(activity.supportFragmentManager, loadingTag)
                    }
                }
                is StoreAddItemViewModel.Event.OnDeleteFinish -> {
                    activity.supportFragmentManager.findFragmentByTag(loadingTag)?.let { fragment ->
                        (fragment as? LoadingFragment)?.dismiss()
                    }
                }
                is StoreAddItemViewModel.Event.OnDeleteSuc -> {
                    Toast.makeText(context,"刪除成功!",Toast.LENGTH_SHORT).show()
                }
                is StoreAddItemViewModel.Event.OnDeleteFail -> {
                    Toast.makeText(context,"刪除失敗!",Toast.LENGTH_SHORT).show()
                }
            }.also {
                viewModel.onEventConsumed(event)
            }
        })

    }
}