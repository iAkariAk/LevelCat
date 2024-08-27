package com.akari.levelcat.level.model.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.akari.levelcat.level.model.constant.ZombieType
import com.akari.levelcat.level.ui.component.ComponentCard
import com.akari.levelcat.level.ui.component.ComponentEnumField
import com.akari.levelcat.level.ui.component.ComponentTextField
import com.akari.levelcat.level.util.BooleanOrEmpty
import com.akari.levelcat.level.util.IntOrEmpty
import com.akari.levelcat.level.util.copyUnsafely
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
) : Component {
    override fun asState() = LevelPropertyState(
        allowedZombies = allowedZombies ?: emptyList(),
        background = background?.toString() ?: "",
        easyUpgrade = easyUpgrade?.toString() ?: "",
        initPlantColumn = initPlantColumn?.toString() ?: "",
        name = name ?: "",
        creator = creator ?: "",
        numWaves = numWaves?.toString() ?: "",
        startingSun = startingSun?.toString() ?: "",
        startingTime = startingTime?.toString() ?: "",
        startingWave = startingWave?.toString() ?: "",
        wavesPerFlag = wavesPerFlag?.toString() ?: ""
    )
}


data class LevelPropertyState(
    val allowedZombies: List<Int>,
    val background: String,
    val creator: String,
    val easyUpgrade: String,
    val initPlantColumn: String,
    val name: String,
    val numWaves: String,
    val startingSun: String,
    val startingTime: String,
    val startingWave: String,
    val wavesPerFlag: String,
) : ComponentState<LevelProperty> {
    override fun toComponent() = LevelProperty(
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

@Composable
fun LevelPropertyEditor(
    modifier: Modifier,
    componentState: LevelPropertyState,
    onComponentStateChange: (LevelPropertyState) -> Unit,
    onComponentDelete: () -> Unit,
) {
    ComponentCard(
        modifier = modifier,
        componentName = "LevelProperty",
        onComponentDelete = onComponentDelete
    ) {
        listOf(
            Triple("Background", componentState::background, IntOrEmpty),
            Triple("Name", componentState::name, null),
            Triple(
                "Creator",
                componentState::creator,
                patternOf("你不是真正的高视角！") { it != "高视角" }),
            Triple("EasyUpgrade", componentState::easyUpgrade, BooleanOrEmpty),
            Triple("InitPlantColumn", componentState::initPlantColumn, IntOrEmpty),
            Triple("NumWaves", componentState::numWaves, IntOrEmpty),
        ).forEach { (name, property, pattern) ->
            ComponentTextField(
                propertyName = name,
                pattern = pattern,
                value = property.get(),
                onValueChange = {
                    val newComponentState = componentState.copyUnsafely(mapOf(property.name to it))
                    onComponentStateChange(newComponentState)
                },
            )
        }
        var zombieType by remember { mutableStateOf(ZombieType.Boss) }
        ComponentEnumField<ZombieType>(
            modifier = Modifier.fillMaxWidth(),
            propertyName = "ZombieType",
            entry = zombieType,
            onEntryChange = {zombieType = it}
        )
    }
}
