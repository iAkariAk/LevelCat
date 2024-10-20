@file:OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)

package com.akari.levelcat.level.model.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import com.akari.levelcat.level.model.ModelPatterns.InColumn
import com.akari.levelcat.level.model.ModelPatterns.InRow
import com.akari.levelcat.level.model.constant.SeedType
import com.akari.levelcat.level.ui.component.ComponentCard
import com.akari.levelcat.level.ui.component.ComponentEditor
import com.akari.levelcat.level.ui.component.ListField
import com.akari.levelcat.ui.component.OutlinedEnumTextField
import com.akari.levelcat.ui.component.OutlinedPatternedTextField
import com.akari.levelcat.ui.component.animateEnter
import kotlinx.serialization.*
import kotlinx.serialization.builtins.IntArraySerializer
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure


@Stable
@Serializable(with = PlantPositionSerializer::class)
data class PlantPosition(
    val type: SeedType,
    val x: Int,
    val y: Int
) {
    fun asState() = PlantPositionState(
        type = type,
        x = x.toString(),
        y = y.toString()
    )
}

private object PlantPositionSerializer : KSerializer<PlantPosition> {
    private val arraySerializer = IntArraySerializer()
    override val descriptor = buildSerialDescriptor("PlantPosition", StructureKind.LIST)

    override fun deserialize(decoder: Decoder): PlantPosition {
        return decoder.decodeStructure(arraySerializer.descriptor) {
            var typeId: Int = -1
            var x: Int = -1
            var y: Int = -1
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> typeId = decodeIntElement(descriptor, index)
                    1 -> x = decodeIntElement(descriptor, index)
                    2 -> y = decodeIntElement(descriptor, index)
                    else -> break
                }
            }
            val type = SeedType.entries.find { it.id == typeId }!!
            PlantPosition(type, x, y)
        }
    }

    override fun serialize(encoder: Encoder, value: PlantPosition) {
        encoder.encodeStructure(arraySerializer.descriptor) {
            encodeIntElement(descriptor, 0, value.type.id)
            encodeIntElement(descriptor, 1, value.x)
            encodeIntElement(descriptor, 2, value.y)
        }
    }
}


@Stable
class PlantPositionState(
     type: SeedType,
     x: String,
     y: String
) {
    val type = mutableStateOf(type)
    val x = mutableStateOf(x)
    val y = mutableStateOf(y)
    fun isValidated() =
        x.value.toIntOrNull()?.let { it in 0..9 } ?: false
                && y.value.toIntOrNull()?.let { it in 0..5 } ?: false

    fun toModel() = PlantPosition(
        type = type.value,
        x = x.value.toIntOrNull() ?: 0,
        y = y.value.toIntOrNull() ?: 0
    )
}

@Serializable
@SerialName("DefaultPlantOnLawn")
data class DefaultPlantOnLawn(
    @SerialName("Plants")
    val plants: List<PlantPosition>? = null
) : Component {
    override fun asState() = DefaultPlantOnLawnState(
        plants = plants?.map(PlantPosition::asState) ?: emptyList()
    )

    companion object {
        val Empty = DefaultPlantOnLawn()
    }
}

@Stable
class DefaultPlantOnLawnState(
    plants: List<PlantPositionState> = emptyList()
) : ComponentState<DefaultPlantOnLawn> {
    val plants = plants.toMutableStateList()

    override fun isValidated() =
        plants.all(PlantPositionState::isValidated)

    override fun toComponent() = DefaultPlantOnLawn(
        plants = plants.map(PlantPositionState::toModel)
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
    ComponentEditor(
        modifier = modifier,
        onComponentDelete = onComponentDelete,
        name = "DefaultPlantOnLawn",
    ) {
        ListField(
            name = "Positions",
            state = componentState.plants,
            initialItem = {
                PlantPositionState(
                    type = SeedType.Peashooter,
                    x = "0",
                    y = "0"
                )
            }
        ) { index, item, onItemChange, onItemDelete ->
            ComponentCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateEnter(),
                componentName = "Position",
                onComponentDelete = onItemDelete
            ) {
                OutlinedEnumTextField<SeedType>(
                    modifier = Modifier.fillMaxWidth(),
                    entry = item.type.value,
                    onEnterChange = {item.type.value = it},
                    entryName = { it.displayName },
                )
                OutlinedPatternedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Column")},
                    value = item.x.value,
                    onValueChange = { item.x.value = it },
                    pattern = InColumn
                )
                OutlinedPatternedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Row")},
                    value = item.y.value,
                    onValueChange = { item.y.value = it },
                    pattern = InRow
                )
            }
        }
    }
}

