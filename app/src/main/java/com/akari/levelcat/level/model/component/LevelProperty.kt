package com.akari.levelcat.level.model.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import com.akari.levelcat.level.ui.component.ComponentCard
import com.akari.levelcat.level.ui.component.ComponentTextField
import com.akari.levelcat.level.util.BooleanOrEmpty
import com.akari.levelcat.level.util.IntOrEmpty
import com.akari.levelcat.level.util.patternOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("LevelProperty")
data class LevelProperty(
    @SerialName("AllowedZombies")
    val allowedZombies: List<Int>? = null,
    @SerialName("Background")
    val background: Int? = null,
    @SerialName("Creator")
    val creator: String? = null,
    @SerialName("EasyUpgrade")
    val easyUpgrade: Boolean? = null,
    @SerialName("InitPlantColumn")
    val initPlantColumn: Int? = null,
    @SerialName("Name")
    val name: String? = null,
    @SerialName("NumWaves")
    val numWaves: Int? = null,
    @SerialName("StartingSun")
    val startingSun: Int? = null,
    @SerialName("StartingTime")
    val startingTime: Int? = null,
    @SerialName("StartingWave")
    val startingWave: Int? = null,
    @SerialName("WavesPerFlag")
    val wavesPerFlag: Int? = null,
) : Component


@Composable
fun LevelPropertyEditor(
    modifier: Modifier,
    component: LevelProperty,
    onComponentChange: (LevelProperty) -> Unit,
    onComponentDelete: () -> Unit,
) {
    val mutableModel = remember(component) {
        object {
            val allowedZombies =
                component.allowedZombies?.toMutableStateList() ?: mutableStateListOf()
            var background by mutableStateOf<String>(component.background?.toString() ?: "")
            var name by mutableStateOf<String>(component.name?.toString() ?: "")
            var creator by mutableStateOf<String>(component.creator?.toString() ?: "")
            var easyUpgrade by mutableStateOf<String>(component.easyUpgrade?.toString() ?: "")
            var initPlantColumn by mutableStateOf<String>(
                component.initPlantColumn?.toString() ?: ""
            )
            var numWaves by mutableStateOf<String>(component.numWaves?.toString() ?: "")
            var startingSun by mutableStateOf<String>(component.startingSun?.toString() ?: "")
            var startingTime by mutableStateOf<String>(component.startingTime?.toString() ?: "")
            var startingWave by mutableStateOf<String>(component.startingWave?.toString() ?: "")
            var wavesPerFlag by mutableStateOf<String>(component.wavesPerFlag?.toString() ?: "")

            fun asImmutable() = LevelProperty(
                allowedZombies = allowedZombies,
                background = background.toIntOrNull(),
                name = name.takeIf(String::isNotEmpty),
                creator = creator.takeIf(String::isNotEmpty),
                easyUpgrade = easyUpgrade.toBooleanStrictOrNull(),
                initPlantColumn = initPlantColumn.toIntOrNull(),
                numWaves = numWaves.toIntOrNull(),
                startingSun = startingSun.toIntOrNull(),
                startingTime = startingTime.toIntOrNull(),
                startingWave = startingWave.toIntOrNull(),
                wavesPerFlag = wavesPerFlag.toIntOrNull(),
            )
        }
    }
    ComponentCard(
        modifier = modifier,
        componentName = "LevelProperty",
        onComponentChange = { onComponentChange(mutableModel.asImmutable()) },
        onComponentDelete = onComponentDelete
    ) {
        listOf(
            Triple("Background", mutableModel::background, IntOrEmpty),
            Triple("Name", mutableModel::name, null),
            Triple("Creator", mutableModel::creator, patternOf("你不是真正的高视角！") { it != "高视角" }),
            Triple("EasyUpgrade", mutableModel::easyUpgrade, BooleanOrEmpty),
            Triple("InitPlantColumn", mutableModel::initPlantColumn, IntOrEmpty),
            Triple("NumWaves", mutableModel::numWaves, IntOrEmpty),
        ).forEach { (name, property, pattern) ->
            ComponentTextField(
                propertyName = name,
                pattern = pattern,
                value = property.get(),
                onValueChange = property::set,
            )
        }
    }
}
