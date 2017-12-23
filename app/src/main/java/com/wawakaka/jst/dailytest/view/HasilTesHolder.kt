package com.wawakaka.jst.dailytest.view

import android.view.View
import com.wawakaka.jst.R
import com.wawakaka.jst.dailytest.model.HasilTesHarian
import com.wawakaka.jst.dailytest.presenter.DailyTestPresenter
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.IHolder
import kotlinx.android.synthetic.main.hasil_tes_item.view.*

/**
 * Created by wawakaka on 11/27/2017.
 */
data class HasilTesHolder(private val model: HasilTesHarian,
                          private val presenter: DailyTestPresenter) : AbstractFlexibleItem<HasilTesViewHolder>(), IHolder<HasilTesHarian> {

    override fun getModel(): HasilTesHarian = model

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): HasilTesViewHolder = HasilTesViewHolder(view, adapter)

    override fun getLayoutRes(): Int = R.layout.hasil_tes_item

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, holder: HasilTesViewHolder, position: Int, payloads: MutableList<Any?>?) {
        setNama(holder)
        setHasil(holder)
    }

    private fun setNama(holder: HasilTesViewHolder) {
        holder.itemView.nama.text = presenter.getSiswaById(model.siswaId).nama
    }

    private fun setHasil(holder: HasilTesViewHolder) {
        holder.itemView.hasil.text = model.hasil
    }
}