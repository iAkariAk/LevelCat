package com.akari.levelcat.level.model.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.akari.levelcat.level.ui.component.ComponentCard
import kotlinx.serialization.Serializable

@Serializable
data object ColumnPlanting : Component {
    override fun asState() = ColumnPlantingState
}

data object ColumnPlantingState : ComponentState<ColumnPlanting> {
    override fun toComponent() = ColumnPlanting
}

@Composable
fun ColumnPlanting(
    onComponentDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    ComponentCard(
        modifier = modifier,
        componentName = "Column Planting",
        onComponentDelete = onComponentDelete,
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )
    }
}