package com.wawakaka.jst.event.view

import android.content.Context
import android.view.View
import com.wawakaka.jst.R
import com.wawakaka.jst.base.utils.DateUtils
import com.wawakaka.jst.event.model.Event
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.IHolder
import kotlinx.android.synthetic.main.item_event.view.*

data class EventHolder(val context: Context,
                       private val model: Event) : AbstractFlexibleItem<EventViewHolder>(), IHolder<Event> {

    override fun getModel(): Event = model

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): EventViewHolder = EventViewHolder(view, adapter)

    override fun getLayoutRes(): Int = R.layout.item_event

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, holder: EventViewHolder, position: Int, payloads: MutableList<Any?>?) {
        setSekolah(holder)
        setTanggalMulai(holder)
        setTanggalSelesai(holder)
    }

    private fun setSekolah(holder: EventViewHolder) {
        if (model.sekolahNama.isNullOrBlank()) {
            holder.itemView.nama_sekolah.text = context.getString(R.string.event_empty)
        } else {
            holder.itemView.nama_sekolah.text = model.sekolahNama
        }
    }

    private fun setTanggalMulai(holder: EventViewHolder) {
        if (model.tanggalMulai.isNullOrBlank()) {
            holder.itemView.tanggal_mulai.text = context.getString(R.string.event_empty)
        } else {
            holder.itemView.tanggal_mulai.text =
                DateUtils.getShortDate(model.tanggalMulai ?: "")
        }
    }

    private fun setTanggalSelesai(holder: EventViewHolder) {
        if (model.tanggalSelesai.isNullOrBlank()) {
            holder.itemView.tanggal_selesai.text = context.getString(R.string.event_empty)
        } else {
            holder.itemView.tanggal_selesai.text =
                DateUtils.getShortDate(model.tanggalSelesai ?: "")
        }
    }

}