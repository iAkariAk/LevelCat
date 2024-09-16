@file:OptIn(ExperimentalMaterial3Api::class)

package com.akari.levelcat.ui.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.akari.levelcat.level.model.component.*
import com.akari.levelcat.ui.LevelcatTopAppBar
import com.akari.levelcat.ui.component.AlertDialogHost
import com.akari.levelcat.ui.component.AlertDialogHostState
import com.akari.levelcat.ui.component.animateEnter
import com.akari.levelcat.ui.navigation.LocalNavController
import com.akari.levelcat.ui.navigation.NavigationDestination
import com.akari.levelcat.ui.util.UiEventHandler
import kotlinx.coroutines.launch

@Composable
fun EditorScreen(
    modifier: Modifier = Modifier,
    viewModel: EditorViewModel = hiltViewModel(),
) {
    val navController = LocalNavController.current
    val coroutineScope = rememberCoroutineScope()
    val intentDialogHostState = remember { AlertDialogHostState<EditorIntent>() }
    val snackbarHostState = remember { SnackbarHostState() }
    val editorUiState by viewModel.editorUiState.collectAsState()
    UiEventHandler(
        viewModel = viewModel,
        snackbarHostState = snackbarHostState
    )
    AlertDialogHost(intentDialogHostState)
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            LevelcatTopAppBar(
                destination = NavigationDestination.Editor,
                navigateUp = {
                    coroutineScope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "Are you sure you want to exit without saving?",
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
            EditorFab(
                intentDialogHostState = intentDialogHostState,
                editorUiState = editorUiState,
                onAddComponent = viewModel::addComponent
            )
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(editorUiState.components) { componentState ->
                Editor(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .animateEnter(),
                    componentState = componentState,
                    onComponentDelete = { viewModel.removeComponent(componentState) }
                )
            }
        }
    }
}

@Composable
private fun EditorFab(
    intentDialogHostState: AlertDialogHostState<EditorIntent>,
    editorUiState: EditorUiState,
    onAddComponent: (ComponentState<*>) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    FloatingActionButton(
        modifier = modifier,
        onClick = {
            coroutineScope.launch {
                intentDialogHostState.alert(
                    transform = {},
                    title = { Text("Selected a component type") },
                    text = { controller ->
                        val components = listOf(
                            "LevelProperty" to LevelPropertyState.Empty,
                            "ColumnPlanting" to ColumnPlantingState,
                            "SeedBank" to SeedBankState.Empty,
                        ).filterNot { (_, state) ->
                            editorUiState.components.any { it::class == state::class }
                        }
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            items(components) { (name, componentState) ->
                                TextButton(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .animateEnter(),
                                    onClick = {
                                        controller.dismiss()
                                        onAddComponent(componentState)
                                    }) {
                                    Text(name)
                                }
                            }

                            if (components.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Empty...")
                                    }
                                }
                            }
                        }
                    },
                )
            }
        }) {
        Icon(Icons.Default.Add, contentDescription = null)
    }
}
