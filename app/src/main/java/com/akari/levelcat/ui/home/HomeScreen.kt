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
import com.akari.levelcat.level.ui.component.AlertDialogHost
import com.akari.levelcat.level.ui.component.AlertDialogHostState
import com.akari.levelcat.level.ui.component.AlertResult
import com.akari.levelcat.level.ui.component.OutlinedPatternedTextField
import com.akari.levelcat.level.util.InputPatterns.NotBlank
import com.akari.levelcat.ui.LevelcatTopAppBar
import com.akari.levelcat.ui.navigation.LocalNavController
import com.akari.levelcat.ui.navigation.NavigationDestination
import com.akari.levelcat.ui.util.formatMillisecondAsI18nString
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch

data class IDCard(
    val idCard: String,
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val navController = LocalNavController.current
    val homeUiState by viewModel.homeUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val intentDialogHostState = remember { AlertDialogHostState<HomeIntent>() }

    AlertDialogHost(intentDialogHostState)

    Scaffold(
        modifier = modifier,
        topBar = {
            LevelcatTopAppBar(
                destination = NavigationDestination.Home,
                navigateUp = navController::navigateUp
            )
        },
        floatingActionButton = {
            CreateProjectFab(
                intentDialogHostState = intentDialogHostState,
                onCreateProject = viewModel::createProject
            )
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

private suspend fun alertForNew(
    dialogHostState: AlertDialogHostState<HomeIntent>,
    onCreateProject: (Project) -> Unit,
) {
    val result = dialogHostState.alert(
        title = { Text("Create Project") },
        text = {
            var name by mutableStateArgument { "" }
            var creator by mutableStateArgument { "" }
            Column {
                OutlinedPatternedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    pattern = NotBlank,
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Project Name") }
                )
                OutlinedPatternedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    pattern = NotBlank,
                    value = creator,
                    onValueChange = { creator = it },
                    label = { Text("Creator") }
                )
            }
        },
        transform = {
            val name by mutableStateArgument<String>()
            val creator by mutableStateArgument<String>()
            HomeIntent.CreateProject.New(name, creator)
        }
    )
    if (result is AlertResult.Confirmed) {
        val intent = result.value
        val project = Project(
            name = intent.name,
            creator = intent.creator,
        )
        onCreateProject(project)
    }
}


@Composable
private fun CreateProjectFab(
    intentDialogHostState: AlertDialogHostState<HomeIntent>,
    onCreateProject: (Project) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    FloatingActionButton(
        modifier = modifier,
        onClick = {
            coroutineScope.launch {
                intentDialogHostState.alert(
                    title = { Text("Select a create mode") },
                    text = { controller ->
                        Column {
                            TextButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    controller.dismiss()
                                    coroutineScope.launch {
                                        alertForNew(
                                            dialogHostState = intentDialogHostState,
                                            onCreateProject = onCreateProject
                                        )
                                    }
                                }
                            ) { Text("New") }
                            TextButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    controller.dismiss()
//                                    alertCreateInfo(HomeIntent.SelectCreateProjectMode.Clipboard)
                                }
                            ) { Text("Clipboard") }
                        }
                    },
                    transform = {}
                )
            }
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