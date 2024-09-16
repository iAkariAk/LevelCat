package com.akari.levelcat.level.model.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import com.akari.levelcat.level.model.constant.SeedType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// TODO: Complete DefaultPlantOnLawn

@Serializable
@SerialName("DefaultPlantOnLawn")
data class DefaultPlantOnLawn(
    @SerialName("Plants")
    val plants: List<Triple<SeedType, Int, Int>>? = null
) : Component {
    override fun asState() = DefaultPlantOnLawnState(
        plants = plants ?: emptyList()
    )

    companion object {
        val Empty = DefaultPlantOnLawn()
    }
}

@Stable
class DefaultPlantOnLawnState(
    plants: List<Triple<SeedType, Int, Int>> = emptyList()
) : ComponentState<DefaultPlantOnLawn> {
    val plants = plants.toMutableStateList()

    override fun isValidated(): Boolean {
        for ((index, plant) in plants.withIndex()) {
            if (plant.first == SeedType.None) {
                throw Exception("Plants[$index](${plant.first}, ${plant.second}, ${plant.third}) None is not available")
            }
            if (plant.second < 0 || plant.second >= GRID_SIZE_X || plant.third < 0 || plant.third >= MAX_GRID_SIZE_Y) {
                throw Exception("Plants[$index](${plant.first}, ${plant.second}, ${plant.third}) out of range")
            }
        }
        return true
    }

    override fun toComponent() = DefaultPlantOnLawn(
        plants = plants.toList()
    )

    companion object {
        fun Empty() = DefaultPlantOnLawnState()
    }
}

@Composable
fun DefaultPlantOnLawn(
    modifier: Modifier,
    componentState: DefaultPlantOnLawnState,
    onComponentDelete: () -> Unit
) {
    com.akari.levelcat.level.ui.component.ComponentEditor(
        modifier = modifier,
        onComponentDelete = onComponentDelete,
        name = "DefaultPlantOnLawn",
    ) {
//        EnumListField("Plants", componentState.plants)
    }
}

// Constants
private const val GRID_SIZE_X = 9
private const val MAX_GRID_SIZE_Y = 5
