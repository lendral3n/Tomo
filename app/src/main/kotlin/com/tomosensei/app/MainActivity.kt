package com.tomosensei.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tomosensei.core.designsystem.theme.TomoSenseiTheme
import com.tomosensei.feature.drill.ui.DrillScreen
import com.tomosensei.feature.stats.ui.StatsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TomoSenseiTheme {
                TomoSenseiApp()
            }
        }
    }
}

private enum class TopLevelRoute(val route: String, val label: String, val icon: ImageVector) {
    Drill("drill", "Drill", Icons.Default.Style),
    Stats("stats", "Stats", Icons.Default.Insights),
}

@Composable
private fun TomoSenseiApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                TopLevelRoute.entries.forEach { route ->
                    val selected = currentDestination?.hierarchy?.any { it.route == route.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(route.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(route.icon, contentDescription = route.label) },
                        label = { Text(route.label) },
                    )
                }
            }
        },
    ) { padding ->
        TomoNavHost(
            startDestination = TopLevelRoute.Drill.route,
            padding = padding,
            navHostController = navController,
        )
    }
}

@Composable
private fun TomoNavHost(
    startDestination: String,
    padding: PaddingValues,
    navHostController: androidx.navigation.NavHostController,
) {
    NavHost(
        navController = navHostController,
        startDestination = startDestination,
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
    ) {
        composable(TopLevelRoute.Drill.route) { DrillScreen() }
        composable(TopLevelRoute.Stats.route) { StatsScreen() }
    }
}
