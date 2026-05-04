package com.tomosensei.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tomosensei.core.designsystem.components.BottomTomoNav
import com.tomosensei.core.designsystem.components.DefaultTomoNav
import com.tomosensei.core.designsystem.theme.TomoSenseiTheme
import com.tomosensei.core.designsystem.theme.WashiCream
import com.tomosensei.app.ui.SettingsScreen
import com.tomosensei.feature.chat.ui.ChatScreen
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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = WashiCream,
                ) {
                    TomoSenseiAppRoot()
                }
            }
        }
    }
}

@Composable
private fun TomoSenseiAppRoot() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val activeId = backStackEntry?.destination?.hierarchy
        ?.firstOrNull { it.route in DefaultTomoNav.map(com.tomosensei.core.designsystem.components.TomoNavItem::id) }
        ?.route
        ?: "drill"
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        TomoNavHost(
            navHostController = navController,
            modifier = Modifier.fillMaxSize(),
        )
        BottomTomoNav(
            items = DefaultTomoNav,
            activeId = activeId,
            onSelect = { item ->
                if (!item.enabled) {
                    Toast.makeText(context, "Segera hadir", Toast.LENGTH_SHORT).show()
                    return@BottomTomoNav
                }
                navController.navigate(item.id) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun TomoNavHost(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navHostController,
        startDestination = "drill",
        modifier = modifier,
    ) {
        composable("drill") { DrillScreen() }
        composable("chat") { ChatScreen() }
        composable("stats") { StatsScreen() }
        composable("settings") { SettingsScreen() }
    }
}
