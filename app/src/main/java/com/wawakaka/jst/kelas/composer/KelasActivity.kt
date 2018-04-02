package com.wawakaka.jst.kelas.composer

import android.content.Intent
import android.view.MenuItem
import com.jakewharton.rxbinding2.view.RxView
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.base.utils.ExtraUtils.Companion.EXTRA_ID_KELAS
import com.wawakaka.jst.dashboard.model.Kelas
import com.wawakaka.jst.journal.composer.JournalActivity
import com.wawakaka.jst.presensi.composer.PresensiActivity
import com.wawakaka.jst.tesHarian.composer.HasilTesHarianActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_kelas.*

class KelasActivity : BaseActivity() {

    companion object {
        private val TAG = KelasActivity::class.java.simpleName
        const val EXTRA_KELAS = "extra_kelas"
        const val EXTRA_ID_JADWAL = "extra_id_jadwal"
    }

    private var kelas: Kelas? = null
    private var idJadwalKelas: Int? = null

    init {
        initLayout()
        initPresensiButton()
        initDailyTestButton()
        initJournalButton()
    }

    private fun initLayout() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                kelas = intent.getSerializableExtra(EXTRA_KELAS) as Kelas
                idJadwalKelas = intent.getSerializableExtra(EXTRA_ID_JADWAL) as Int
                setContentView(R.layout.activity_kelas)
                initToolbar()
            }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initPresensiButton() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxView.clicks(attendance) }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                launchPresensiActivity()
            }
    }

    private fun launchPresensiActivity() {
        val intent = Intent(this, PresensiActivity::class.java)
        intent.putExtra(EXTRA_KELAS, kelas)
        intent.putExtra(EXTRA_ID_JADWAL, idJadwalKelas)
        startActivity(intent)
    }

    private fun initDailyTestButton() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxView.clicks(daily_test) }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                launchDailyTestActivity()
            }
    }

    private fun launchDailyTestActivity() {
        val intent = Intent(this, HasilTesHarianActivity::class.java)
        intent.putExtra(EXTRA_ID_KELAS, kelas?.id)
        intent.putExtra(EXTRA_ID_JADWAL, idJadwalKelas)
        startActivity(intent)
    }

    private fun initJournalButton() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { RxView.clicks(journal) }
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                launchJournalActivity()
            }
    }

    private fun launchJournalActivity() {
        val intent = Intent(this, JournalActivity::class.java)
        intent.putExtra(EXTRA_ID_JADWAL, idJadwalKelas)
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
