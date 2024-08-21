package com.akari.levelcat.ui.editor

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.akari.levelcat.integratedlevel.model.LevelProperty
import com.akari.levelcat.ui.LevelcatTopAppBar
import com.akari.levelcat.ui.navigation.LocalLevelNavController
import com.akari.levelcat.ui.navigation.NavigationDestination

@Composable
fun EditorScreen(
    modifier: Modifier = Modifier,
    viewModel: EditorViewModel = hiltViewModel(),
) {
    val navController = LocalLevelNavController.current
    val editorUiState by viewModel.uiState.collectAsState()
    val level = editorUiState.project.level
    Scaffold(
        modifier = modifier,
        topBar = {
            LevelcatTopAppBar(
                destination = NavigationDestination.Editor,
                navigateUp = navController::navigateUp
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.addLevelComponent(LevelProperty())
            }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(level.components) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    item.Editor(modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}