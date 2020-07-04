package com.local.local.screen.store.items

import android.app.Activity
import android.app.Dialog
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
import android.widget.*
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.local.local.R
import com.local.local.body.StoreInfo
import com.local.local.body.StoreItems
import com.local.local.extensions.Extensions.listenTextAndClearError
import com.local.local.extensions.Extensions.loadImage
import com.local.local.screen.dialog.BaseDialogFragment
import com.local.local.util.PermissionUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class AddItemDialogFragment : BaseDialogFragment() {
    private val viewModel : AddItemViewModel by viewModel()
    companion object {
        private const val takImgCode = 6556
        private const val selectImgCode = 6557
        private const val requestPermissionCode = 6558
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
        uploadFile = File(context?.externalCacheDir, fileName)
        val uploadFile = uploadFile ?: return
        Toast.makeText(context, "Here~", Toast.LENGTH_SHORT).show()
        if (!uploadFile.exists()) {
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

    var fileName: String = ""
    var uploadFile: File? = null
    var imageUri: Uri? = null

    private lateinit var ivItem: ImageView
    private lateinit var dialog: BottomSheetDialog
    private val bottomSheetView: View by lazy {
            LayoutInflater.from(context).inflate(R.layout.view_bottomsheet_editprofile, null) }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //從相簿讀取照片
        if (requestCode == selectImgCode && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            if (selectedImageUri != null) {
                fileName = getFileName(selectedImageUri)
                Glide.with(this)
                    .asBitmap()
                    .load(selectedImageUri)
                    .into(object : CustomTarget<Bitmap>(640, 640) {
                        override fun onLoadCleared(placeholder: Drawable?) {
                        }

                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            saveImage(resource, fileName)
                            ivItem.setImageBitmap(resource)
                        }
                    })
            }
        }

        //拍照
        else if (requestCode == takImgCode && resultCode == Activity.RESULT_OK) {
            fileName = getFileName(imageUri)
            Glide.with(this)
                .asBitmap()
                .load(imageUri)
                .into(object : CustomTarget<Bitmap>(640, 640) {
                    override fun onLoadCleared(placeholder: Drawable?) {
                    }

                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        saveImage(resource, fileName)
                        ivItem.setImageBitmap(resource)
                    }
                })
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = context ?: return super.onCreateDialog(savedInstanceState)
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_store_additems_window,null)
        val storeItems : StoreItems? = arguments?.getSerializable("storeItem") as StoreItems?
        val adminPassStoreInfo: StoreInfo? = arguments?.getSerializable("storeInfo") as StoreInfo?
        ivItem = view.findViewById(R.id.iv_addItems_img)
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

        bottomSheetView.findViewById<Button>(R.id.btn_sheet_uploadPhoto).setOnClickListener {
            if (!PermissionUtil.hasGrantedReadWriteExternalStorage(context))
                PermissionUtil.requestReadWriteExternalStorage(activity, 0)
            else {
                val photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                startActivityForResult(photoPickerIntent, selectImgCode)
                dialog.dismiss()
            }
        }


        bottomSheetView.findViewById<Button>(R.id.btn_sheet_takePhoto).setOnClickListener {
            if (!PermissionUtil.hasGrantedCamera(context)) {
                PermissionUtil.requestCameraPermission(
                    activity,
                    requestPermissionCode
                )
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
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(intent, takImgCode)
            }
            dialog.dismiss()
        }

        view.findViewById<LinearLayout>(R.id.viewGroup_addItems_img).setOnClickListener {
            dialog.show()
        }

        val viewGroupName = view.findViewById<TextInputLayout>(R.id.viewGroup_addItems_name)
        val etCouponName = view.findViewById<TextInputEditText>(R.id.et_addItems_name).apply {
            listenTextAndClearError(viewGroupName)
            setText(storeItems?.description)
        }
        val viewGroupPoints = view.findViewById<TextInputLayout>(R.id.viewGroup_addItems_points)
        val etPoints = view.findViewById<TextInputEditText>(R.id.et_addItems_points).apply {
            listenTextAndClearError(viewGroupPoints)
            val points = storeItems?.needPoints?.toString() ?: ""
            setText(points)
        }

        view.findViewById<ImageView>(R.id.iv_addItems_img).apply {
            if(storeItems?.imgUrl != null)
                loadImage(context,storeItems.imgUrl)
        }

        view.findViewById<Button>(R.id.btn_addItems_confirm).setOnClickListener {
            if (TextUtils.isEmpty(etCouponName.text)) {
                viewGroupName.error = "請輸入優惠券名稱"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(etPoints.text)) {
                viewGroupPoints.error = "請輸入所需點數!"
                return@setOnClickListener
            }
            val name = etCouponName.text.toString()
            val points = etPoints.text.toString().toInt()

            if(TextUtils.isEmpty(etPoints.text)){
                viewGroupPoints.error = "請輸入所需之點數"
                return@setOnClickListener
            }
            if (uploadFile == null && arguments == null) {
                Toast.makeText(context,"請上傳優惠券照片!",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (uploadFile == null && storeItems == null && adminPassStoreInfo != null) {
                Toast.makeText(context, "請上傳優惠券照片!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.onClickConfirm(uploadFile, name, points, storeItems, adminPassStoreInfo)
        }

        viewModel.eventLiveData.observe(this, Observer { event ->
            event ?: return@Observer
            when(event){
                is AddItemViewModel.Event.OnSaveStart -> showLoadingMsg()
                is AddItemViewModel.Event.OnSaveFinish -> dismissLoadingMsg()
                is AddItemViewModel.Event.OnSaveSuc -> {
                    if (arguments == null) {
                        showDoneMsg("優惠券新增成功!")
                        dismiss()
                    } else {
                        showDoneMsg("修改成功!")
                        dismiss()
                    }
                }
                is AddItemViewModel.Event.OnSaveFailed -> showErrorMsg("優惠券新增失敗")
            }.also {
                viewModel.onEventConsumed(event)
            }
        })

        return MaterialAlertDialogBuilder(context).setView(view).create()
    }
}