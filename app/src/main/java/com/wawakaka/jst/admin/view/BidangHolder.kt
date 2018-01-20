package com.wawakaka.jst.admin.view

import android.view.View
import com.wawakaka.jst.R
import com.wawakaka.jst.dashboard.model.Bidang
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.IHolder
import kotlinx.android.synthetic.main.item_bidang.view.*

/**
 * Created by wawakaka on 12/29/2017.
 */
data class BidangHolder(private val model: Bidang) : AbstractFlexibleItem<BidangViewHolder>(), IHolder<Bidang> {

    override fun getModel(): Bidang = model

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): BidangViewHolder = BidangViewHolder(view, adapter)

    override fun getLayoutRes(): Int = R.layout.item_bidang

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, holder: BidangViewHolder, position: Int, payloads: MutableList<Any?>?) {
        setNama(holder)
    }

    private fun setNama(holder: BidangViewHolder) {
        holder.itemView.nama_bidang.text = model.nama
    }
}