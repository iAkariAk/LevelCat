@file:OptIn(ExperimentalMaterial3Api::class)

package com.akari.levelcat.ui.editor

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.akari.levelcat.level.model.component.Editor
import com.akari.levelcat.level.model.component.LevelPropertyState
import com.akari.levelcat.ui.LevelcatTopAppBar
import com.akari.levelcat.ui.navigation.LocalNavController
import com.akari.levelcat.ui.navigation.NavigationDestination
import com.akari.levelcat.util.logger
import kotlinx.coroutines.launch

@Composable
fun EditorScreen(
    modifier: Modifier = Modifier,
    viewModel: EditorViewModel = hiltViewModel(),
) {
    val navController = LocalNavController.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val editorUiState by viewModel.editorUiState.collectAsState()
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            LevelcatTopAppBar(
                destination = NavigationDestination.Editor,
                navigateUp = {
                    coroutineScope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "Are you to exit without save ?",
                            duration = SnackbarDuration.Indefinite,
                            withDismissAction = true
                        )
                        if (result == SnackbarResult.Dismissed) {
                            navController.navigateUp()
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.save()
                    }) {
                        Icon(Icons.Outlined.Save, contentDescription = "save")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.addComponent(
                    LevelPropertyState(
                        allowedZombies = listOf(),
                        background = "sem",
                        creator = "te",
                        easyUpgrade = "euismod",
                        initPlantColumn = "platea",
                        name = "Neva Wilkerson",
                        numWaves = "class",
                        startingSun = "necessitatibus",
                        startingTime = "consequat",
                        startingWave = "tamquam",
                        wavesPerFlag = "dico"
                    )
                )
            }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { innerPadding ->

        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(editorUiState.components) { componentState ->
                Editor(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    componentState = componentState,
                    onComponentStateChange = {
                        logger.info("onComponentChange: $it")
                        viewModel.updateComponent(it)
                    },
                    onComponentDelete = { viewModel.removeComponent(componentState) }
                )
            }
        }
    }
}
