@file:OptIn(ExperimentalFoundationApi::class, FlowPreview::class, ExperimentalMaterial3Api::class)

package com.akari.levelcat.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.akari.levelcat.data.model.Project
import com.akari.levelcat.level.model.Level
import com.akari.levelcat.ui.LevelcatTopAppBar
import com.akari.levelcat.ui.navigation.LocalNavController
import com.akari.levelcat.ui.navigation.NavigationDestination
import com.akari.levelcat.ui.util.formatMillisecondAsI18nString
import kotlinx.coroutines.FlowPreview

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val navController = LocalNavController.current
    val homeUiState by viewModel.homeUiState.collectAsState()

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
                    onExportProject = { viewModel.exportProject(item) },
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
    onExportProject: () -> Unit = {},
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
            Row(modifier = Modifier.align(Alignment.TopEnd)) {
                IconButton(onClick = onExportProject) {
                    Icon(Icons.Outlined.ContentCopy, contentDescription = null)
                }
                IconButton(onClick = onDeleteProject) {
                    Icon(Icons.Outlined.Delete, contentDescription = null)
                }
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