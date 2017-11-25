package com.wawakaka.jst.base

import android.content.Context
import com.google.gson.Gson
import com.wawakaka.jst.base.utils.GsonModule
import com.wawakaka.jst.base.utils.LogUtilsModule
import com.wawakaka.jst.dashboard.DashboardModule
import com.wawakaka.jst.dashboard.presenter.DashboardPresenter
import com.wawakaka.jst.datasource.local.LocalRequestManager
import com.wawakaka.jst.datasource.local.LocalRequestModule
import com.wawakaka.jst.datasource.local.preference.PreferenceApiModule
import com.wawakaka.jst.datasource.server.ServerRequestManager
import com.wawakaka.jst.datasource.server.ServerRequestModule
import com.wawakaka.jst.datasource.server.retrofit.RetrofitModule
import com.wawakaka.jst.login.LoginModule
import com.wawakaka.jst.login.presenter.LoginPresenter
import com.wawakaka.jst.navigation.NavigationModule
import com.wawakaka.jst.navigation.presenter.NavigationPresenter
import com.wawakaka.jst.onboarding.OnBoardingModule
import com.wawakaka.jst.onboarding.presenter.OnBoardingPresenter
import dagger.Component
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by wawakaka on 7/25/2017.
 */
@Singleton
@Component(modules = arrayOf(
    JstApplicationModule::class,
    LogUtilsModule::class,
    GsonModule::class,
    RetrofitModule::class,
    ServerRequestModule::class,
    PreferenceApiModule::class,
    LocalRequestModule::class,
    LoginModule::class,
    OnBoardingModule::class,
    DashboardModule::class,
    NavigationModule::class,
    JobModule::class
))
interface JstComponent {

    fun provideApplicationContext(): Context
    fun provideServerRequestManager(): ServerRequestManager
    fun proviceLoginPresenter(): LoginPresenter
    fun provideLocalRequestManager(): LocalRequestManager
    fun provideOnBoardingPresenter(): OnBoardingPresenter
    fun provideDashboardPresenter(): DashboardPresenter
    fun provideNavigationPresenter(): NavigationPresenter

    @Named("lower_case_with_underscores_gson")
    fun provideGson(): Gson
}