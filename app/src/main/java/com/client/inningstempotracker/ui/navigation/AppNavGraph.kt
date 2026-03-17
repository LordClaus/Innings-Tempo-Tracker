package com.client.inningstempotracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.client.inningstempotracker.ui.analytics.AnalyticsScreen
import com.client.inningstempotracker.ui.detail.InningsDetailScreen
import com.client.inningstempotracker.ui.edit.EditInningScreen
import com.client.inningstempotracker.ui.home.HomeScreen
import com.client.inningstempotracker.ui.library.InningsLibraryScreen
import com.client.inningstempotracker.ui.match.CreateMatchScreen
import com.client.inningstempotracker.ui.onboarding.OnboardingAnalyticsDemoScreen
import com.client.inningstempotracker.ui.onboarding.OnboardingInputDemoScreen
import com.client.inningstempotracker.ui.onboarding.OnboardingStartScreen
import com.client.inningstempotracker.ui.over.OverInputScreen
import com.client.inningstempotracker.ui.preloader.PreloaderScreen
import com.client.inningstempotracker.ui.settings.SettingsScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onThemeChanged: (Boolean) -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Preloader.route,
        modifier = modifier
    ) {
        composable(Screen.Preloader.route) {
            PreloaderScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.OnboardingStart.route) {
                        popUpTo(Screen.Preloader.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Preloader.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.OnboardingStart.route) {
            OnboardingStartScreen(
                onNext = { navController.navigate(Screen.OnboardingInputDemo.route) },
                onSkip = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.OnboardingStart.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.OnboardingInputDemo.route) {
            OnboardingInputDemoScreen(
                onNext = { navController.navigate(Screen.OnboardingAnalyticsDemo.route) },
                onBack = { navController.popBackStack() },
                onSkip = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.OnboardingStart.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.OnboardingAnalyticsDemo.route) {
            OnboardingAnalyticsDemoScreen(
                onFinish = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.OnboardingStart.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onCreateMatch = { navController.navigate(Screen.CreateMatch.route) },
                onMatchClick = { matchId ->
                    navController.navigate(Screen.InningsDetail.createRoute(matchId))
                }
            )
        }

        composable(Screen.CreateMatch.route) {
            CreateMatchScreen(
                onMatchCreated = { matchId ->
                    navController.navigate(Screen.OverInput.createRoute(matchId)) {
                        popUpTo(Screen.CreateMatch.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.OverInput.route,
            arguments = listOf(navArgument("matchId") { type = NavType.IntType })
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getInt("matchId") ?: return@composable
            OverInputScreen(
                matchId = matchId,
                onDone = { id ->
                    navController.navigate(Screen.InningsDetail.createRoute(id)) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.InningsLibrary.route) {
            InningsLibraryScreen(
                onMatchClick = { matchId ->
                    navController.navigate(Screen.InningsDetail.createRoute(matchId))
                }
            )
        }

        composable(
            route = Screen.InningsDetail.route,
            arguments = listOf(navArgument("matchId") { type = NavType.IntType })
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getInt("matchId") ?: return@composable
            InningsDetailScreen(
                matchId = matchId,
                onEdit = { id ->
                    navController.navigate(Screen.EditInning.createRoute(id))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditInning.route,
            arguments = listOf(navArgument("matchId") { type = NavType.IntType })
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getInt("matchId") ?: return@composable
            EditInningScreen(
                matchId = matchId,
                onSaved = { id ->
                    navController.navigate(Screen.InningsDetail.createRoute(id)) {
                        popUpTo(Screen.EditInning.createRoute(id)) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Analytics.route) {
            AnalyticsScreen()
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onThemeChanged = onThemeChanged)
        }
    }
}