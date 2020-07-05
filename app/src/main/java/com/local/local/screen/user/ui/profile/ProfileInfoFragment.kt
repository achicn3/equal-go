package com.local.local.screen.user.ui.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.local.local.R
import com.local.local.extensions.Extensions.listenTextAndClearError
import com.local.local.extensions.Extensions.loadCircleImage
import com.local.local.manager.UserLoginManager
import com.local.local.screen.dialog.BaseDialogFragment
import com.local.local.util.PermissionUtil
import kotlinx.android.synthetic.main.fragment_profileinfo.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class ProfileInfoFragment : BaseDialogFragment() {
    val viewModel : ProfileInfoViewModel by viewModel()
    private lateinit var dialog: BottomSheetDialog
    private lateinit var bottomSheetView: View
    private lateinit var ivAvatar : ImageView
    var fileName: String? = null
    var uploadFile: File? = null
    var imageUri: Uri? = null
    var userClickIconPosition = -1

    companion object {
        private const val CAMERA_IMG_REQUEST_CODE = 5566
        private const val SELECT_IMG_REQUEST_CODE = 5577
        private const val TAKE_IMG_REQUEST_CODE = 5588
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bottomSheetView = inflater.inflate(R.layout.view_bottomsheet_editprofile, null)
        return inflater.inflate(R.layout.fragment_profileinfo,container,false)
    }


    override fun onDestroy() {
        super.onDestroy()
        viewModel.uploadFile = null
    }

    /**
     * 根據uri取得檔案名稱
     * */
    private fun getFileName(uri: Uri?): String {
        uri ?: return ""
        val projection =
                arrayOf(MediaStore.Images.Media.DISPLAY_NAME)
        context?.contentResolver?.query(uri, projection, null, null, null).use { cursor ->
            val columnIndex =
                    cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME) ?: return ""
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex)
            }
        }
        return uri.path ?: ""
    }

    private fun saveImage(bitmap: Bitmap, fileName: String) {
        if (TextUtils.isEmpty(fileName))
            return
        viewModel.uploadFile = File(context?.externalCacheDir, fileName)
        val uploadFile = viewModel.uploadFile ?: return
        if (uploadFile.exists()) {
            uploadFile.parent?.run {
                File(this).mkdirs()
            }
        }
        try {
            val outputStream = FileOutputStream(uploadFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //從相簿讀取照片
        if (requestCode == SELECT_IMG_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            userClickIconPosition = -1
            val selectedImageUri = data.data
            if (selectedImageUri != null) {
                var fileName = getFileName(selectedImageUri)
                Glide.with(this)
                        .asBitmap()
                        .load(selectedImageUri)
                        .apply(RequestOptions().circleCrop())
                        .into(object : CustomTarget<Bitmap>(640, 640) {
                            override fun onLoadCleared(placeholder: Drawable?) {
                            }

                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                saveImage(resource, fileName)
                                ivAvatar.setImageBitmap(resource)
                            }
                        })
            }
        }

        //拍照
        else if (requestCode == TAKE_IMG_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            userClickIconPosition = -1
            val fileName = getFileName(imageUri)
            Glide.with(this)
                    .asBitmap()
                    .load(imageUri)
                    .apply(RequestOptions().circleCrop())
                    .into(object : CustomTarget<Bitmap>(640, 640) {
                        override fun onLoadCleared(placeholder: Drawable?) {
                        }

                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            saveImage(resource, fileName)
                            ivAvatar.setImageBitmap(resource)
                        }
                    })
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = context ?: return super.onViewCreated(view, savedInstanceState)
        val defaultList = mutableListOf<String?>()
        val rvAdapter = ProfileInfoAdapter(context,defaultList)
        val viewGroupName = view.findViewById<TextInputLayout>(R.id.viewGroup_profileInfo_name)
        ivAvatar = view.findViewById(R.id.iv_profileInfo_userAvatar)
        viewModel.retrieveDefaultAvatar()

        dialog = BottomSheetDialog(context).apply {
            setContentView(bottomSheetView)
            setOnShowListener {
                findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)?.also {
                    BottomSheetBehavior.from(it).apply {
                        state = BottomSheetBehavior.STATE_EXPANDED
                        setPeekHeight(0)
                    }
                }
            }
        }

        bottomSheetView.findViewById<Button>(R.id.btn_sheet_takePhoto).setOnClickListener {
            if (!PermissionUtil.hasGrantedCamera(context)) {
                PermissionUtil.requestCameraPermission(activity, CAMERA_IMG_REQUEST_CODE)
            } else {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                uploadFile = File(
                        context.getExternalFilesDir(null),
                        "img_" + System.currentTimeMillis().toString() + ".jpg"
                )
                /**
                 * AndroidX  要多FileProvider
                 * */
                val authority = "${context.packageName}.fileprovider"
                uploadFile?.run {
                    imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        FileProvider.getUriForFile(
                                context,
                                authority, this
                        ).also {
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                    } else {
                        Uri.fromFile(this)
                    }
                }
                viewModel.uploadFile = uploadFile
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(intent, TAKE_IMG_REQUEST_CODE)
            }
            dialog.dismiss()
        }

        bottomSheetView.findViewById<Button>(R.id.btn_sheet_uploadPhoto).setOnClickListener {
            if (!PermissionUtil.hasGrantedReadWriteExternalStorage(context))
                PermissionUtil.requestReadWriteExternalStorage(activity, 0)
            else {
                val photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                startActivityForResult(photoPickerIntent, SELECT_IMG_REQUEST_CODE)
                dialog.dismiss()
            }
        }
        val userAvatar = view.findViewById<ImageView>(R.id.iv_profileInfo_userAvatar).apply{
            loadCircleImage(context,UserLoginManager.instance.userData?.avatarUrl)
        }

        val etName = view.findViewById<TextInputEditText>(R.id.et_profileInfo_displayName).apply {
            setText(UserLoginManager.instance.userData?.name)
        }

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
            dialog.show()
        }

        viewModel.defaultAvatarList.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            defaultList.clear()
            defaultList.addAll(it)
            rvAdapter.notifyDataSetChanged()
        })
        etName.listenTextAndClearError(viewGroupName)
        view.findViewById<Button>(R.id.btn_profileInfo_save).setOnClickListener {
            if(!TextUtils.isEmpty(etName.text)){
                viewGroupName.error = "請輸入名字!!"
                return@setOnClickListener
            }
            val name = etName.text.toString()
            viewModel.onClickSave(userClickIconPosition,name)
            Toast.makeText(context,"儲存成功!",Toast.LENGTH_SHORT).show()
        }

    }
}