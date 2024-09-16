package com.akari.levelcat.level.model.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.akari.levelcat.level.ui.component.EmptyComponentCard
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("ColumnPlanting")
data object ColumnPlanting : Component {
    override fun asState() = ColumnPlantingState
}

data object ColumnPlantingState : ComponentState<ColumnPlanting> {
    override fun toComponent() = ColumnPlanting
}

@Composable
fun ColumnPlanting(
    componentState: ColumnPlantingState,
    onComponentDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyComponentCard(
        modifier = modifier,
        componentName = "ColumnPlanting",
        onComponentDelete = onComponentDelete,
    )
}