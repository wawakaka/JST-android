package com.wawakaka.jst.admin.view

import android.view.View
import com.wawakaka.jst.R
import com.wawakaka.jst.admin.model.Sekolah
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.IHolder
import kotlinx.android.synthetic.main.item_sekolah.view.*

/**
 * Created by babang on 1/21/2018.
 */
data class SekolahHolder(private val model: Sekolah) : AbstractFlexibleItem<SekolahViewHolder>(), IHolder<Sekolah> {

    override fun getModel(): Sekolah = model

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): SekolahViewHolder = SekolahViewHolder(view, adapter)

    override fun getLayoutRes(): Int = R.layout.item_sekolah

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, holder: SekolahViewHolder, position: Int, payloads: MutableList<Any?>?) {
        setNama(holder)
    }

    private fun setNama(holder: SekolahViewHolder) {
        holder.itemView.nama_sekolah.text = model.nama
    }
}