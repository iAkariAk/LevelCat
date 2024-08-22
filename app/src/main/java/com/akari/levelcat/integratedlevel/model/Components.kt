package com.akari.levelcat.integratedlevel.model

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import com.akari.levelcat.integratedlevel.ui.dsl.Ignored
import com.akari.levelcat.integratedlevel.ui.dsl.LabelName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Composable
private fun Input(
    label: String,
    inputPattern: (String) -> Boolean,
    onSubmit: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var value by remember { mutableStateOf("") }
    TextField(
        modifier = modifier,
        label = { Text(label) },
        value = value,
        onValueChange = { if (inputPattern(it)) value = it },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onSubmit(value) })
    )
}


@Serializable
sealed interface Component {
    @Composable
    fun Editor(modifier: Modifier, onChange: (Component) -> Unit)
}

@Serializable
@SerialName("LevelProperty")
@LabelName("LevelProperty")
data class LevelProperty(
    @SerialName("AllowedZombies")
    @Ignored
    val allowedZombies: List<Int>? = null,
    @SerialName("Background")
    @LabelName("背景")
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
    override fun Editor(modifier: Modifier, onChange: (Component) -> Unit) {

    }
}
