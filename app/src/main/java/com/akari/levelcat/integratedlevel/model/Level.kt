package com.akari.levelcat.integratedlevel.model

import androidx.compose.runtime.Stable
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
