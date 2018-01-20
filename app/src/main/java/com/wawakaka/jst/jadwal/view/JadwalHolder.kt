package com.wawakaka.jst.jadwal.view

import android.support.v4.content.ContextCompat
import android.view.View
import com.wawakaka.jst.R
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.utils.DateUtils
import com.wawakaka.jst.dashboard.model.JadwalKelas
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.IHolder
import kotlinx.android.synthetic.main.item_jadwal.view.*

/**
 * Created by wawakaka on 11/25/2017.
 */
data class JadwalHolder(private val model: JadwalKelas) : AbstractFlexibleItem<ScheduleViewHolder>(), IHolder<JadwalKelas> {

    private val context = JstApplication.component.provideApplicationContext()

    override fun getModel(): JadwalKelas = model

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): ScheduleViewHolder = ScheduleViewHolder(view, adapter)

    override fun getLayoutRes(): Int = R.layout.item_jadwal

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, holder: ScheduleViewHolder, position: Int, payloads: MutableList<Any?>?) {
        setDate(holder)
        setTime(holder)
    }

    private fun setDate(holder: ScheduleViewHolder) {
        holder.itemView.schedule_item_date.text = DateUtils.getFormattedDate(model.tanggal ?: "")
        if (DateUtils.isPast(model.tanggal ?: "")) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.disabled_color))
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
    }

    private fun setTime(holder: ScheduleViewHolder) {
        holder.itemView.schedule_item_time.text = DateUtils.getIso8601Time(model.tanggal ?: "")
    }
}