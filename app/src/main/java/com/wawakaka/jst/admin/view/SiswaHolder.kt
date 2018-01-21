package com.wawakaka.jst.admin.view

import android.content.Context
import android.view.View
import com.wawakaka.jst.R
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.dashboard.model.Siswa
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.IHolder
import kotlinx.android.synthetic.main.item_siswa.view.*

/**
 * Created by wawakaka on 12/30/2017.
 */
data class SiswaHolder(private val model: Siswa) : AbstractFlexibleItem<SiswaViewHolder>(), IHolder<Siswa> {

    private val context: Context = JstApplication.component.provideApplicationContext()

    override fun getModel(): Siswa = model

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): SiswaViewHolder = SiswaViewHolder(view, adapter)

    override fun getLayoutRes(): Int = R.layout.item_siswa

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, holder: SiswaViewHolder, position: Int, payloads: MutableList<Any?>?) {
        setNama(holder)
        setSekolah(holder)
        setkelasSiswa(holder)
        setStatus(holder)
    }

    private fun setNama(holder: SiswaViewHolder) {
        holder.itemView.nama_siswa.text = model.nama
    }

    private fun setSekolah(holder: SiswaViewHolder) {
        holder.itemView.sekolah_siswa.text = model.sekolahNama
    }

    private fun setkelasSiswa(holder: SiswaViewHolder) {
        holder.itemView.kelas_siswa.text = model.kelas
    }

    private fun setStatus(holder: SiswaViewHolder) {
        if (model.isActive == true) {
            holder.itemView.is_active_siswa.text = context.getString(R.string.active)
        } else {
            holder.itemView.is_active_siswa.text = context.getString(R.string.not_active)
        }
    }
}