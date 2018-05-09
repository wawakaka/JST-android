package com.wawakaka.jst.dashboard.view

import android.view.View
import com.wawakaka.jst.R
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.view.makeGone
import com.wawakaka.jst.base.view.makeVisible
import com.wawakaka.jst.dashboard.model.Kelas
import com.wawakaka.jst.dashboard.presenter.DashboardPresenter
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.IHolder
import kotlinx.android.synthetic.main.item_kelas.view.*

/**
 * Created by wawakaka on 11/22/2017.
 */
data class KelasHolder(private val model: Kelas) : AbstractFlexibleItem<ClassViewHolder>(), IHolder<Kelas> {

    private val dashboardPresenter: DashboardPresenter by lazy {
        JstApplication.component.provideDashboardPresenter()
    }

    override fun getModel(): Kelas = model

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): ClassViewHolder = ClassViewHolder(view, adapter)

    override fun getLayoutRes(): Int = R.layout.item_kelas

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, holder: ClassViewHolder, position: Int, payloads: MutableList<Any?>?) {
        setGuru(holder)
        setBidang(holder)
        setSekolahIfEvent(holder)
    }

    private fun setGuru(holder: ClassViewHolder) {
        holder.itemView.guru_kelas.text = dashboardPresenter.getUser().nama
    }

    private fun setBidang(holder: ClassViewHolder) {
        if (model.bidangNama?.isEmpty() == true) {
            holder.itemView.bidang_kelas.text = JstApplication.component.provideApplicationContext().getString(R.string.invalid_data)
        } else {
            holder.itemView.bidang_kelas.text = model.bidangNama
        }
    }

    private fun setSekolahIfEvent(holder: ClassViewHolder) {
        if (model.eventId != null) {
            holder.itemView.sekolah_kelas.text = dashboardPresenter.getEventList().find { it.id == model.eventId }?.sekolahNama
            holder.itemView.sekolah_kelas.makeVisible()
        } else {
            holder.itemView.sekolah_kelas.makeGone()
        }
    }
}