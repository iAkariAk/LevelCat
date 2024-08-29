package com.akari.levelcat.level.model.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.akari.levelcat.level.model.constant.BackgroundType
import com.akari.levelcat.level.model.constant.ZombieType
import com.akari.levelcat.level.ui.component.*
import com.akari.levelcat.level.util.InputPatterns
import com.akari.levelcat.level.util.InputPatterns.EmptyOnly
import com.akari.levelcat.level.util.InputPatterns.IntOrEmpty
import com.akari.levelcat.level.util.copyUnsafely
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

data class LevelPropertyState(
    val allowedZombies: List<ZombieType> = emptyList(),
    val background: BackgroundType = BackgroundType.Day,
    val creator: String = "",
    val easyUpgrade: Boolean = false,
    val initPlantColumn: String = "",
    val name: String = "",
    val numWaves: String = "",
    val startingSun: String = "",
    val startingTime: String = "",
    val startingWave: String = "",
    val wavesPerFlag: String = "",
) : ComponentState<LevelProperty> {
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
    onComponentStateChange: (LevelPropertyState) -> Unit,
    onComponentDelete: () -> Unit,
) {
    fun updateState(updateBlock: (old: LevelPropertyState) -> LevelPropertyState) {
        onComponentStateChange(updateBlock(componentState))
    }

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
//            Triple("Background", componentState::background, InputPatterns.IntRange(0..19)),
//            Triple("EasyUpgrade", componentState::easyUpgrade, BooleanOrEmpty),
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
        ComponentEnumField<BackgroundType>(
            propertyName = "Background",
            entry = componentState.background,
            onEntryChange = { changed ->
                updateState { it.copy(background = changed) }
            }
        )
        ComponentSwitch(
            modifier = Modifier.fillMaxWidth(),
            propertyName = "EasyUpgrade",
            checked = componentState.easyUpgrade,
            onCheckedChange = { changed ->
                updateState { it.copy(easyUpgrade = changed) }
            }
        )
        ComponentListField(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp),
            propertyName = "AllowedZombies",
            items = componentState.allowedZombies,
            onItemChange = { changed ->
                updateState { it.copy(allowedZombies = changed) }
            },
            initialItem = { ZombieType.Boss },
            itemContent = { item, onItemDelete ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item.displayName)
                    IconButton(onClick = onItemDelete) {
                        Icon(Icons.Default.Delete, null)
                    }
                }
            },
        )
    }
}
