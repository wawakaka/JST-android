package com.wawakaka.jst.dashboard.view

import android.view.View
import com.wawakaka.jst.R
import com.wawakaka.jst.dashboard.model.Kelas
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.IHolder
import kotlinx.android.synthetic.main.class_item.view.*

/**
 * Created by wawakaka on 11/22/2017.
 */
data class ClassHolder(private val model: Kelas) : AbstractFlexibleItem<ClassViewHolder>(), IHolder<Kelas> {

    override fun getModel(): Kelas = model

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): ClassViewHolder = ClassViewHolder(view, adapter)

    override fun getLayoutRes(): Int = R.layout.class_item

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, holder: ClassViewHolder, position: Int, payloads: MutableList<Any?>?) {
        setId(holder)
        setBidang(holder)
    }

    private fun setId(holder: ClassViewHolder) {
        holder.itemView.id_kelas.text = model.id.toString()
    }

    private fun setBidang(holder: ClassViewHolder) {
        holder.itemView.bidang_kelas.text = model.bidang.toString()
    }
}