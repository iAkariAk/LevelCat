@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSerializationApi::class, InternalSerializationApi::class)

package com.akari.levelcat.level.model.component


import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.akari.levelcat.level.model.constant.ConstantEnum
import com.akari.levelcat.level.model.constant.SeedType
import com.akari.levelcat.level.model.constant.ZombieType
import com.akari.levelcat.level.ui.component.ComponentEditor
import com.akari.levelcat.level.ui.component.ComponentScope
import com.akari.levelcat.level.ui.component.InputField
import com.akari.levelcat.level.ui.component.ListField
import com.akari.levelcat.level.util.InputPatterns
import com.akari.levelcat.level.util.InputPatterns.IntOrEmpty
import com.akari.levelcat.level.util.or
import com.akari.levelcat.ui.component.EnumText
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
typealias ZombieVaseCountState = @Serializable(ZombieVaseCountSerializer::class) VaseCountState<ZombieType>
typealias PlantVaseCountState = @Serializable(PlantVaseCountSerializer::class) VaseCountState<SeedType>

@Stable
@Serializable
data class VaseCount<T>(
    val type: T,
    val count: Int?,
) where T : Enum<T>, T : ConstantEnum {
    fun asState() = VaseCountState(
        type = type,
        count = count?.toString() ?: ""
    )
}

@Stable
class VaseCountState<T>(
    type: T,
    count: String
) where T : Enum<T>, T : ConstantEnum {
    val type = mutableStateOf(type)
    val count = mutableStateOf(count)

    fun toModel() = VaseCount(
        type = type.value,
        count = count.value.toIntOrNull()
    )
}

private object ZombieVaseCountSerializer : VaseCountSerializer<ZombieType>(ZombieType.entries)
private object PlantVaseCountSerializer : VaseCountSerializer<SeedType>(SeedType.entries)

private sealed class VaseCountSerializer<T>(
    private val typeEntries: EnumEntries<T>,
) : KSerializer<VaseCount<T>> where T : Enum<T>, T : ConstantEnum {
    private val arraySerializer = IntArraySerializer()
    override val descriptor = buildSerialDescriptor("VaseCount", StructureKind.LIST)

    override fun deserialize(decoder: Decoder): VaseCount<T> {
        return decoder.decodeStructure(arraySerializer.descriptor) {
            var typeId: Int = -1
            var count: Int = -1
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> typeId = decodeIntElement(descriptor, index)
                    1 -> count = decodeIntElement(descriptor, index)
                    else -> break
                }
            }
            val type = typeEntries.find { it.id == typeId }!!
            VaseCount(type, count)
        }
    }

    override fun serialize(encoder: Encoder, value: VaseCount<T>) {
        encoder.encodeStructure(arraySerializer.descriptor) {
            encodeIntElement(descriptor, 0, value.type.id)
            encodeIntElement(descriptor, 1, value.count ?: -1)
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
        plantVases = plantVases?.map(PlantVaseCount::asState) ?: emptyList(),
        zombieVases = zombieVases?.map(ZombieVaseCount::asState) ?: emptyList(),
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
    plantVases: List<PlantVaseCountState> = emptyList(),
    zombieVases: List<ZombieVaseCountState> = emptyList(),
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
        plantVases = plantVases.map(PlantVaseCountState::toModel),
        zombieVases = zombieVases.map(ZombieVaseCountState::toModel),
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
private inline fun <reified E> ComponentScope.VaseCountProperty(
    name: String,
    listState: SnapshotStateList<VaseCountState<E>>,
    noinline initialItem: () -> VaseCountState<E>
) where E : Enum<E>, E : ConstantEnum = VaseCountProperty(
    name = name,
    entriesOfE = kotlin.enums.enumEntries<E>(),
    listState = listState,
    initialItem = initialItem
)

@Composable
private fun <E> ComponentScope.VaseCountProperty(
    name: String,
    entriesOfE: List<E>,
    listState: SnapshotStateList<VaseCountState<E>>,
    initialItem: () -> VaseCountState<E>
) where E : Enum<E>, E : ConstantEnum {
    ListField(
        name = name,
        initialItem = initialItem,
        state = listState
    ) { index, item, onItemChange, onItemDelete ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            OutlinedPatternedTextField(
                modifier = Modifier.fillMaxWidth(),
                prefix = {
                    EnumText(
                        entry = item.type.value,
                        entries = entriesOfE,
                        onEnterChange = { item.type.value = it },
                    )
                    Spacer(Modifier.width(32.dp))
                },
                label = { Text("Count") },
                value = item.count.value,
                onValueChange = {
                    item.count.value = it
                },
                pattern = IntOrEmpty
            )
        }
    }

}

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


        VaseCountProperty(
            name = "PlantVases",
            listState = componentState.plantVases,
            initialItem = { PlantVaseCountState(SeedType.Peashooter, "0") },
        )
        VaseCountProperty(
            name = "ZombieVases",
            listState = componentState.zombieVases,
            initialItem = { ZombieVaseCountState(ZombieType.Normal, "0") },
        )
    }
}
