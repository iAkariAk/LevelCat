package com.akari.levelcat.level.model.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import com.akari.levelcat.level.model.constant.SeedType
import com.akari.levelcat.level.ui.component.ComponentEditor
import com.akari.levelcat.level.ui.component.EnumListField
import com.akari.levelcat.level.ui.component.InputField
import com.akari.levelcat.level.ui.component.Switch
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
    val numPackets = mutableStateOf(numPackets)
    val bannedCards = bannedCards.toMutableStateList()
    val lockedCards = lockedCards.toMutableStateList()
    val userChoose = mutableStateOf(userChoose)

    override fun toComponent(): SeedBank = SeedBank(
        numPackets = numPackets.value.toIntOrNull(),
        bannedCards = bannedCards,
        lockedCards = lockedCards,
        userChoose = userChoose.value
    )

    companion object {
        fun Empty() = SeedBankState()
    }
}


@Composable
fun SeedBank(
    componentState: SeedBankState,
    onComponentDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    ComponentEditor(
        modifier = modifier,
        onComponentDelete = onComponentDelete,
        name = "SeedBank"
    ) {
        InputField(name = "NumPackets", state = componentState.numPackets)
        Switch(name = "UserChoose", state = componentState.userChoose)
        EnumListField(name = "BannedCards", state = componentState.bannedCards)
        EnumListField(name = "LockedCards", state = componentState.lockedCards)
    }
}