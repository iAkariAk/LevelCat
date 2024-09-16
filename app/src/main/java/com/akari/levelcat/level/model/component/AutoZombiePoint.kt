package com.akari.levelcat.level.model.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.akari.levelcat.level.ui.component.ComponentEditor
import com.akari.levelcat.level.ui.component.InputField
import com.akari.levelcat.level.util.InputPatterns
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("AutoZombiePoint")
data class AutoZombiePoint(
    @SerialName("StartingPoints")
    val startingPoints: Int? = null,
    @SerialName("PointIncrementPerWave")
    val pointIncrementPerWave: Float? = null,
    @SerialName("FlagPointMultiplier")
    val flagPointMultiplier: Float? = null
) : Component {
    override fun asState() = AutoZombiePointState(
        startingPoints = startingPoints?.toString() ?: "",
        pointIncrementPerWave = pointIncrementPerWave?.toString() ?: "",
        flagPointMultiplier = flagPointMultiplier?.toString() ?: "",
    )

    companion object {
        val Empty = AutoZombiePoint()
    }
}

@Stable
class AutoZombiePointState(
    startingPoints: String = "",
    pointIncrementPerWave: String = "",
    flagPointMultiplier: String = "",
) : ComponentState<AutoZombiePoint> {
    val startingPoints = mutableStateOf(startingPoints)
    val pointIncrementPerWave = mutableStateOf(pointIncrementPerWave)
    val flagPointMultiplier = mutableStateOf(flagPointMultiplier)

    override fun toComponent() = AutoZombiePoint(
        startingPoints = startingPoints.value.toIntOrNull(),
        pointIncrementPerWave = pointIncrementPerWave.value.toFloatOrNull(),
        flagPointMultiplier = flagPointMultiplier.value.toFloatOrNull()
    )

    companion object {
        fun Empty() = AutoZombiePointState()
    }
}

@Composable
fun AutoZombiePoint(
    modifier: Modifier,
    componentState: AutoZombiePointState,
    onComponentDelete: () -> Unit
) {
    ComponentEditor(
        modifier = modifier,
        onComponentDelete = onComponentDelete,
        name = "AutoZombiePoint"
    ) {
        InputField("StartingPoints", componentState.startingPoints, InputPatterns.IntOrEmpty)
        InputField("PointIncrementPerWave", componentState.pointIncrementPerWave, InputPatterns.FloatOrEmpty)
        InputField("FlagPointMultiplier", componentState.flagPointMultiplier, InputPatterns.FloatOrEmpty)
    }
}
