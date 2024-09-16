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


@Composable
fun Editor(
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
}