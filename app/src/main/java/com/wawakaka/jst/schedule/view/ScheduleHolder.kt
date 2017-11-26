package com.wawakaka.jst.schedule.view

import android.view.View
import com.wawakaka.jst.R
import com.wawakaka.jst.base.utils.DateUtils
import com.wawakaka.jst.dashboard.model.JadwalKelas
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.IHolder
import kotlinx.android.synthetic.main.schedule_item.view.*

/**
 * Created by wawakaka on 11/25/2017.
 */
data class ScheduleHolder(private val model: JadwalKelas) : AbstractFlexibleItem<ScheduleViewHolder>(), IHolder<JadwalKelas> {

    override fun getModel(): JadwalKelas = model

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): ScheduleViewHolder = ScheduleViewHolder(view, adapter)

    override fun getLayoutRes(): Int = R.layout.schedule_item

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, holder: ScheduleViewHolder, position: Int, payloads: MutableList<Any?>?) {
        setDate(holder)
    }

    private fun setDate(holder: ScheduleViewHolder) {
        holder.itemView.schedule_item_date.text = DateUtils.getFormattedDate(model.tanggal ?: "")
    }
}