@file:OptIn(ObsoleteCoroutinesApi::class)

package com.akari.levelcat.ui.editor

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akari.levelcat.data.model.Project
import com.akari.levelcat.data.repository.ProjectRepository
import com.akari.levelcat.level.model.Level
import com.akari.levelcat.level.model.component.ComponentState
import com.akari.levelcat.level.model.component.LevelProperty
import com.akari.levelcat.level.model.component.LevelPropertyState
import com.akari.levelcat.ui.navigation.ARG_EDITOR_ID
import com.akari.levelcat.util.logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.KClass

@HiltViewModel
class EditorViewModel @Inject constructor(
    val projectRepository: ProjectRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val projectId: Long = savedStateHandle[ARG_EDITOR_ID]!!
    private val project = projectRepository.getProject(projectId)
        .stateIn(
            viewModelScope,
            WhileSubscribed(5000),
            Project.Empty
        )

    private val mutableComponents =
        mutableStateMapOf<KClass<out ComponentState<*>>, ComponentState<*>>()

    //    private var components by mutableStateOf(mapOf<KClass<out Component>, Component>())
    val editorUiState = combine(
        project.map { it.id },
        project.map { it.name },
        project.map { it.creator },
        project.map { it.level.version },
        snapshotFlow { mutableComponents.toMap() }
    ) { id, name, creator, version, components ->
        EditorUiState(
            id,
            name,
            creator,
            version,
            components.values.toList()
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(5000),
        initialValue = EditorUiState.Empty
    )

    init {
        viewModelScope.launch {
            project.collectLatest {
                val initialMap = project.value.level.components
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
        logger.debug("${project.value}")
        val project = editorUiState.value.toProject()
        projectRepository.updateProject(project)
        logger.debug("Save: $project")
    }
}

@Stable
data class EditorUiState(
    val projectId: Long,
    val projectName: String,
    val projectCreator: String,
    val projectSdkVersion: Int,
    val components: List<ComponentState<*>>,
) {
    companion object {
        val Empty = EditorUiState(
            projectId = 0,
            projectName = "",
            projectCreator = "",
            projectSdkVersion = 0,
            components = emptyList()
        )
    }
}

fun EditorUiState.toProject(): Project {
    val levelProperty = components
        .find { it is LevelPropertyState }
        ?.toComponent() as? LevelProperty
    return Project(
        id = projectId,
        name = levelProperty?.name ?: projectName,
        creator = levelProperty?.creator ?: projectCreator,
        lastModifyTime = System.currentTimeMillis(),
        level = Level(
            version = projectSdkVersion,
            components = components.map(ComponentState<*>::toComponent)
        )
    )
}


