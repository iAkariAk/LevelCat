@file:OptIn(ExperimentalFoundationApi::class, FlowPreview::class, ExperimentalMaterial3Api::class)

package com.akari.levelcat.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.akari.levelcat.data.model.ProjectSnapshot
import com.akari.levelcat.level.util.InputPatterns.NotBlank
import com.akari.levelcat.ui.LevelcatTopAppBar
import com.akari.levelcat.ui.component.*
import com.akari.levelcat.ui.navigation.LocalNavController
import com.akari.levelcat.ui.navigation.NavigationDestination
import com.akari.levelcat.ui.util.UiEventHandler
import com.akari.levelcat.ui.util.formatMillisecondAsI18nString
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

private typealias HomeIntentDialogHostState = AlertDialogHostState<HomeIntent>

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val navController = LocalNavController.current
    val homeUiState by viewModel.homeUiState.collectAsState()
    val intentDialogHostState = remember { HomeIntentDialogHostState() }
    val snackbarHostState = remember { SnackbarHostState() }
    AlertDialogHost(intentDialogHostState)
    UiEventHandler(viewModel = viewModel, snackbarHostState = snackbarHostState)

    Scaffold(
        modifier = modifier,
        topBar = {
            LevelcatTopAppBar(
                destination = NavigationDestination.Home,
                navigateUp = navController::navigateUp,
                actions = {
                    val uriHandler = LocalUriHandler.current
                    IconButton(onClick = {
                        uriHandler.openUri("https://github.com/iAkariAk/LevelCat")
                    }) {
                        Icon(Icons.Outlined.Code, contentDescription = "")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            CreateProjectFab(
                intentDialogHostState = intentDialogHostState,
                onCreateProject = viewModel::createProject,
                onImportProjectFromClipboard = viewModel::importProjectFromClipboard
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            items(items = homeUiState.projects, key = { it.id }) { item ->
                Spacer(modifier = Modifier.height(4.dp))
                ProjectItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .animateEnter(),
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
private fun CreateProjectFab(
    intentDialogHostState: AlertDialogHostState<HomeIntent>,
    onCreateProject: (ProjectSnapshot) -> Unit,
    onImportProjectFromClipboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    FloatingActionButton(
        modifier = modifier,
        onClick = {
            coroutineScope.launch {
                intentDialogHostState.alertSelectCreateMode(
                    onCreateProject = onCreateProject,
                    onImportProjectFromClipboard = onImportProjectFromClipboard,
                )
            }
        }
    ) {
        Icon(Icons.Filled.Add, contentDescription = "add")
    }
}


private suspend fun HomeIntentDialogHostState.alertSelectCreateMode(
    onCreateProject: (ProjectSnapshot) -> Unit,
    onImportProjectFromClipboard: () -> Unit
) = coroutineScope {
    val result = alert(
        title = { Text("Select a create mode") },
        text = { controller ->
            var mode by argument { HomeIntent.SelectCreateProjectMode.New }
            Column {
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        mode = HomeIntent.SelectCreateProjectMode.New
                        controller.confirm()
                    }
                ) { Text("New") }
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        mode = HomeIntent.SelectCreateProjectMode.Clipboard
                        controller.confirm()
                    }
                ) { Text("Clipboard") }
            }
        },
        dismissButton = {},
        confirmButton = {},
        transform = {
            val mode by argument<HomeIntent.SelectCreateProjectMode>()
            mode
        }
    )
    if (result is AlertResult.Confirmed) {
        when (result.value) {
            HomeIntent.SelectCreateProjectMode.New -> alertForNew(onCreateProject)
            HomeIntent.SelectCreateProjectMode.Clipboard -> onImportProjectFromClipboard()
            HomeIntent.SelectCreateProjectMode.System -> TODO("Import from system")
        }
    }
}

private suspend fun HomeIntentDialogHostState.alertForNew(
    onCreateProject: (ProjectSnapshot) -> Unit,
) {
    val result = alert(
        title = { Text("Create ProjectSnapshot") },
        text = {
            var name by mutableStateArgument { "" }
            var creator by mutableStateArgument { "" }
            var description by mutableStateArgument { "" }
            Column {
                OutlinedPatternedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    pattern = NotBlank,
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("ProjectSnapshot Name") }
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
                OutlinedPatternedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    pattern = NotBlank,
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )
            }
        },
        transform = {
            val name by mutableStateArgument<String>()
            val creator by mutableStateArgument<String>()
            val description by mutableStateArgument<String>()
            HomeIntent.CreateProject.New(name, creator, description)
        }
    )
    if (result is AlertResult.Confirmed) {
        val intent = result.value
        val project = ProjectSnapshot(
            name = intent.name,
            creator = intent.creator,
            description = intent.description,
        )
        onCreateProject(project)
    }
}

@Composable
private fun ProjectItem(
    item: ProjectSnapshot,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column {
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
                Row {
                    IconButton(onClick = onExportProject) {
                        Icon(Icons.Outlined.ContentCopy, contentDescription = null)
                    }
                    IconButton(onClick = onDeleteProject) {
                        Icon(Icons.Outlined.Delete, contentDescription = null)
                    }
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
    val item = ProjectSnapshot(
        id = System.nanoTime(),
        name = "Example",
        creator = "Akari",
        description = "Jellyfish as fish",
        lastModifyTime = 19323400035L,
    )
    ProjectItem(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        item = item
    )
}