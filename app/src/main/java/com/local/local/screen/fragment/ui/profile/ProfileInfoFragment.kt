package com.local.local.screen.fragment.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.local.local.R
import com.local.local.extensions.Extensions.loadCircleImage
import com.local.local.screen.fragment.dialog.BaseDialogFragment
import kotlinx.android.synthetic.main.fragment_profileinfo.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class ProfileInfoFragment : BaseDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profileinfo,container,false)
    }


    private lateinit var bottomSheetView : View

    private fun showBottomSheet(context: Context) {
        BottomSheetDialog(context).apply {
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView.parent as View)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBehavior.setPeekHeight(0)
        }.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = context ?: return super.onViewCreated(view, savedInstanceState)
        val defaultList = mutableListOf<String?>()
        bottomSheetView = LayoutInflater.from(context).inflate(R.layout.view_bottomsheet_editprofile, null)
        val rvAdapter = ProfileInfoAdapter(context,defaultList)
        var userClickIconPosition = -1
        var fileName: String? = null
        var uploadFile: File? = null
        val viewModel : ProfileInfoViewModel by viewModel()
        viewModel.retrieveDefaultAvatar()
        val userAvatar = view.findViewById<ImageView>(R.id.iv_profileInfo_userAvatar)
        view.findViewById<RecyclerView>(R.id.rv_profileInfo_defaultIcon).apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            adapter = rvAdapter
            addItemDecoration(SpaceItemDecoration(10,0))
            addOnItemTouchListener(
                    IconClickListener(
                            context,
                            rv_profileInfo_defaultIcon,
                            object : IconClickListener.OnItemClickListener {
                                override fun onItemClick(view: View?, position: Int) {
                                    viewModel.defaultAvatarList.value?.get(position)?.apply {
                                        userAvatar.loadCircleImage(context,this)
                                    }
                                    userClickIconPosition = position
                                    fileName = ""
                                    uploadFile = null
                                    viewModel.uploadFile = null
                                }
                            })
            )
        }
        view.findViewById<ImageView>(R.id.iv_profileInfo_editProfileAvatar).setOnClickListener {
            showBottomSheet(context)
        }



        viewModel.defaultAvatarList.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            defaultList.clear()
            defaultList.addAll(it)
            rvAdapter.notifyDataSetChanged()
        })
    }
}