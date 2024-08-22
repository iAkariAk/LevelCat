@file:OptIn(ExperimentalFoundationApi::class, FlowPreview::class, ExperimentalMaterial3Api::class)

package com.akari.levelcat.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.akari.levelcat.data.model.Project
import com.akari.levelcat.integratedlevel.model.Level
import com.akari.levelcat.ui.LevelcatTopAppBar
import com.akari.levelcat.ui.navigation.LocalLevelNavController
import com.akari.levelcat.ui.navigation.NavigationDestination
import com.akari.levelcat.ui.util.formatMillisecondAsI18nString
import kotlinx.coroutines.FlowPreview

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val navController = LocalLevelNavController.current
    val homeUiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            LevelcatTopAppBar(
                destination = NavigationDestination.Home,
                navigateUp = navController::navigateUp
            )
        },
        floatingActionButton = {
            CreateProjectFloatingActionButton(onCreateProject = viewModel::createProject)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            itemsIndexed(homeUiState.projects) { index, item ->
                Spacer(modifier = Modifier.height(4.dp))
                ProjectItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    item = item,
                    onDeleteProject = { viewModel.deleteProject(item) },
                    onRenameProject = { viewModel.renameProject(item, it) },
                    onOpenProject = {
                        navController.navigate(
                            NavigationDestination.Editor.withArguments(item.id)
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun CreateProjectFloatingActionButton(
    onCreateProject: (Project) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isCreateAlertOpen by remember { mutableStateOf(false) }
    var projectName by remember { mutableStateOf("") }
    var projectCreator by remember { mutableStateOf("") }

    if (isCreateAlertOpen) {
        AlertDialog(
            title = { Text("Create Project") },
            text = {
                Column {
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = projectName,
                        onValueChange = {
                            projectName = it
                        },
                        label = {
                            Text("Project Name")
                        }
                    )
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        value = projectCreator,
                        onValueChange = {
                            projectCreator = it
                        },
                        label = {
                            Text("Creator")
                        }
                    )
                }
            },
            onDismissRequest = { isCreateAlertOpen = false },
            confirmButton = {
                TextButton(onClick = {
                    val project = Project(
                        System.nanoTime(),
                        projectName,
                        projectCreator,
                        System.currentTimeMillis(),
                        level = Level.Empty
                    )
                    onCreateProject(project)
                    projectName = ""
                    projectCreator = ""
                    isCreateAlertOpen = false
                }) {
                    Text("confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { isCreateAlertOpen = false }) {
                    Text("dismiss")
                }
            },
        )
    }

    FloatingActionButton(
        modifier = modifier,
        onClick = {
            isCreateAlertOpen = true
        }
    ) {
        Icon(Icons.Filled.Add, contentDescription = "add")
    }
}

@Composable
private fun ProjectItem(
    item: Project,
    modifier: Modifier = Modifier,
    onOpenProject: () -> Unit = {},
    onDeleteProject: () -> Unit = {},
    onRenameProject: (newName: String) -> Unit = {},
) {
    var newName by remember { mutableStateOf(item.name) }
    var isEditEnabled by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = modifier.combinedClickable(
            onClick = onOpenProject,
            onLongClick = { }
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                            append("Creator: ")
                        }
                        append(item.creator)
                    },
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            IconButton(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = onDeleteProject
            ) {
                Icon(Icons.Filled.Delete, contentDescription = null)
            }
            Text(
                modifier = Modifier.align(Alignment.BottomEnd),
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                        append("LastModifyTime: ")
                    }
                    append(formatMillisecondAsI18nString(item.lastModifyTime))
                }
            )
        }
    }
}

@Preview
@Composable
private fun PreviewProjectItem() {
    val item = Project(
        id = System.nanoTime(),
        name = "Alexandria Bell",
        creator = "gravida",
        lastModifyTime = 19323400035L,
        level = Level.Empty
    )
    ProjectItem(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        item = item
    )
}