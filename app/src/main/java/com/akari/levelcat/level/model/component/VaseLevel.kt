@file:OptIn(ExperimentalMaterial3Api::class)

package com.akari.levelcat.level.model.component


import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import com.akari.levelcat.level.model.constant.SeedType
import com.akari.levelcat.level.model.constant.ZombieType
import com.akari.levelcat.level.ui.component.InputField
import com.akari.levelcat.level.util.InputPatterns
import com.akari.levelcat.level.util.InputPatterns.IntOrEmpty
import com.akari.levelcat.level.util.or
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// TODO: Complete VaseLevel

@Serializable
@SerialName("VaseLevel")
data class VaseLevel(
    @SerialName("MinColumn")
    val minColumn: Int? = null,
    @SerialName("MaxColumn")
    val maxColumn: Int? = null,
    @SerialName("PlantVases")
    val plantVases: List<Pair<SeedType, Int>>? = null,
    @SerialName("ZombieVases")
    val zombieVases: List<Pair<ZombieType, Int>>? = null,
    @SerialName("NumPlantVases")
    val numPlantVases: Int? = null,
    @SerialName("NumZombieVases")
    val numZombieVases: Int? = null,
) : Component {
    override fun asState() = VaseLevelState(
        minColumn = minColumn?.toString() ?: "",
        maxColumn = maxColumn?.toString() ?: "",
        plantVases = plantVases ?: emptyList(),
        zombieVases = zombieVases ?: emptyList(),
        numPlantVases = numPlantVases?.toString() ?: "",
        numZombieVases = numZombieVases?.toString() ?: ""
    )

    companion object {
        val Empty = VaseLevel()
    }
}

@Stable
class VaseLevelState(
    minColumn: String = "",
    maxColumn: String = "",
    plantVases: List<Pair<SeedType, Int>> = emptyList(),
    zombieVases: List<Pair<ZombieType, Int>> = emptyList(),
    numPlantVases: String = "",
    numZombieVases: String = "",
) : ComponentState<VaseLevel> {
    val minColumn = mutableStateOf(minColumn)
    val maxColumn = mutableStateOf(maxColumn)
    val plantVases = plantVases.toMutableStateList()
    val zombieVases = zombieVases.toMutableStateList()
    val numPlantVases = mutableStateOf(numPlantVases)
    val numZombieVases = mutableStateOf(numZombieVases)

    override fun isValidated() =
        MinColumnPattern.match(minColumn.value) && MaxColumnPattern.match(maxColumn.value)

    override fun toComponent() = VaseLevel(
        minColumn = minColumn.value.toIntOrNull(),
        maxColumn = maxColumn.value.toIntOrNull(),
        plantVases = plantVases.toList(),
        zombieVases = zombieVases.toList(),
        numPlantVases = numPlantVases.value.toIntOrNull(),
        numZombieVases = numZombieVases.value.toIntOrNull(),
    )

    companion object {
        fun Empty() = VaseLevelState()
    }
}

private val MinColumnPattern = InputPatterns.IntRange(0..9) or InputPatterns.EmptyOnly
private val MaxColumnPattern = InputPatterns.IntRange(0..9) or InputPatterns.EmptyOnly

@Composable
fun VaseLevel(
    modifier: Modifier,
    componentState: VaseLevelState,
    onComponentDelete: () -> Unit,
) {
    com.akari.levelcat.level.ui.component.ComponentEditor(
        modifier = modifier,
        name = "VaseLevel",
        onComponentDelete = onComponentDelete,
    ) {
        InputField("MinColumn", componentState.minColumn, MinColumnPattern)
        InputField("MaxColumn", componentState.maxColumn, MaxColumnPattern)
        InputField("NumPlantVases", componentState.numPlantVases, IntOrEmpty)
        InputField("NumZombieVases", componentState.numZombieVases, IntOrEmpty)
//        EnumListField("PlantVases", componentState.plantVases)
//        EnumListField("ZombieVases", componentState.zombieVases)
    }
}
