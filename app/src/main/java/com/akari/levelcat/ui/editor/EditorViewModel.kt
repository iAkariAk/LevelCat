package com.akari.levelcat.ui.editor

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akari.levelcat.data.model.Project
import com.akari.levelcat.data.repository.ProjectRepository
import com.akari.levelcat.integratedlevel.model.Component
import com.akari.levelcat.ui.navigation.ARG_EDITOR_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    val projectRepository: ProjectRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val projectId: Long = savedStateHandle[ARG_EDITOR_ID]!!
    private val project = projectRepository.getProject(projectId)

    val uiState = project.map { EditorUiState(it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EditorUiState(Project.Empty)
    )

    fun addLevelComponent(component: Component) = viewModelScope.launch {
        val project = uiState.value.project
        val level = project.level
        projectRepository.updateProject(project.copy(level = level.copy(components = level.components + component)))
    }
}

@Stable
data class EditorUiState(
    val project: Project,
)

