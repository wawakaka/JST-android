package com.wawakaka.jst.event.composer

import android.content.Intent
import android.view.MenuItem
import com.jakewharton.rxbinding2.view.RxView
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.base.utils.ExtraUtils
import com.wawakaka.jst.dashboard.composer.DashboardActivity
import com.wawakaka.jst.pengeluaran.composer.PengeluaranActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_event_menu.*

class EventMenuActivity : BaseActivity() {

    private val event: com.wawakaka.jst.event.model.Event? by lazy {
        intent.getSerializableExtra(ExtraUtils.EVENT) as? com.wawakaka.jst.event.model.Event
    }

    init {
        initLayout()
        initKelasButton()
        initPengeluaranButton()
    }

    private fun initLayout() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                setContentView(R.layout.activity_event_menu)
                initToolbar()
            }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initKelasButton() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxView.clicks(event_kelas) }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                launchKelasActivity()
            }
    }

    private fun launchKelasActivity() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra(ExtraUtils.ID_EVENT, event?.id)
        startActivity(intent)
    }

    private fun initPengeluaranButton() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxView.clicks(event_pengeluaran) }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                launchPengeluaranActivity()
            }
    }

    private fun launchPengeluaranActivity() {
        val intent = Intent(this, PengeluaranActivity::class.java)
        intent.putExtra(ExtraUtils.ID_EVENT, event?.id)
        startActivity(intent)
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

}
