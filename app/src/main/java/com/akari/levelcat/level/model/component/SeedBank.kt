package com.akari.levelcat.level.model.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.akari.levelcat.level.model.constant.SeedType
import com.akari.levelcat.level.ui.component.ComponentCard
import com.akari.levelcat.level.ui.component.ComponentListField
import com.akari.levelcat.level.ui.component.ComponentTextField
import com.akari.levelcat.level.util.InputPatterns.IntOrEmpty
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Stable
@Serializable
data class SeedBank(
    @SerialName("NumPackets")
    val numPackets: Int? = null,
    @SerialName("BannedCards")
    val bannedCards: List<SeedType>? = null,
    @SerialName("LockedCards")
    val lockedCards: List<SeedType>? = null,
    @SerialName("UserChoose")
    val userChoose: Boolean? = null
) : Component {
    override fun asState() = SeedBankState(
        numPackets = numPackets?.toString() ?: "",
        bannedCards = bannedCards ?: emptyList(),
        lockedCards = lockedCards ?: emptyList(),
        userChoose = userChoose ?: true
    )
}

@Stable
data class SeedBankState(
    val numPackets: String = "",
    val bannedCards: List<SeedType> = emptyList(),
    val lockedCards: List<SeedType> = emptyList(),
    val userChoose: Boolean = true
) : ComponentState<SeedBank> {
    override fun toComponent(): SeedBank = SeedBank(
        numPackets = numPackets.toIntOrNull(),
        bannedCards = bannedCards,
        lockedCards = lockedCards,
        userChoose = userChoose
    )

    companion object {
        val Empty = SeedBankState()
    }
}


@Composable
fun SeedBank(
    componentState: SeedBankState,
    onComponentStateChange: (SeedBankState) -> Unit,
    onComponentDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    ComponentCard(
        modifier = modifier,
        componentName = "SeedBank",
        onComponentDelete = onComponentDelete,
    ) {
        ComponentTextField(
            propertyName = "NumPackets",
            value = componentState.numPackets,
            onValueChange = { onComponentStateChange(componentState.copy(numPackets = it)) },
            pattern = IntOrEmpty
        )
        ComponentListField(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp)
                .padding(top = 8.dp),
            propertyName = "BannedCards",
            items = componentState.bannedCards,
            initialItem = { SeedType.Peashooter },
            itemContent = { item, _ ->
                Text(item.name)
            },
            onItemChange = { onComponentStateChange(componentState.copy(bannedCards = it)) }
        )
    }
}