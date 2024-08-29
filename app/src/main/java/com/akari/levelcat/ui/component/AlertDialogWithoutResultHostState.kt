package com.akari.levelcat.ui.component

import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.DialogProperties

object AlertDialogHostWithoutResultStateDefaults {
    val confirmButton: @Composable (AlertDialogController) -> Unit
        get() = { controller ->
            TextButton(onClick = controller::confirm) { Text("Confirm") }
        }
    val dismissButton: @Composable (AlertDialogController) -> Unit
        get() = { controller ->
            TextButton(onClick = controller::dismiss) { Text("Dismiss") }
        }
}

@Stable
class AlertDialogWithoutResultHostState {
    val delegate = AlertDialogHostState<Unit>()

    suspend fun alert(
        title: @Composable (controller: AlertDialogController) -> Unit = {},
        icon: @Composable (controller: AlertDialogController) -> Unit = {},
        text: @Composable (controller: AlertDialogController) -> Unit = {},
        confirmButton: @Composable (controller: AlertDialogController) -> Unit = AlertDialogHostWithoutResultStateDefaults.confirmButton,
        dismissButton: @Composable (controller: AlertDialogController) -> Unit = AlertDialogHostWithoutResultStateDefaults.dismissButton,
    ): AlertResult<Unit> = delegate.alert(
        transform = { Unit },
        title = { title(it) },
        icon = { icon(it) },
        text = { text(it) },
        confirmButton = { confirmButton(it) },
        dismissButton = {  dismissButton(it) },
    )
}


@Composable
fun AlertDialogWithoutResultHost(
    hostState: AlertDialogWithoutResultHostState,
    modifier: Modifier = Modifier,
    shape: Shape = AlertDialogDefaults.shape,
    containerColor: Color = AlertDialogDefaults.containerColor,
    iconContentColor: Color = AlertDialogDefaults.iconContentColor,
    titleContentColor: Color = AlertDialogDefaults.titleContentColor,
    textContentColor: Color = AlertDialogDefaults.textContentColor,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
    properties: DialogProperties = DialogProperties()
) {
    AlertDialogHost(
        hostState = hostState.delegate,
        modifier = modifier,
        shape = shape,
        containerColor = containerColor,
        iconContentColor = iconContentColor,
        titleContentColor = titleContentColor,
        textContentColor = textContentColor,
        tonalElevation = tonalElevation,
        properties = properties
    )
}

