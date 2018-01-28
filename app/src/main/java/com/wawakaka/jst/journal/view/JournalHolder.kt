package com.wawakaka.jst.journal.view

import android.view.View
import com.wawakaka.jst.R
import com.wawakaka.jst.journal.model.Kegiatan
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.IHolder
import kotlinx.android.synthetic.main.item_journal.view.*

/**
 * Created by babang on 1/28/2018.
 */
data class JournalHolder(private val model: Kegiatan) : AbstractFlexibleItem<JournalViewHolder>(), IHolder<Kegiatan> {

    override fun getModel(): Kegiatan = model

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): JournalViewHolder = JournalViewHolder(view, adapter)

    override fun getLayoutRes(): Int = R.layout.item_journal

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, holder: JournalViewHolder, position: Int, payloads: MutableList<Any?>?) {
        setSesiMulai(holder)
        setSesiSelesai(holder)
        setmateri(holder)
        setketerangan(holder)
    }

    private fun setSesiMulai(holder: JournalViewHolder) {
        holder.itemView.sesi_start.text = model.sesiMulai.toString()
    }

    private fun setSesiSelesai(holder: JournalViewHolder) {
        holder.itemView.sesi_end.text = model.sesiSelesai.toString()
    }

    private fun setmateri(holder: JournalViewHolder) {
        holder.itemView.materi.text = model.materi.toString()
    }

    private fun setketerangan(holder: JournalViewHolder) {
        holder.itemView.keterangan.text = model.keterangan.toString()
    }
}