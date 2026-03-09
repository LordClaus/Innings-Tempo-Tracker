package com.client.inningstempotracker.ui.navigation

sealed class Screen(val route: String) {
    object Preloader : Screen("preloader")
    object OnboardingStart : Screen("onboarding_start")
    object OnboardingInputDemo : Screen("onboarding_input_demo")
    object OnboardingAnalyticsDemo : Screen("onboarding_analytics_demo")
    object Home : Screen("home")
    object CreateMatch : Screen("create_match")
    object OverInput : Screen("over_input/{matchId}") {
        fun createRoute(matchId: Int) = "over_input/$matchId"
    }
    object InningsLibrary : Screen("innings_library")
    object InningsDetail : Screen("innings_detail/{matchId}") {
        fun createRoute(matchId: Int) = "innings_detail/$matchId"
    }
    object EditInning : Screen("edit_inning/{matchId}") {
        fun createRoute(matchId: Int) = "edit_inning/$matchId"
    }
    object Analytics : Screen("analytics")
    object Settings : Screen("settings")
}