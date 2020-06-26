package com.local.local.extensions

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.local.local.R
import com.local.local.body.LocationInfo

object Extensions {
    fun TextInputEditText.listenTextAndClearError(parentLayout: TextInputLayout){
        this.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                parentLayout.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }

    fun <T> MutableLiveData<T>.notifyObserver(){
        this.value = this.value
    }

    fun ImageView.loadCircleImage(context: Context,url: String?){
        val cp = CircularProgressDrawable(context)
        cp.strokeWidth = 5f
        cp.centerRadius = 30f
        cp.setColorSchemeColors(R.color.colorGreen)
        cp.start()
        Glide
            .with(context)
            .load(url)
            .apply(RequestOptions().circleCrop())
            .placeholder(cp)
            .into(this)
    }

    val locationList = listOf<LocationInfo>(
            LocationInfo(23.06338,120.42038,"高雄市六龜區寶來社區發展協會"),
            LocationInfo(23.06342,120.42127,"高雄市立寶來國民中學"),
            LocationInfo(23.06330,120.42025,"高雄市立寶來國民小學"),
            LocationInfo(23.06382,120.42113,"高雄市政府消防局第六大隊第三中隊寶來分隊"),
            LocationInfo(23.06275,120.41552,"高雄市政府警察局六龜分局寶來派出所"),
            LocationInfo(23.06278,120.41576,"六龜寶來郵局鳳山44支局"),
            LocationInfo(23.06317,120.41593,"7-ELEVEN 寶來門市"),
            LocationInfo(23.06276,120.41541,"高雄市六龜觀光休閒協會"),
            LocationInfo(23.06252,120.42099,"寶來花賞溫泉公園(遠山望月溫泉露營)"),
            LocationInfo(23.06198,120.41548,"寶來溪橋"),
            LocationInfo(23.06199,120.41394,"檨仔腳文化共享空間"),
            LocationInfo(23.06179,120.40556,"建山國小"),
            LocationInfo(23.06292,120.41014,"高雄市政府警察局六龜分局建山派出所"),
            LocationInfo(23.09747,120.68225,"台灣中油 保來站(加盟)"),
            LocationInfo(23.104116,120.696490,"寶來河床旁公園(外)"),
            LocationInfo(23.105761,120.697311,"寶來河床旁公園(內)"),
            LocationInfo(23.06239,120.41006,"真耶穌教會建山祈禱所"),
            LocationInfo(23.06287,120.41015,"建山基督長老教會"),
            LocationInfo(23.06281,120.40598,"建山安息日會"),
            LocationInfo(23.05428,120.41357,"寶來保安宮"),
            LocationInfo(23.06020,120.41071,"玅通寺"),
            LocationInfo(23.06284,120.42089,"福隆堂")
    )
}