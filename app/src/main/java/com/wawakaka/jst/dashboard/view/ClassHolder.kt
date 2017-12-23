package com.wawakaka.jst.dashboard.view

import android.view.View
import com.wawakaka.jst.R
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.view.makeGone
import com.wawakaka.jst.base.view.makeVisible
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
        setIfPrivate(holder)
        setSekolahIfNotPrivate(holder)
    }

    private fun setId(holder: ClassViewHolder) {
        holder.itemView.id_kelas.text = model.id.toString()
    }

    private fun setBidang(holder: ClassViewHolder) {
        if (model.bidangNama?.isEmpty() == true) {
            holder.itemView.bidang_kelas.text = JstApplication.component.provideApplicationContext().getString(R.string.invalid_data)
        }
        holder.itemView.bidang_kelas.text = model.bidangNama
    }

    private fun setIfPrivate(holder: ClassViewHolder) {
        if (model.isPrivate) {
            holder.itemView.private_kelas.makeVisible()
        }
        holder.itemView.private_kelas.makeGone()
    }

    private fun setSekolahIfNotPrivate(holder: ClassViewHolder) {
        if (!model.isPrivate) {
            holder.itemView.sekolah_kelas.text = model.sekolah ?: ""
            holder.itemView.sekolah_kelas.makeVisible()
        }
        holder.itemView.sekolah_kelas.makeGone()
    }
}