package com.akari.levelcat.integratedlevel.model

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@SerialName("DLKJA")
sealed interface Component {
    @Composable
    fun Editor(modifier: Modifier)
}

@Serializable
@SerialName("LevelProperty")
data class LevelProperty(
    @SerialName("AllowedZombies")
    val allowedZombies: List<Int>? = null,
    @SerialName("Background")
    val background: Int? = null,
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

    @Composable
    override fun Editor(modifier: Modifier) {
        Text(modifier = modifier, text = "TODO")
    }
}
