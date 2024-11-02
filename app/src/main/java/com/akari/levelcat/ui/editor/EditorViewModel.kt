@file:OptIn(ObsoleteCoroutinesApi::class)

package com.akari.levelcat.ui.editor

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.akari.levelcat.data.model.ProjectSnapshot
import com.akari.levelcat.data.repository.ProjectRepository
import com.akari.levelcat.level.model.Level
import com.akari.levelcat.level.model.component.ComponentState
import com.akari.levelcat.level.model.component.LevelProperty
import com.akari.levelcat.level.model.component.LevelPropertyState
import com.akari.levelcat.ui.navigation.ARG_EDITOR_ID
import com.akari.levelcat.ui.util.BasicViewModel
import com.akari.levelcat.util.logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.KClass

@HiltViewModel
class EditorViewModel @Inject constructor(
    val projectRepository: ProjectRepository,
    savedStateHandle: SavedStateHandle,
) : BasicViewModel() {
    private val projectId: Long = savedStateHandle[ARG_EDITOR_ID]!!
    private val project = projectRepository.getProject(projectId)
        .stateIn(
            viewModelScope,
            WhileSubscribed(5000),
            ProjectSnapshot.Empty()
        )

    private val mutableComponents =
        mutableStateMapOf<KClass<out ComponentState<*>>, ComponentState<*>>()

    //    private var components by mutableStateOf(mapOf<KClass<out Component>, Component>())
    val editorUiState = combine(
        project,
        snapshotFlow { mutableComponents.toMap() }
    ) { project, components ->
        EditorUiState(
            projectId = project.id,
            projectName = project.name,
            projectDescription = project.description,
            projectCreator = project.creator,
            projectMinSdk = project.version,
            components = components.values.toList(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(5000),
        initialValue = EditorUiState.Empty
    )

    init {
        viewModelScope.launch {
            project.collectLatest {
                val initialMap = projectRepository.openProjectLevel(it.id).components
                    .associate {
                        val state = it.asState()
                        state::class to state
                    }
                mutableComponents.clear()
                mutableComponents.putAll(initialMap)
//                components = initialMap
            }

            launch {
                ticker(5000).consumeEach {
                    if (true /* FIXME EnableAutoSave*/) save()
                }
            }
        }
    }


    fun addComponent(component: ComponentState<*>) = viewModelScope.launch {
        mutableComponents.putIfAbsent(component::class, component)
    }

    fun updateComponent(component: ComponentState<*>) {
        mutableComponents[component::class] = component
    }

    fun removeComponent(component: ComponentState<*>) {
        mutableComponents.remove(component::class)
    }

    fun save() = viewModelScope.launch {
        val editorUiState = editorUiState.value
        if (editorUiState.isSavable()) {
            val project = editorUiState.toProjectSnapshot()
            val level = editorUiState.toLevel()
            projectRepository.updateProject(project, level)
            logger.debug("Saved: $project")
        } else {
            showSnackbar(
                "Cannot save because the project has errors",
                withDismissAction = true
            )
        }
    }
}

@Stable
data class EditorUiState(
    val projectId: Long,
    val projectName: String,
    val projectCreator: String,
    val projectMinSdk: Int,
    val projectDescription: String,
    val components: List<ComponentState<*>>,
) {
    fun isSavable() = components.all(ComponentState<*>::isValidated)

    companion object {
        val Empty = EditorUiState(
            projectId = 0,
            projectName = "",
            projectCreator = "",
            projectDescription = "",
            projectMinSdk = 0,
            components = emptyList()
        )
    }
}

fun EditorUiState.toProjectSnapshot(): ProjectSnapshot {
    val levelProperty = components
        .find { it is LevelPropertyState }
        ?.toComponent() as? LevelProperty
    return ProjectSnapshot(
        id = projectId,
        name = levelProperty?.name ?: projectName,
        version = projectMinSdk,
        creator = levelProperty?.creator ?: projectCreator,
        description = projectDescription,
        lastModifyTime = System.currentTimeMillis(),
    )
}



fun EditorUiState.toLevel(): Level {
    return Level(
        version = projectMinSdk,
        components = components.map(ComponentState<*>::toComponent)
    )
}


fun main() {
    while (true)
        println("Hello world")
}