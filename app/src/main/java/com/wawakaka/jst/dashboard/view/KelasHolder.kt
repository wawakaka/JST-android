package com.wawakaka.jst.dashboard.view

import android.view.View
import com.wawakaka.jst.R
import com.wawakaka.jst.dashboard.model.Kelas
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.IHolder
import kotlinx.android.synthetic.main.item_kelas.view.*

/**
 * Created by wawakaka on 11/22/2017.
 */
data class KelasHolder(private val model: Kelas) : AbstractFlexibleItem<KelasViewHolder>(), IHolder<Kelas> {

    override fun getModel(): Kelas = model

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): KelasViewHolder = KelasViewHolder(view, adapter)

    override fun getLayoutRes(): Int = R.layout.item_kelas

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, holder: KelasViewHolder, position: Int, payloads: MutableList<Any?>?) {
        setId(holder)
        setBidang(holder)
    }

    private fun setId(holder: KelasViewHolder) {
        holder.itemView.id_kelas.text = model.id.toString()
    }

    private fun setBidang(holder: KelasViewHolder) {
        holder.itemView.bidang_kelas.text = model.bidang.toString()
    }
}