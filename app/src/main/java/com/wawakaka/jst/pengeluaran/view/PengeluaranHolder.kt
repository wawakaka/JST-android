package com.wawakaka.jst.pengeluaran.view

import android.view.View
import com.wawakaka.jst.R
import com.wawakaka.jst.pengeluaran.model.Pengeluaran
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.IHolder
import kotlinx.android.synthetic.main.item_pengeluaran.view.*

/**
 * Created by wawakaka on 2/1/2018.
 */
data class PengeluaranHolder(private val model: Pengeluaran) : AbstractFlexibleItem<PengeluaranViewHolder>(), IHolder<Pengeluaran> {

    override fun getModel(): Pengeluaran = model

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): PengeluaranViewHolder = PengeluaranViewHolder(view, adapter)

    override fun getLayoutRes(): Int = R.layout.item_pengeluaran

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, holder: PengeluaranViewHolder, position: Int, payloads: MutableList<Any?>?) {
        setId(holder)
        setTanggal(holder)
        setStatus(holder)
    }

    private fun setId(holder: PengeluaranViewHolder) {
        holder.itemView.id_pengeluaran.text = model.id.toString()
    }

    private fun setTanggal(holder: PengeluaranViewHolder) {
        holder.itemView.tanggal.text = model.tanggal.toString()
    }

    private fun setStatus(holder: PengeluaranViewHolder) {
        holder.itemView.status.text = model.status.toString()
    }
}