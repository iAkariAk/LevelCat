@file:OptIn(ExperimentalFoundationApi::class, InternalSerializationApi::class)

package com.akari.levelcat.level.model.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.akari.levelcat.level.model.constant.ZombieType
import com.akari.levelcat.level.ui.component.ComponentCard
import com.akari.levelcat.level.ui.component.ComponentEditor
import com.akari.levelcat.level.ui.component.InputField
import com.akari.levelcat.level.util.InputPatterns.IntOrEmpty
import com.akari.levelcat.ui.component.EnumText
import com.akari.levelcat.ui.component.animateEnter
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Stable
@Serializable
@SerialName("OverrideZombieProperties")
data class OverrideZombieProperties(
    @SerialName("Properties")
    val properties: List<Property>? = null,

    ) : Component {
    override fun asState() = OverrideZombiePropertiesState(
        properties = properties?.map(Property::asState) ?: emptyList()
    )

    @Stable
    @Serializable
    data class Property(
        @SerialName("ZombieType")
        val zombieType: ZombieType,
        @SerialName("BodyHealth")
        val bodyHealth: Int? = null,
        @SerialName("HelmHealth")
        val helmHealth: Int? = null,
        @SerialName("ShieldHealth")
        val shieldHealth: Int? = null,
        @SerialName("FlyingHealth")
        val flyingHealth: Int? = null,
        @SerialName("ZombieValue")
        val zombieValue: Int? = null,
        @SerialName("FirstAllowedWave")
        val firstAllowedWave: Int? = null,
        @SerialName("PickWeight")
        val pickWeight: Int? = null,
    ) {
        fun asState() = OverrideZombiePropertiesState.PropertyState(
            zombieType = zombieType,
            bodyHealth = bodyHealth?.toString() ?: "",
            helmHealth = helmHealth?.toString() ?: "",
            shieldHealth = shieldHealth?.toString() ?: "",
            flyingHealth = flyingHealth?.toString() ?: "",
            zombieValue = zombieValue?.toString() ?: "",
            firstAllowedWave = firstAllowedWave?.toString() ?: "",
            pickWeight = pickWeight?.toString() ?: ""

        )
    }
}

@Stable
class OverrideZombiePropertiesState(
    properties: List<PropertyState> = emptyList(),
) : ComponentState<OverrideZombieProperties> {
    val properties = properties.toMutableStateList()

    override fun toComponent(): OverrideZombieProperties = OverrideZombieProperties(
        properties = properties.map(PropertyState::toComponent),
    )

    @Stable
    class PropertyState(
        zombieType: ZombieType = ZombieType.Normal,
        bodyHealth: String = "",
        helmHealth: String = "",
        shieldHealth: String = "",
        flyingHealth: String = "",
        zombieValue: String = "",
        firstAllowedWave: String = "",
        pickWeight: String = "",
    ) {
        val zombieType = mutableStateOf(zombieType)
        val bodyHealth = mutableStateOf(bodyHealth)
        val helmHealth = mutableStateOf(helmHealth)
        val shieldHealth = mutableStateOf(shieldHealth)
        val flyingHealth = mutableStateOf(flyingHealth)
        val zombieValue = mutableStateOf(zombieValue)
        val firstAllowedWave = mutableStateOf(firstAllowedWave)
        val pickWeight = mutableStateOf(pickWeight)

        fun toComponent() = OverrideZombieProperties.Property(
            zombieType = zombieType.value,
            bodyHealth = bodyHealth.value.toIntOrNull(),
            helmHealth = helmHealth.value.toIntOrNull(),
            shieldHealth = shieldHealth.value.toIntOrNull(),
            flyingHealth = flyingHealth.value.toIntOrNull(),
            zombieValue = zombieValue.value.toIntOrNull(),
            firstAllowedWave = firstAllowedWave.value.toIntOrNull(),
            pickWeight = pickWeight.value.toIntOrNull()
        )
    }

    companion object {
        fun Empty() = OverrideZombiePropertiesState()
    }
}


@Composable
fun OverrideZombieProperty(
    componentState: OverrideZombiePropertiesState,
    onComponentDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    ComponentCard(
        modifier = modifier,
        onComponentDelete = onComponentDelete,
        componentName = "OverrideZombieProperties",
        actions = {
            TextButton(onClick = {
                componentState.properties.add(OverrideZombiePropertiesState.PropertyState())
            }) {
                Icon(Icons.Default.Add, contentDescription = "add")
            }
        }
    ) {
        LazyColumn(modifier = Modifier.heightIn(max = 1000.dp)) {
            itemsIndexed(componentState.properties) { index, item ->
                ComponentEditor(
                    modifier = Modifier
                        .padding(16.dp)
                        .animateEnter(),
                    name = {
                        EnumText<ZombieType>(
                            entry = item.zombieType.value,
                            onEnterChange = { item.zombieType.value = it }
                        )
                    },
                    onComponentDelete = { componentState.properties.removeAt(index) },
                ) {
                    InputField("BodyHealth", item.bodyHealth, IntOrEmpty)
                    InputField("HelmHealth", item.helmHealth, IntOrEmpty)
                    InputField("ShieldHealth", item.shieldHealth, IntOrEmpty)
                    InputField("FlyingHealth", item.flyingHealth, IntOrEmpty)
                    InputField("ZombieValue", item.zombieValue, IntOrEmpty)
                    InputField("FirstAllowedWave", item.firstAllowedWave, IntOrEmpty)
                    InputField("PickWeight", item.pickWeight, IntOrEmpty)
                }
            }
        }
    }
}