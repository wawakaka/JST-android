package com.wawakaka.jst.presensi.view

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.widget.LinearLayout
import com.wawakaka.jst.R
import com.wawakaka.jst.base.view.ViewUtils
import com.wawakaka.jst.dashboard.model.Siswa
import com.wawakaka.jst.presensi.model.Presensi
import kotlinx.android.synthetic.main.list_check_box.view.*

/**
 * Created by wawakaka on 11/26/2017.
 */
class PresensiCheckView : LinearLayout {

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context,
                attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context,
                attrs: AttributeSet,
                defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context,
                attrs: AttributeSet,
                defStyleAttr: Int,
                defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView()
    }

    private var siswa: Siswa? = null

    private fun initView() {
        val layoutInflater = ViewUtils.getLayoutInflater(context)
        layoutInflater?.inflate(R.layout.list_check_box, this, true)

        orientation = VERTICAL
    }

    fun setSiswa(siswa: Siswa) {
        this.siswa = siswa
        setNamaSiswa(siswa.nama as String)
    }

    fun getSiswa(): Siswa {
        return siswa ?: Siswa.empty
    }

    fun isSiswaAttend() = presensi.isChecked

    fun setSiswaAttend() {
        presensi.isChecked = true
    }

    fun getPresensi(jadwalKelasId: Int): Presensi {
        return Presensi(jadwalKelasId, siswa?.id ?: "")
    }

    private fun setNamaSiswa(name: String) {
        presensi.text = name
    }

    fun disableView() {
        presensi.isEnabled = false
    }

    fun enableView() {
        presensi.isEnabled = true
    }
}