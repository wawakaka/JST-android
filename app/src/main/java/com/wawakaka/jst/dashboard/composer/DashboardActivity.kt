package com.wawakaka.jst.dashboard.composer

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.wawakaka.jst.R
import com.wawakaka.jst.base.JstApplication
import com.wawakaka.jst.base.composer.BaseActivity
import com.wawakaka.jst.base.utils.LogUtils
import com.wawakaka.jst.dashboard.model.Kelas
import com.wawakaka.jst.dashboard.presenter.DashboardPresenter
import com.wawakaka.jst.dashboard.view.ClassHolder
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
        initClassList()
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
//todo add loading view
    private fun initClassList() {
        RxNavi
            .observe(naviComponent, Event.CREATE)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { initLayoutManager() }
            .observeOn(Schedulers.io())
            .flatMap { dashboardPresenter.loadClassObservable() }
            .doOnNext { LogUtils.debug(TAG, it.toString()) }
            .filter { it != null && it.isNotEmpty() }
            .observeOn(Schedulers.computation())
            .doOnNext {
                createClassHolderList(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
            .subscribe {
                adapter.updateDataSet(list)
            }
    }

    private fun initLayoutManager() {
        val layoutManager = LinearLayoutManager(this)
        class_list_container.layoutManager = layoutManager
        class_list_container.setHasFixedSize(true)
        class_list_container.adapter = adapter
    }

    private fun createClassHolderList(kelas: MutableList<Kelas>) {
        list.clear()
        kelas.map { list.add(ClassHolder(it)) }
    }

    override fun onItemClick(position: Int): Boolean {
        val item = adapter.getItem(position)
        if (item is ClassHolder) {
            launchScheduleActivity(item.model)
            return true
        }
        return false
    }

    private fun launchScheduleActivity(kelas: Kelas) {
        val intent = Intent(this, ScheduleActivity::class.java)
        intent.putExtra(ScheduleActivity.SCHEDULE_INFO, kelas)
        startActivity(intent)
    }

}
