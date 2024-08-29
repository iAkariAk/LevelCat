package com.akari.levelcat.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

val LocalNavController = compositionLocalOf<NavHostController> { error("Impossible") }
val LocalBackStack = compositionLocalOf<NavBackStackEntry> { error("Impossible") }

@Composable
fun LevelcatNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = NavigationDestination.Home.route,
        enterTransition = {
            fadeIn(
                animationSpec = tween(1000)
            )
        },
        exitTransition = {
            fadeOut(
                animationSpec = tween(1000)
            )
        }
    ) {
        NavigationDestination.entries.forEach { destination ->
            composable(
                route = destination.route,
                arguments = destination.arguments
            ) { backStackEntry ->
                CompositionLocalProvider(
                    LocalNavController provides navController,
                    LocalBackStack provides backStackEntry
                ) {
                    destination.screen(Modifier.fillMaxSize())
                }
            }
        }
    }
}

