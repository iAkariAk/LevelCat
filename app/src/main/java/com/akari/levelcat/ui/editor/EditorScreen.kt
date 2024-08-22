@file:OptIn(ExperimentalMaterial3Api::class)

package com.akari.levelcat.ui.editor

import android.util.Log
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.akari.levelcat.integratedlevel.model.LevelProperty
import com.akari.levelcat.integratedlevel.ui.CoffeeCat
import com.akari.levelcat.ui.LevelcatTopAppBar
import com.akari.levelcat.ui.navigation.LocalLevelNavController
import com.akari.levelcat.ui.navigation.NavigationDestination

@Composable
fun EditorScreen(
    modifier: Modifier = Modifier,
    viewModel: EditorViewModel = hiltViewModel(),
) {
    val navController = LocalLevelNavController.current
    val editorUiState = viewModel.editorUiState
    val level = editorUiState.project.level
    Scaffold(
        modifier = modifier,
        topBar = {
            LevelcatTopAppBar(
                destination = NavigationDestination.Editor,
                navigateUp = navController::navigateUp,
                actions = {
                    IconButton(onClick = viewModel::save) {
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
            items(level.components) { component ->
                val cat = remember { CoffeeCat.fromClass(component::class) }
                cat.Editor(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    componet = component,
                    onComponentChange = {
                        Log.d("EditorScreen", "onComponentChange: $it")
                        viewModel.updateComponent(it)
                    },
                    onComponentDelete = { viewModel.removeComponent(component) }
                )
            }
        }
    }
}
