package com.akari.levelcat.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akari.levelcat.data.model.Project
import com.akari.levelcat.data.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val projectRepository: ProjectRepository
) : ViewModel() {

    val uiState = projectRepository.getAllProjects()
        .map { HomeUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState()
        )

    fun createProject(project: Project) = viewModelScope.launch {
        projectRepository.insertProject(project)
    }

    fun deleteProject(project: Project) = viewModelScope.launch {
        projectRepository.deleteProject(project)
    }

    fun renameProject(project: Project, newName: String) = viewModelScope.launch {
        projectRepository.updateProject(project.copy(name = newName))
    }
}

data class HomeUiState(val projects: List<Project> = emptyList())