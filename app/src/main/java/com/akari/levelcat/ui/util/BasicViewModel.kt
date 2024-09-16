package com.akari.levelcat.ui.util

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

open class BasicViewModel : ViewModel() {
    private val _snackbarState = MutableSharedFlow<SnackbarVisuals>()
    val snackbarState = _snackbarState.asSharedFlow()

    protected suspend fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        duration: SnackbarDuration =
            if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite
    ) {
        val snackbarVisuals = SnackbarVisualsImpl(
            actionLabel = actionLabel,
            duration = duration,
            message = message,
            withDismissAction = withDismissAction
        )
        _snackbarState.emit(snackbarVisuals)
    }
}

@Composable
fun UiEventHandler(
    viewModel: BasicViewModel,
    snackbarHostState: SnackbarHostState,
) {
    LaunchedEffect(Unit) {
        viewModel.snackbarState.collect {
            snackbarHostState.showSnackbar(it)
        }
    }
}

@Stable
private data class SnackbarVisualsImpl(
    override val actionLabel: String?,
    override val duration: SnackbarDuration,
    override val message: String,
    override val withDismissAction: Boolean
) : SnackbarVisuals