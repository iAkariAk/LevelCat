package com.akari.levelcat.level.model.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable

@Serializable
@Stable
sealed interface Component {
    fun asState(): ComponentState<Component>
}

@Stable
sealed interface ComponentState<out T : Component> {
    fun toComponent(): T
}


@Composable
fun Editor(
    componentState: ComponentState<*>,
    onComponentStateChange: (ComponentState<*>) -> Unit,
    onComponentDelete: () -> Unit,
    modifier: Modifier = Modifier,
) = when (componentState) {
    is LevelPropertyState -> LevelPropertyEditor(
        modifier = modifier,
        componentState = componentState,
        onComponentStateChange = onComponentStateChange,
        onComponentDelete = onComponentDelete,
    )
}

