package com.akari.levelcat.ui.home

import android.content.ClipData
import android.content.ClipboardManager
import androidx.lifecycle.viewModelScope
import com.akari.levelcat.BuildConfig
import com.akari.levelcat.data.model.Project
import com.akari.levelcat.data.repository.ProjectRepository
import com.akari.levelcat.level.model.Level
import com.akari.levelcat.level.model.component.LevelProperty
import com.akari.levelcat.level.util.Json
import com.akari.levelcat.ui.util.BasicViewModel
import com.akari.levelcat.util.logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val projectRepository: ProjectRepository,
    val clipboardManager: ClipboardManager,
) : BasicViewModel() {

    val homeUiState = projectRepository.getAllProjects()
        .catch {
            projectRepository.deleteAllProjects()
            logger.error(it.message ?: "", it)
            showSnackbar("Cannot get projects because ${it.message}")
            emit(emptyList())
        }
        .map { HomeUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState()
        )

    fun createProject(project: Project) = viewModelScope.launch {
        projectRepository.insertProject(project)
    }

    fun importProjectFromClipboard() = viewModelScope.launch {
        clipboardManager.primaryClip?.let { data ->
            val importedJson = data.getItemAt(0).text.toString()
            val level = Json.decodeFromString<Level>(importedJson)
            val levelProperty = level.components.find { it is LevelProperty } as LevelProperty?
            val id = System.nanoTime()
            val project = Project(
                id = id,
                name = levelProperty?.name ?: "Unnamed $id",
                creator = levelProperty?.creator ?: "Unnamed $id",
                level = level
            )
            createProject(project)
        }
    }

    fun deleteProject(project: Project) = viewModelScope.launch {
        projectRepository.deleteProject(project)
    }

    fun renameProject(project: Project, newName: String) = viewModelScope.launch {
        projectRepository.updateProject(project.copy(name = newName))
    }

    fun exportProject(project: Project) = viewModelScope.launch {
        val exportedJson = Json.encodeToString(project.level)
        clipboardManager.setPrimaryClip(ClipData.newPlainText(BuildConfig.APPLICATION_ID, exportedJson))
    }
}

data class HomeUiState(val projects: List<Project> = emptyList())