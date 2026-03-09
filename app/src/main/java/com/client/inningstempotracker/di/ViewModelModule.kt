package com.client.inningstempotracker.di

import com.client.inningstempotracker.ui.analytics.AnalyticsViewModel
import com.client.inningstempotracker.ui.detail.InningsDetailViewModel
import com.client.inningstempotracker.ui.edit.EditInningViewModel
import com.client.inningstempotracker.ui.home.HomeViewModel
import com.client.inningstempotracker.ui.library.InningsLibraryViewModel
import com.client.inningstempotracker.ui.match.CreateMatchViewModel
import com.client.inningstempotracker.ui.onboarding.OnboardingViewModel
import com.client.inningstempotracker.ui.over.OverInputViewModel
import com.client.inningstempotracker.ui.preloader.PreloaderViewModel
import com.client.inningstempotracker.ui.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::PreloaderViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::CreateMatchViewModel)
    viewModelOf(::OverInputViewModel)
    viewModelOf(::InningsLibraryViewModel)
    viewModelOf(::InningsDetailViewModel)
    viewModelOf(::EditInningViewModel)
    viewModelOf(::AnalyticsViewModel)
    viewModelOf(::SettingsViewModel)
}