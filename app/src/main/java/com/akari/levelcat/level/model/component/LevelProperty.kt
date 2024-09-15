package com.akari.levelcat.level.model.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.akari.levelcat.level.model.constant.BackgroundType
import com.akari.levelcat.level.model.constant.ZombieType
import com.akari.levelcat.level.ui.component.*
import com.akari.levelcat.level.util.InputPatterns
import com.akari.levelcat.level.util.InputPatterns.EmptyOnly
import com.akari.levelcat.level.util.InputPatterns.IntOrEmpty
import com.akari.levelcat.level.util.or
import com.akari.levelcat.level.util.patternOf
import com.akari.levelcat.ui.component.EnumText
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
    var background by mutableStateOf(background)
    var creator by mutableStateOf(creator)
    var easyUpgrade by mutableStateOf(easyUpgrade)
    var initPlantColumn by mutableStateOf(initPlantColumn)
    var name by mutableStateOf(name)
    var numWaves by mutableStateOf(numWaves)
    var startingSun by mutableStateOf(startingSun)
    var startingTime by mutableStateOf(startingTime)
    var startingWave by mutableStateOf(startingWave)
    var wavesPerFlag by mutableStateOf(wavesPerFlag)

    override fun toComponent() = LevelProperty(
        allowedZombies = allowedZombies,
        background = background,
        name = name.takeIf(String::isNotEmpty),
        creator = creator.takeIf(String::isNotEmpty),
        easyUpgrade = easyUpgrade,/*.toBooleanStrictOrNull()*/
        initPlantColumn = initPlantColumn.toIntOrNull(),
        numWaves = numWaves.toIntOrNull(),
        startingSun = startingSun.toIntOrNull(),
        startingTime = startingTime.toIntOrNull(),
        startingWave = startingWave.toIntOrNull(),
        wavesPerFlag = wavesPerFlag.toIntOrNull(),
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
    ComponentCard(
        modifier = modifier,
        componentName = "LevelProperty",
        onComponentDelete = onComponentDelete
    ) {
        listOf(
            Triple("Name", componentState::name, null),
            Triple("Creator", componentState::creator, CreatorPattern),
            Triple("InitPlantColumn", componentState::initPlantColumn, InitPlantColumnPattern),
            Triple("NumWaves", componentState::numWaves, IntOrEmpty),
        ).forEach { (name, property, pattern) ->
            ComponentTextField(
                propertyName = name,
                pattern = pattern,
                value = property.get(),
                onValueChange = property::set,
            )
        }
        ComponentEnumField<BackgroundType>(
            propertyName = "Background",
            entry = componentState.background,
            onEntryChange = { changed ->
                componentState.background = changed
            }
        )
        ComponentSwitch(
            modifier = Modifier.fillMaxWidth(),
            propertyName = "EasyUpgrade",
            checked = componentState.easyUpgrade,
            onCheckedChange = { changed ->
                componentState.easyUpgrade = changed
            }
        )
        ComponentListField(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp),
            propertyName = "AllowedZombies",
            itemListState = componentState.allowedZombies,
            initialItem = { ZombieType.Boss },
            itemContent = { item, onItemChange, onItemDelete ->
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(MaterialTheme.colorScheme.background),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(item.displayName)
//                    IconButton(onClick = onItemDelete) {
//                        Icon(Icons.Default.Delete, null)
//                    }
//                }
                EnumText<ZombieType>(
                    modifier = Modifier.fillMaxWidth(),
                    entry = item,
                    onEnterChange = onItemChange
                )
            },
        )
    }
}
