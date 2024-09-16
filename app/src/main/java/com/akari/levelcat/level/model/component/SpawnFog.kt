package com.akari.levelcat.level.model.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.akari.levelcat.level.ui.component.ComponentEditor
import com.akari.levelcat.level.ui.component.InputField
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Stable
@Serializable
@SerialName("SpawnFog")
data class SpawnFog(
    @SerialName("Column")
    val column: Int? = null,
) : Component {
    override fun asState() = SpawnFogState(
        column = column?.toString() ?: "",
    )
}

@Stable
class SpawnFogState(
    column: String = "",
) : ComponentState<SpawnFog> {
    val column = mutableStateOf(column)

    override fun toComponent(): SpawnFog = SpawnFog(
        column = column.value.toIntOrNull()
    )

    companion object {
        fun Empty() = SpawnFogState()
    }
}


@Composable
fun SpawnFog(
    componentState: SpawnFogState,
    onComponentDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    ComponentEditor(
        modifier = modifier,
        onComponentDelete = onComponentDelete,
        name = "SpawnFog"
    ) {
        InputField(name = "column", state = componentState.column)
    }
}