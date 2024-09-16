package com.akari.levelcat.level.model.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import com.akari.levelcat.level.model.constant.BackgroundType
import com.akari.levelcat.level.model.constant.ZombieType
import com.akari.levelcat.level.ui.component.ComponentEditor
import com.akari.levelcat.level.ui.component.EnumField
import com.akari.levelcat.level.ui.component.EnumList
import com.akari.levelcat.level.util.InputPatterns
import com.akari.levelcat.level.util.InputPatterns.EmptyOnly
import com.akari.levelcat.level.util.InputPatterns.IntOrEmpty
import com.akari.levelcat.level.util.or
import com.akari.levelcat.level.util.patternOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("LevelProperty")
data class LevelProperty(
    @SerialName("AllowedZombies")
    val allowedZombies: List<ZombieType>? = null,
    @SerialName("Background")
    val background: BackgroundType? = null,
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
        background = background ?: BackgroundType.Day,
        easyUpgrade = easyUpgrade ?: false /*?.toString() ?: ""*/,
        initPlantColumn = initPlantColumn?.toString() ?: "",
        name = name ?: "",
        creator = creator ?: "",
        numWaves = numWaves?.toString() ?: "",
        startingSun = startingSun?.toString() ?: "",
        startingTime = startingTime?.toString() ?: "",
        startingWave = startingWave?.toString() ?: "",
        wavesPerFlag = wavesPerFlag?.toString() ?: ""
    )

    companion object {
        val Empty = LevelProperty()
    }
}

@Stable
class LevelPropertyState(
    allowedZombies: List<ZombieType> = emptyList(),
    background: BackgroundType = BackgroundType.Day,
    creator: String = "",
    easyUpgrade: Boolean = false,
    initPlantColumn: String = "",
    name: String = "",
    numWaves: String = "",
    startingSun: String = "",
    startingTime: String = "",
    startingWave: String = "",
    wavesPerFlag: String = "",
) : ComponentState<LevelProperty> {
    val allowedZombies = allowedZombies.toMutableStateList()
    val background = mutableStateOf(background)
    val creator = mutableStateOf(creator)
    val easyUpgrade = mutableStateOf(easyUpgrade)
    val initPlantColumn = mutableStateOf(initPlantColumn)
    val name = mutableStateOf(name)
    val numWaves = mutableStateOf(numWaves)
    val startingSun = mutableStateOf(startingSun)
    val startingTime = mutableStateOf(startingTime)
    val startingWave = mutableStateOf(startingWave)
    val wavesPerFlag = mutableStateOf(wavesPerFlag)

    override fun isValidated() =
        CreatorPattern.match(creator.value) && InitPlantColumnPattern.match(initPlantColumn.value)

    override fun toComponent() = LevelProperty(
        allowedZombies = allowedZombies,
        background = background.value,
        name = name.value.takeIf(String::isNotEmpty),
        creator = creator.value.takeIf(String::isNotEmpty),
        easyUpgrade = easyUpgrade.value,/*.toBooleanStrictOrNull()*/
        initPlantColumn = initPlantColumn.value.toIntOrNull(),
        numWaves = numWaves.value.toIntOrNull(),
        startingSun = startingSun.value.toIntOrNull(),
        startingTime = startingTime.value.toIntOrNull(),
        startingWave = startingWave.value.toIntOrNull(),
        wavesPerFlag = wavesPerFlag.value.toIntOrNull(),
    )

    companion object {
        val Empty = LevelPropertyState()
    }
}


private val CreatorPattern = patternOf("你不是真正的高视角！") { it != "高视角" } // Easter Egg!
private val InitPlantColumnPattern = InputPatterns.IntRange(0..9) or EmptyOnly

@Composable
fun LevelPropertyEditor(
    modifier: Modifier,
    componentState: LevelPropertyState,
    onComponentDelete: () -> Unit,
) {
    ComponentEditor(
        modifier = modifier,
        onComponentDelete = onComponentDelete,
        name = "LevelProperty",
    ) {
        InputField("Name", componentState.name, null)
        InputField("Creator", componentState.creator, CreatorPattern)
        InputField("InitPlantColumn", componentState.initPlantColumn, InitPlantColumnPattern)
        InputField("NumWaves", componentState.numWaves, IntOrEmpty)
        EnumField("Background", componentState.background)
        Switch("EasyUpgrade", componentState.easyUpgrade)
        EnumList("AllowedZombies", componentState.allowedZombies)
    }
}
