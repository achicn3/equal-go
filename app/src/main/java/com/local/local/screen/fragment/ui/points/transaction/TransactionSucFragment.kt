package com.local.local.screen.fragment.ui.points.transaction

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.local.local.R
import kotlinx.coroutines.*
import java.text.DecimalFormat

class TransactionSucFragment() : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = context ?: return super.onCreateDialog(savedInstanceState)
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_transaction_success, null)
        isCancelable = false
        val tvCountDown = view.findViewById<TextView>(R.id.tv_transactionSuc_countDown)
        val btnConfirm = view.findViewById<Button>(R.id.btn_transactionSuc_confirm)
        val formatter = DecimalFormat("0#:##")
        view.findViewById<ImageView>(R.id.iv_transactionSuc_close).setOnClickListener {
            dismiss()
        }
        val job: Job = GlobalScope.launch(Dispatchers.Main) {
            for (times in 180 downTo 0) {
                val min = times / 60
                val sec = times - 60 * min
                tvCountDown.text = getString(R.string.transactionSuc_countDown_format, min, sec)
                delay(1000)
            }
            dismiss()
        }

        btnConfirm.setOnClickListener {
            job.cancel()
            dismiss()
        }
        return MaterialAlertDialogBuilder(context).setView(view).create()
    }
}