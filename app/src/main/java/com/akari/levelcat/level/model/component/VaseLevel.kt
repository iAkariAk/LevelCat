@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSerializationApi::class, InternalSerializationApi::class)

package com.akari.levelcat.level.model.component


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import com.akari.levelcat.level.model.constant.ConstantEnum
import com.akari.levelcat.level.model.constant.SeedType
import com.akari.levelcat.level.model.constant.ZombieType
import com.akari.levelcat.level.ui.component.ComponentEditor
import com.akari.levelcat.level.ui.component.InputField
import com.akari.levelcat.level.ui.component.ListField
import com.akari.levelcat.level.util.InputPatterns
import com.akari.levelcat.level.util.InputPatterns.IntOrEmpty
import com.akari.levelcat.level.util.or
import com.akari.levelcat.ui.component.OutlinedEnumTextField
import com.akari.levelcat.ui.component.OutlinedPatternedTextField
import kotlinx.serialization.*
import kotlinx.serialization.builtins.IntArraySerializer
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlin.enums.EnumEntries

// TODO: Complete VaseLevel

typealias ZombieVaseCount = @Serializable(ZombieVaseCountSerializer::class) VaseCount<ZombieType>
typealias PlantVaseCount = @Serializable(PlantVaseCountSerializer::class) VaseCount<SeedType>

data class VaseCount<T>(
    val type: T,
    val count: Int,
) where T : Enum<T>, T : ConstantEnum

private object ZombieVaseCountSerializer : VaseCountSerializer<ZombieType>(ZombieType.entries)
private object PlantVaseCountSerializer : VaseCountSerializer<SeedType>(SeedType.entries)

private sealed class VaseCountSerializer<T>(
    private val typeEntries: EnumEntries<T>,
) : KSerializer<VaseCount<T>> where T : Enum<T>, T : ConstantEnum {
    private val arraySerializer = IntArraySerializer()
    override val descriptor = buildSerialDescriptor("VaseCount", StructureKind.LIST)

    override fun deserialize(decoder: Decoder): VaseCount<T> {
        return decoder.decodeStructure(arraySerializer.descriptor) {
            val typeId = decodeIntElement(descriptor, 0)
            val type = typeEntries.find { it.id == typeId }!!
            val count = decodeIntElement(descriptor, 1)
            VaseCount(type, count)
        }
    }

    override fun serialize(encoder: Encoder, value: VaseCount<T>) {
        encoder.encodeStructure(arraySerializer.descriptor) {
            encodeIntElement(descriptor, 0, value.type.id)
            encodeIntElement(descriptor, 1, value.count)
        }
    }
}

@Serializable
@SerialName("VaseLevel")
data class VaseLevel(
    @SerialName("MinColumn")
    val minColumn: Int? = null,
    @SerialName("MaxColumn")
    val maxColumn: Int? = null,
    @SerialName("PlantVases")
    val plantVases: List<PlantVaseCount>? = null,
    @SerialName("ZombieVases")
    val zombieVases: List<ZombieVaseCount>? = null,
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
    plantVases: List<PlantVaseCount> = emptyList(),
    zombieVases: List<ZombieVaseCount> = emptyList(),
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
    ComponentEditor(
        modifier = modifier,
        name = "VaseLevel",
        onComponentDelete = onComponentDelete,
    ) {
        InputField("MinColumn", componentState.minColumn, MinColumnPattern)
        InputField("MaxColumn", componentState.maxColumn, MaxColumnPattern)
        InputField("NumPlantVases", componentState.numPlantVases, IntOrEmpty)
        InputField("NumZombieVases", componentState.numZombieVases, IntOrEmpty)
        mapOf(
            "PlantVases" to componentState.plantVases,
            "ZombieVases" to componentState.zombieVases,
        ).forEach { (name, state) ->
            ListField<PlantVaseCount>(
                name = "PlantVases",
                initialItem = { PlantVaseCount(SeedType.Peashooter, 0) },
                state = componentState.plantVases
            ) { index, item, onItemChange, onItemDelete ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedEnumTextField<SeedType>(
                        modifier = Modifier.weight(0.4f),
                        entry = item.type,
                        onEnterChange = { onItemChange(index, item.copy(type = it)) },
                    )
                    OutlinedPatternedTextField(
                        modifier = Modifier.weight(0.6f),
                        value = item.count.toString(),
                        onValueChange = {
                            it.toIntOrNull()?.let {
                                onItemChange(index, item.copy(count = it))
                            }
                        }
                    )
                }
            }
        }
    }
}
