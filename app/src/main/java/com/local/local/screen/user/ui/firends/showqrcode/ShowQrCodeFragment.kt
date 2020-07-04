package com.local.local.screen.user.ui.firends.showqrcode

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.local.local.R
import com.local.local.manager.UserLoginManager

class ShowQrCodeFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        isCancelable = true
        val v = LayoutInflater.from(context).inflate(R.layout.fragment_show_qrcode, null)
        v.findViewById<ImageView>(R.id.iv_showQrCode_close).setOnClickListener {
            dismiss()
        }
        val key = UserLoginManager.instance.userData?.userKey ?: return super.onCreateDialog(
            savedInstanceState
        )
        val encoder = BarcodeEncoder()

        v.findViewById<ImageView>(R.id.iv_showQrCode_code).apply {
            setImageBitmap(
                encoder.encodeBitmap(key, BarcodeFormat.QR_CODE, 300, 300)
            )
        }
        return builder.setView(v).create()
    }


}