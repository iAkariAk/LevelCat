package com.akari.levelcat.ui

import androidx.compose.runtime.Stable
import androidx.navigation.NavController
import androidx.navigation.NavDestination

@Stable
class LevelcatAppState(
    val navController: NavController,
) {
    val currentDestination: NavDestination?
        get() = navController.currentBackStackEntry?.destination
}