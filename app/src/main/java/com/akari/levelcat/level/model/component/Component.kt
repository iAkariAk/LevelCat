package com.akari.levelcat.level.model.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable

@Stable
@Serializable
sealed interface Component {
    fun asState(): ComponentState<Component>
}

@Stable
sealed interface ComponentState<out T : Component> {
    fun isValidated(): Boolean = true
    fun toComponent(): T
}

val emptyComponents
    get() = listOf(
        "LevelProperty" to LevelPropertyState.Empty(),
        "ColumnPlanting" to ColumnPlantingState,
        "SeedBank" to SeedBankState.Empty(),
        "SpawnFog" to SpawnFogState.Empty(),
        "OverrideZombieProperty" to OverrideZombiePropertyState.Empty(),
        "VaseLevel" to LevelPropertyState.Empty(),
        "AutoZombiePoint" to AutoZombiePointState.Empty(),
        "DefaultPlantOnLawnState" to DefaultPlantOnLawnState.Empty(),
    )

@Composable
fun ComponentEditor(
    componentState: ComponentState<*>,
    onComponentDelete: () -> Unit,
    modifier: Modifier = Modifier,
) = when (componentState) {
    is LevelPropertyState -> LevelPropertyEditor(
        modifier = modifier,
        componentState = componentState,
        onComponentDelete = onComponentDelete,
    )

    is SeedBankState -> SeedBank(
        modifier = modifier,
        componentState = componentState,
        onComponentDelete = onComponentDelete,
    )

    ColumnPlantingState -> ColumnPlanting(
        modifier = modifier,
        componentState = ColumnPlantingState,
        onComponentDelete = onComponentDelete,
    )

    is SpawnFogState -> SpawnFog(
        modifier = modifier,
        componentState = componentState,
        onComponentDelete = onComponentDelete,
    )

    is OverrideZombiePropertyState -> OverrideZombieProperty(
        modifier = modifier,
        componentState = componentState,
        onComponentDelete = onComponentDelete,
    )

    is VaseLevelState -> VaseLevel(
        modifier = modifier,
        componentState = componentState,
        onComponentDelete = onComponentDelete,
    )

    is AutoZombiePointState -> AutoZombiePoint(
        modifier = modifier,
        componentState = componentState,
        onComponentDelete,
    )

    is DefaultPlantOnLawnState -> DefaultPlantOnLawn(
        modifier = modifier,
        componentState = componentState,
        onComponentDelete
    )
}