package com.akari.levelcat.level.ui.component

import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.DialogProperties
import com.akari.levelcat.level.util.ArgumentBundle
import com.akari.levelcat.level.util.ArgumentBundleScope

object AlertDialogHostStateWithBundleDefaults {
    val confirmButton: @Composable ArgumentBundleScope.(AlertDialogController) -> Unit
        get() = { controller ->
            AlertDialogHostStateWithoutResultDefaults.confirmButton(controller)
        }
    val dismissButton: @Composable ArgumentBundleScope.(AlertDialogController) -> Unit
        get() = { controller ->
            AlertDialogHostStateWithoutResultDefaults.dismissButton(controller)
        }
}

@Stable
class AlertDialogHostStateWithBundle {
    val delegate = AlertDialogHostState<ArgumentBundle>()

    suspend fun alert(
        title: @Composable ArgumentBundleScope.(controller: AlertDialogController) -> Unit = {},
        icon: @Composable ArgumentBundleScope.(controller: AlertDialogController) -> Unit = {},
        text: @Composable ArgumentBundleScope.(controller: AlertDialogController) -> Unit = {},
        confirmButton: @Composable ArgumentBundleScope.(controller: AlertDialogController) -> Unit = AlertDialogHostStateWithBundleDefaults.confirmButton,
        dismissButton: @Composable ArgumentBundleScope.(controller: AlertDialogController) -> Unit = AlertDialogHostStateWithBundleDefaults.dismissButton,
    ): AlertResult<ArgumentBundle> = delegate.alert(
        transform = {
            val bundle by argument<ArgumentBundle>()
            bundle
        },
        title = {
            val bundle by argument { ArgumentBundle() }
            ArgumentBundleScope(bundle).run { title(it) }
        },
        icon = {
            val bundle by argument { ArgumentBundle() }
            ArgumentBundleScope(bundle).run { icon(it) }
        },
        text = {
            val bundle by argument { ArgumentBundle() }
            ArgumentBundleScope(bundle).run { text(it) }
        },
        confirmButton = {
            val bundle by argument { ArgumentBundle() }
            ArgumentBundleScope(bundle).run { confirmButton(it) }
        },
        dismissButton = {
            val bundle by argument { ArgumentBundle() }
            ArgumentBundleScope(bundle).run { dismissButton(it) }
        }
    )
}


@Composable
fun AlertDialogHostWithBundle(
    hostState: AlertDialogHostStateWithBundle,
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