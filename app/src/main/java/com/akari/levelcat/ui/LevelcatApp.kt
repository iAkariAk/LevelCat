@file:OptIn(ExperimentalMaterial3Api::class)

package com.akari.levelcat.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.akari.levelcat.ui.navigation.LevelcatNavHost
import com.akari.levelcat.ui.navigation.NavigationDestination

@Preview
@Composable
fun LevelcatApp() {
    LevelcatNavHost(modifier = Modifier.fillMaxSize())
}

@Composable
fun LevelcatTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) IconButton(onClick = navigateUp) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
            }
        },
        title = { Text(title) },
    )
}

@Composable
fun LevelcatTopAppBar(
    destination: NavigationDestination,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    LevelcatTopAppBar(
        title = destination.title,
        canNavigateBack = destination.canNavigateUp,
        navigateUp = navigateUp,
        modifier = modifier
    )
}