package com.akari.levelcat.level.model.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
class SeedBankState(
    numPackets: String = "",
    bannedCards: List<SeedType> = emptyList(),
    lockedCards: List<SeedType> = emptyList(),
    userChoose: Boolean = true
) : ComponentState<SeedBank> {
    var numPackets by mutableStateOf(numPackets)
    val bannedCards = bannedCards.toMutableStateList()
    val lockedCards = lockedCards.toMutableStateList()
    var userChoose by mutableStateOf(userChoose)

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
            onValueChange = { componentState.numPackets = it },
            pattern = IntOrEmpty
        )
        ComponentListField(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp)
                .padding(top = 8.dp),
            propertyName = "BannedCards",
            itemListState = componentState.bannedCards,
            initialItem = { SeedType.Peashooter },
            itemContent = { item, _, _ ->
                Text(item.name)
            },
        )
    }
}