package com.local.local.screen.fragment.ui.points.daystatics

import android.animation.ObjectAnimator
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.local.local.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import androidx.lifecycle.Observer
import com.local.local.body.RecordInfo
import com.local.local.manager.UserLoginManager
import java.text.DecimalFormat

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    private fun Int.format(): String = if (this < 10) "0$this" else "$this"

    private fun Calendar.getFormatDate(): String {
        val year = get(Calendar.YEAR)
        val month = (get(Calendar.MONTH) + 1).format()
        val day = get(Calendar.DAY_OF_MONTH).format()
        return getString(R.string.date_format, year, month, day)
    }

    private val viewModel: HomeViewModel by viewModel()

    private fun ProgressBar.animateProgress(value: Int) =
            ObjectAnimator.ofInt(this, "progress", value)
                    .apply {
                        duration = 1500
                        interpolator = FastOutSlowInInterpolator()
                        start()
                    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = context ?: return super.onViewCreated(view, savedInstanceState)
        val pbPoints = view.findViewById<ProgressBar>(R.id.pb_main_points).apply {
            animateProgress(100)
        }
        val tvPointsInPb = view.findViewById<TextView>(R.id.tv_main_pointsInPb).apply {
            text = getString(R.string.accumulation_points, 30)
        }
        val listener = object : UserLoginManager.LoginListener{
            override fun onLogStateChange() {

            }

            override fun onUserInfoChange() {
                viewModel.searchRecord(Calendar.getInstance(Locale.TAIWAN).getFormatDate())
            }

        }

        UserLoginManager.instance.addListener(listener)
        val tvDistance = view.findViewById<TextView>(R.id.tv_main_moveDistance)
        val tvDates = view.findViewById<TextView>(R.id.tv_main_date).apply {
            setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    DatePickerDialog(context).apply {
                        setOnDateSetListener { _, year, month, dayOfMonth ->
                            viewModel.setDate(year, month, dayOfMonth)
                        }
                    }.show()
                }
            }
        }
        val tvPoints = view.findViewById<TextView>(R.id.tv_main_points)
        viewModel.calendar.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            tvDates.text = it.getFormatDate()
            viewModel.searchRecord(it.getFormatDate())
        })

        viewModel.recordInfo.observe(viewLifecycleOwner, Observer { recordInfo ->
            val info = recordInfo ?: RecordInfo(0f,0)
            tvPointsInPb.text = getString(R.string.accumulation_points,info.points)
            tvPoints.text = info.points.toString()
            pbPoints.animateProgress(info.points)
            val df = DecimalFormat("0.0#")
            tvDistance.text = df.format(info.distance/1000)
        })

        view.findViewById<ImageView>(R.id.iv_main_left).apply {
            setOnClickListener {
                viewModel.decrementDay()
            }
        }

        view.findViewById<ImageView>(R.id.iv_main_right).apply {
            setOnClickListener {
                //calendar.add(Calendar.DATE, 1)
                //tvDates.text = getFormatDate()
                viewModel.incrementDay()
            }
        }


    }


}
