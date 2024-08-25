@file:OptIn(ExperimentalMaterial3Api::class)

package com.akari.levelcat.ui.editor

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.akari.levelcat.level.model.component.Editor
import com.akari.levelcat.level.model.component.LevelProperty
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
    val saveOpterations = remember { PresaveOpterationsImpl() }
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
                        saveOpterations.save()
                        viewModel.save()
                    }) {
                        Icon(Icons.Outlined.Save, contentDescription = "save")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.addComponent(LevelProperty())
            }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { innerPadding ->

        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(editorUiState.components) { component ->
                CompositionLocalProvider(LocalPresaveOpterations provides saveOpterations) {
                    Editor(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        component = component,
                        onComponentChange = {
                            logger.info("onComponentChange: $it")
                            viewModel.updateComponent(it)
                        },
                        onComponentDelete = { viewModel.removeComponent(component) }
                    )
                }
            }
        }
    }
}

val LocalPresaveOpterations = compositionLocalOf<PresaveOpterations> { error("Impossible") }

interface PresaveOpterations {
    fun register(listener: () -> Unit)
}

private class PresaveOpterationsImpl : PresaveOpterations {
    private val listeners = mutableListOf<() -> Unit>()
    override fun register(listener: () -> Unit) {
        listeners.add(listener)
    }

    fun save() = listeners.forEach { it() }
}