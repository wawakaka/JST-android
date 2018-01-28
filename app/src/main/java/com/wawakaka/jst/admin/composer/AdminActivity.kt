package com.wawakaka.jst.admin.composer

import android.content.Intent
import com.jakewharton.rxbinding2.view.RxView
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.admin.bidang.composer.ManageBidangActivity
import com.wawakaka.jst.admin.jadwalkelas.composer.ManageJadwalKelasActivity
import com.wawakaka.jst.admin.kelas.composer.ManageKelasActivity
import com.wawakaka.jst.admin.sekolah.composer.ManageSekolahActivity
import com.wawakaka.jst.admin.siswa.composer.ManageSiswaActivity
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.navigation.composer.NavigationFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_admin.*

class AdminActivity : BaseActivity() {

    init {
        initLayout()
        initNavigationDrawer()
        initManageKelasButton()
        initManageJadwalKelasButton()
        initManageSekolahButton()
        initManageSiswaButton()
        initManageBidangButton()
    }

    private fun initLayout() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe {
                    setContentView(R.layout.activity_admin)
                }
    }

    private fun initNavigationDrawer() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe {
                    initNavigationDrawer(
                            navigation_drawer_icon,
                            navigation_drawer,
                            R.id.dashboard_drawer_fragment,
                            NavigationFragment.DRAWER_TYPE_ADMIN
                    )
                }
    }


    private fun initManageKelasButton() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { RxView.clicks(admin_kelas) }
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe { launchManageKelasActivity() }
    }

    private fun launchManageKelasActivity() {
        val intent = Intent(this, ManageKelasActivity::class.java)
        startActivity(intent)
    }


    private fun initManageJadwalKelasButton() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { RxView.clicks(admin_jadwal_kelas) }
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe { launchManageJadwalKelasActivity() }
    }

    private fun launchManageJadwalKelasActivity() {
        val intent = Intent(this, ManageJadwalKelasActivity::class.java)
        startActivity(intent)
    }

    private fun initManageSekolahButton() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { RxView.clicks(admin_sekolah) }
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe { launchManageSekolahActivity() }
    }

    private fun launchManageSekolahActivity() {
        val intent = Intent(this, ManageSekolahActivity::class.java)
        startActivity(intent)
    }


    private fun initManageSiswaButton() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { RxView.clicks(admin_siswa) }
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe { launchManageSiswaActivity() }
    }

    private fun launchManageSiswaActivity() {
        val intent = Intent(this, ManageSiswaActivity::class.java)
        startActivity(intent)
    }

    private fun initManageBidangButton() {
        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { RxView.clicks(admin_bidang) }
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .subscribe { launchManageBidangActivity() }
    }

    private fun launchManageBidangActivity() {
        val intent = Intent(this, ManageBidangActivity::class.java)
        startActivity(intent)
    }

}
