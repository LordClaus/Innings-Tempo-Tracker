package com.client.inningstempotracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.client.inningstempotracker.theme.AppTheme
import com.client.inningstempotracker.ui.navigation.AppNavGraph
import com.client.inningstempotracker.ui.navigation.Screen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }

            AppTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val bottomNavRoutes = listOf(
                    Screen.Home.route,
                    Screen.InningsLibrary.route,
                    Screen.Analytics.route,
                    Screen.Settings.route
                )

                val showBottomBar = currentRoute in bottomNavRoutes

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = currentRoute == Screen.Home.route,
                                    onClick = {
                                        navController.navigate(Screen.Home.route) {
                                            popUpTo(Screen.Home.route) { inclusive = false }
                                            launchSingleTop = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                                    label = { Text("Home") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Screen.InningsLibrary.route,
                                    onClick = {
                                        navController.navigate(Screen.InningsLibrary.route) {
                                            popUpTo(Screen.Home.route) { inclusive = false }
                                            launchSingleTop = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.LibraryBooks, contentDescription = null) },
                                    label = { Text("Library") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Screen.Analytics.route,
                                    onClick = {
                                        navController.navigate(Screen.Analytics.route) {
                                            popUpTo(Screen.Home.route) { inclusive = false }
                                            launchSingleTop = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.Analytics, contentDescription = null) },
                                    label = { Text("Analytics") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Screen.Settings.route,
                                    onClick = {
                                        navController.navigate(Screen.Settings.route) {
                                            popUpTo(Screen.Home.route) { inclusive = false }
                                            launchSingleTop = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                                    label = { Text("Settings") }
                                )
                            }
                        }
                    }
                ) { paddingValues ->
                    AppNavGraph(
                        navController = navController,
                        modifier = Modifier.padding(paddingValues),
                        onThemeChanged = { isDarkTheme = it }
                    )
                }
            }
        }
    }
}