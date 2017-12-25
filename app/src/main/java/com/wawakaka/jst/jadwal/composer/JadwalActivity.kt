package com.wawakaka.jst.jadwal.composer

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.dashboard.model.JadwalKelas
import com.wawakaka.jst.dashboard.model.Kelas
import com.wawakaka.jst.jadwal.view.JadwalHolder
import com.wawakaka.jst.kelas.composer.KelasActivity
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_jadwal.*

class JadwalActivity : BaseActivity(), FlexibleAdapter.OnItemClickListener {

    companion object {
        const val SCHEDULE_INFO = "schedule_info"
    }

    private var kelas: Kelas? = null
    private val list: MutableList<AbstractFlexibleItem<*>> = mutableListOf()
    private var adapter = FlexibleAdapter(list, this, true)

    init {
        initLayout()
        initScheduleList()
    }

    private fun initLayout() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                kelas = intent.getSerializableExtra(SCHEDULE_INFO) as Kelas
                setContentView(R.layout.activity_jadwal)
                initToolbar()
            }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initScheduleList() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { initLayoutManager() }
            .observeOn(Schedulers.io())
            .map { kelas?.jadwalKelas?.sortedBy { it.tanggal }?.toMutableList() }
            .filter { it.isNotEmpty() }
            .observeOn(Schedulers.computation())
            .doOnNext { scheduleHolderList(it ?: mutableListOf()) }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                adapter.updateDataSet(list)
            }
    }

    private fun initLayoutManager() {
        val layoutManager = LinearLayoutManager(this)
        list_schedule_container.layoutManager = layoutManager
        list_schedule_container.setHasFixedSize(true)
        list_schedule_container.adapter = adapter
    }

    private fun scheduleHolderList(jadwalKelas: MutableList<JadwalKelas>) {
        list.clear()
        jadwalKelas.forEach { list.add(JadwalHolder(it)) }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        return if (id == android.R.id.home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(position: Int): Boolean {
        val item = adapter.getItem(position)
        if (item is JadwalHolder) {
            launchClassActivity(item.model.id!!)
            return true
        }
        return false
    }

    private fun launchClassActivity(idJadwalKelas: Int) {
        val intent = Intent(this, KelasActivity::class.java)
        intent.putExtra(KelasActivity.EXTRA_KELAS, kelas)
        intent.putExtra(KelasActivity.EXTRA_ID_JADWAL, idJadwalKelas)
        startActivity(intent)
    }
}
