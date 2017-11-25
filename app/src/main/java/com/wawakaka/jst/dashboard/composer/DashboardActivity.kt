package com.wawakaka.jst.dashboard.composer

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.dashboard.model.Kelas
import com.wawakaka.jst.dashboard.presenter.DashboardPresenter
import com.wawakaka.jst.dashboard.view.KelasHolder
import com.wawakaka.jst.navigation.composer.NavigationFragment
import com.wawakaka.jst.schedule.composer.ScheduleActivity
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_dashboard.*


class DashboardActivity : BaseActivity(), FlexibleAdapter.OnItemClickListener {

    companion object {
        private val TAG = DashboardActivity::class.java.simpleName
    }

    private val dashboardPresenter: DashboardPresenter by lazy {
        JstApplication.component.provideDashboardPresenter()
    }

    private val list: MutableList<AbstractFlexibleItem<*>> = mutableListOf()
    private var adapter = FlexibleAdapter(list, this, true)

    init {
        initLayout()
        initNavigationDrawer()
        initListKelas()
    }

    private fun initLayout() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                setContentView(R.layout.activity_dashboard)
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
                    NavigationFragment.DRAWER_TYPE_LOGOUT
                )
            }
    }

    private fun initListKelas() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { initLayoutManager() }
            .observeOn(Schedulers.io())
            .flatMap { dashboardPresenter.loadClassObservable() }
            .filter { it != null && it.isNotEmpty() }
            .observeOn(Schedulers.computation())
            .doOnNext {
                createKelasHolderList(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                adapter.updateDataSet(list)
            }
    }

    private fun initLayoutManager() {
        val layoutManager = LinearLayoutManager(this)
        list_kelas_container.layoutManager = layoutManager
        list_kelas_container.setHasFixedSize(true)
        list_kelas_container.adapter = adapter
    }

    private fun createKelasHolderList(kelas: MutableList<Kelas>) {
        list.clear()
        kelas.map { list.add(KelasHolder(it)) }
    }

    override fun onItemClick(position: Int): Boolean {

        val item = adapter.getItem(position)
        if (item is KelasHolder) {
            launchScheduleActivity(item.model)
            return true
        }
        return false
    }

    private fun launchScheduleActivity(kelas: Kelas) {
        val intent = Intent(this, ScheduleActivity::class.java)
        intent.putExtra(ScheduleActivity.KELAS_INFO, kelas)
        startActivity(intent)
    }

}
