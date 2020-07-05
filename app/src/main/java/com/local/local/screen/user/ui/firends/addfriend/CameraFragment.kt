package com.local.local.screen.user.ui.firends.addfriend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.local.local.R
import com.otaliastudios.cameraview.CameraView
import org.koin.android.ext.android.inject

class CameraFragment : Fragment() {
    private val viewModel: AddFriendViewModel by inject()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scan_qrcode, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val context = context ?: return super.onViewCreated(view, savedInstanceState)
        val activity = activity ?: return super.onViewCreated(view, savedInstanceState)
        val options = FirebaseVisionBarcodeDetectorOptions.Builder()
            .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE)
            .build()

        val detector: FirebaseVisionBarcodeDetector? = FirebaseVision.getInstance().getVisionBarcodeDetector(options)
        view.findViewById<CameraView>(R.id.cameraView).apply {
            setLifecycleOwner(this@CameraFragment)
            addFrameProcessor { frame ->
                val data = frame.getData<ByteArray>()
                val metadata = FirebaseVisionImageMetadata.Builder()
                    .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                    .setHeight(frame.size.height)
                    .setWidth(frame.size.width)
                    .build()
                val image = FirebaseVisionImage.fromByteArray(data, metadata)
                detector?.detectInImage(image)?.addOnSuccessListener { list ->
                    processResult(list)
                    activity.supportFragmentManager.beginTransaction().remove(this@CameraFragment).commit()
                }
            }
        }
    }

    private fun processResult(barcodes: List<FirebaseVisionBarcode>) {
        if (barcodes.isNotEmpty()) {
            val valueType = barcodes[0].rawValue
            viewModel.qrCodeScanResult.value = valueType
        }
    }
}