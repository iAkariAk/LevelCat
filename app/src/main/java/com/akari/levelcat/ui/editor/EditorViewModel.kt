@file:OptIn(ObsoleteCoroutinesApi::class)

package com.akari.levelcat.ui.editor

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akari.levelcat.data.model.Project
import com.akari.levelcat.data.repository.ProjectRepository
import com.akari.levelcat.level.model.component.Component
import com.akari.levelcat.ui.navigation.ARG_EDITOR_ID
import com.akari.levelcat.util.logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    val projectRepository: ProjectRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val projectId: Long = savedStateHandle[ARG_EDITOR_ID]!!
    private val project = projectRepository.getProject(projectId)
    var editorUiState by mutableStateOf(EditorUiState(Project.Empty))
        private set

    init {
        viewModelScope.launch {
            project.collectLatest { project ->
                editorUiState = EditorUiState(project)
                Log.d("EditorViewModel", "editorUiState: $editorUiState")
            }
            launch {
                ticker(5000).consumeEach { save() }
            }
        }
    }


    fun addComponent(component: Component) = viewModelScope.launch {
        Log.d("EditorViewModel", "editorUiState: $editorUiState")

        val newCOmponents = editorUiState.project.level.components + component
        editorUiState = editorUiState.copy(
            editorUiState.project.copy(
                level = editorUiState.project.level.copy(components = newCOmponents)
            )
        )
//        val project = uiState.value.project
//        val level = project.level
//         projectRepository.updateProject(project.copy(level = level.copy(components = level.components + component)))
    }

    fun updateComponent(component: Component) {
        val newComponents = editorUiState.project.level.components.toMutableList().apply {
            val index = indexOfFirst { it::class == component::class }
            this[index] = component
        }
        editorUiState = editorUiState.copy(
            editorUiState.project.copy(
                level = editorUiState.project.level.copy(components = newComponents)
            )
        )
    }

    fun removeComponent(component: Component) {
        val newComponents = editorUiState.project.level.components - component
        editorUiState = editorUiState.copy(
            editorUiState.project.copy(
                level = editorUiState.project.level.copy(components = newComponents)
            )
        )
    }

    fun save() = viewModelScope.launch {
        projectRepository.updateProject(editorUiState.project)
        logger.debug("Save: ${editorUiState.project}")
    }


}

@Stable
data class EditorUiState(
    val project: Project,
)

