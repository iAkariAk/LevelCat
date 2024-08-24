package com.akari.levelcat.level.model

import androidx.compose.runtime.Stable
import com.akari.levelcat.level.model.component.Component
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Stable
data class Level(
    @SerialName("Version")
    val version: Int = VERSION,
    @SerialName("Components")
    val components: List<Component> = emptyList()
) {
    companion object {
        const val VERSION = 1

        val Empty = Level()
    }
}
