package com.local.local.screen.user.ui.firends.addfriend

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.local.local.R
import com.local.local.util.PermissionRationalActivity
import org.koin.android.ext.android.inject
import java.io.IOException

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
        val surfaceView = view.findViewById<SurfaceView>(R.id.scanQrCode_surfaceView)
        val detector = BarcodeDetector.Builder(context).setBarcodeFormats(Barcode.QR_CODE).build()
        val cameraSource = CameraSource.Builder(context, detector).setAutoFocusEnabled(true).build()
        val options = FirebaseVisionBarcodeDetectorOptions.Builder()
            .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE)
            .build()
        val container = view.findViewById<FrameLayout>(R.id.scanQrCode_container)
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(
                holder: SurfaceHolder?,
                format: Int,
                width: Int,
                height: Int
            ) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                cameraSource.stop()
            }

            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder?) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    startActivity(Intent(context, PermissionRationalActivity::class.java))
                } else {
                    try {
                        cameraSource.start(holder)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        })
        detector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
            }

            override fun receiveDetections(p0: Detector.Detections<Barcode>?) {
                p0?.detectedItems?.run {
                    if (this.size() > 0) {
                        viewModel.qrCodeScanResult.postValue(this.valueAt(0).rawValue)
                    }
                }
            }
        })
        viewModel.qrCodeScanResult.observe(viewLifecycleOwner, Observer { result ->
            result ?: return@Observer
            detector.release()
            cameraSource.stop()
            container.removeAllViews()
            findNavController().popBackStack()
        })
    }
}