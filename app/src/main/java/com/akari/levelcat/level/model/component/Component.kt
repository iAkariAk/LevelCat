package com.akari.levelcat.level.model.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable

@Serializable
@Stable
sealed interface Component

@Stable
interface MutableComponent<T : Component> {
    fun asImmutable(): T
}

@Composable
fun Editor(
    component: Component,
    onComponentChange: (Component) -> Unit,
    onComponentDelete: () -> Unit,
    modifier: Modifier = Modifier,
) = when (component) {
    is LevelProperty -> LevelPropertyEditor(
        modifier = modifier,
        component = component,
        onComponentChange = onComponentChange,
        onComponentDelete = onComponentDelete,
    )
}

