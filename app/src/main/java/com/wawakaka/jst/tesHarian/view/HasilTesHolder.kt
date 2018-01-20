package com.wawakaka.jst.tesHarian.view

import android.view.View
import com.wawakaka.jst.R
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.tesHarian.model.HasilTesHarian
import com.wawakaka.jst.tesHarian.presenter.TesHarianPresenter
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.IHolder
import kotlinx.android.synthetic.main.hasil_tes_item.view.*

/**
 * Created by wawakaka on 11/27/2017.
 */
data class HasilTesHolder(private val model: HasilTesHarian,
                          private val harianPresenter: TesHarianPresenter) : AbstractFlexibleItem<HasilTesViewHolder>(), IHolder<HasilTesHarian> {

    private val context = JstApplication.component.provideApplicationContext()

    override fun getModel(): HasilTesHarian = model

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): HasilTesViewHolder = HasilTesViewHolder(view, adapter)

    override fun getLayoutRes(): Int = R.layout.hasil_tes_item

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, holder: HasilTesViewHolder, position: Int, payloads: MutableList<Any?>?) {
        setNama(holder)
        setHasil(holder)
    }

    fun getNama() = harianPresenter.getSiswaById(model.siswaId).nama

    fun getHasil() = model.hasil

    private fun setNama(holder: HasilTesViewHolder) {
        holder.itemView.nama.text = harianPresenter.getSiswaById(model.siswaId).nama
    }

    private fun setHasil(holder: HasilTesViewHolder) {
        if (holder.itemView.hasil.text.isNullOrBlank()) {
            holder.itemView.hasil.text = context.getString(R.string.daily_test_result_not_scored_yet)
        } else {
            holder.itemView.hasil.text = model.hasil
        }
    }
}