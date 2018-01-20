package com.wawakaka.jst.admin.view

import android.view.View
import com.wawakaka.jst.R
import com.wawakaka.jst.dashboard.model.JadwalKelas
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.IHolder

/**
 * Created by wawakaka on 12/30/2017.
 */
data class JadwalKelasHolder(private val model: JadwalKelas) : AbstractFlexibleItem<JadwalKelasViewHolder>(), IHolder<JadwalKelas> {

    override fun getModel(): JadwalKelas = model

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): JadwalKelasViewHolder = JadwalKelasViewHolder(view, adapter)

    override fun getLayoutRes(): Int = R.layout.item_kelas

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, holder: JadwalKelasViewHolder, position: Int, payloads: MutableList<Any?>?) {

    }
}