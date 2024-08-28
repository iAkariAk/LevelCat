@file:OptIn(ExperimentalMaterial3Api::class)

package com.akari.levelcat.level.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.DialogProperties
import com.akari.levelcat.level.util.ObjScopeMaker
import com.akari.levelcat.util.logger
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface AlertDialogController {
    fun confirm()
    fun dismiss()
}

@Stable
interface AlertDialogState : AlertDialogController {
    val title: @Composable AlertArgumentsScope.(controller: AlertDialogController) -> Unit
    val icon: @Composable AlertArgumentsScope.(controller: AlertDialogController) -> Unit
    val text: @Composable AlertArgumentsScope.(controller: AlertDialogController) -> Unit
    val confirmButton: @Composable AlertArgumentsScope.(controller: AlertDialogController) -> Unit
    val dismissButton: @Composable AlertArgumentsScope.(controller: AlertDialogController) -> Unit

    override fun confirm()
    override fun dismiss()
}

private class AlertDialogStateImpl<T>(
    private val continuation: CancellableContinuation<AlertResult<T>>,
    private val transform: AlertArgumentsScope.() -> T,
    override val title: @Composable AlertArgumentsScope.(controller: AlertDialogController) -> Unit,
    override val icon: @Composable AlertArgumentsScope.(controller: AlertDialogController) -> Unit,
    override val text: @Composable AlertArgumentsScope.(controller: AlertDialogController) -> Unit,
    override val confirmButton: @Composable AlertArgumentsScope.(controller: AlertDialogController) -> Unit,
    override val dismissButton: @Composable AlertArgumentsScope.(controller: AlertDialogController) -> Unit,
) : AlertDialogState {
    val args = mutableMapOf<String, Any?>()
    val argsScope = AlertArgumentsScopeImpl(args) as AlertArgumentsScope

    override fun confirm() {
        if (continuation.isActive) {
            val transformedArgs = argsScope.transform()
            continuation.resume(AlertResult.Confirmed(transformedArgs))
            args.clear()
        }
    }

    override fun dismiss() {
        if (continuation.isActive) {
            continuation.resume(AlertResult.Dismissed)
            args.clear()
        }
    }
}

object AlertDialogHostStateDefaults {
    val confirmButton: @Composable AlertArgumentsScope.(AlertDialogController) -> Unit
        get() = { controller ->
            AlertDialogHostWithoutResultStateDefaults.confirmButton(controller)
        }
    val dismissButton: @Composable AlertArgumentsScope.(AlertDialogController) -> Unit
        get() = { controller ->
            AlertDialogHostWithoutResultStateDefaults.dismissButton(controller)
        }
}

@Stable
class AlertDialogHostState<out T> {
    private val mutex = Mutex()
    var currentState by mutableStateOf<AlertDialogState?>(null)
        private set

    suspend fun <T> alert(
        transform: AlertArgumentsScope.() -> T,
        title: @Composable AlertArgumentsScope.(controller: AlertDialogController) -> Unit = {},
        icon: @Composable AlertArgumentsScope.(controller: AlertDialogController) -> Unit = {},
        text: @Composable AlertArgumentsScope.(controller: AlertDialogController) -> Unit = {},
        confirmButton: @Composable AlertArgumentsScope.(controller: AlertDialogController) -> Unit = AlertDialogHostStateDefaults.confirmButton,
        dismissButton: @Composable AlertArgumentsScope.(controller: AlertDialogController) -> Unit = AlertDialogHostStateDefaults.dismissButton,
    ): AlertResult<T> = mutex.withLock {
        try {
            suspendCancellableCoroutine { continuation ->
                currentState = AlertDialogStateImpl(
                    continuation = continuation,
                    transform = transform,
                    title = title,
                    icon = icon,
                    text = text,
                    confirmButton = confirmButton,
                    dismissButton = dismissButton
                )
            }
        } finally {
            currentState = null
        }
    }
}


@ObjScopeMaker
interface AlertArgumentsScope {
    fun <T> argument(name: String? = null): ReadOnlyProperty<Any?, T>
    fun <T> argument(name: String? = null, initialValue: () -> T): ReadWriteProperty<Any?, T>

    fun <T> mutableStateArgument(name: String? = null): ReadOnlyProperty<Any?, T> =
        ReadOnlyProperty { _, property ->
            val arg by argument<MutableState<T>>(name ?: property.name)
            arg.value
        }


    fun <T> mutableStateArgument(name: String? = null, initialValue: () -> T): ReadWriteProperty<Any?, T> =
        object : ReadWriteProperty<Any?, T> {
            override fun getValue(thisRef: Any?, property: KProperty<*>): T {
                val arg by argument(name ?: property.name) { mutableStateOf(initialValue()) }
                logger.debug("get : property: ${property.name}")
                return arg.value
            }

            override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
                val arg by argument(name ?: property.name) { mutableStateOf(initialValue()) }
                logger.debug("set : property: ${property.name}, $value")
                arg.value = value
            }
        }
}


@Suppress("UNCHECKED_CAST")
private class AlertArgumentsScopeImpl(val args: MutableMap<String, Any?>) : AlertArgumentsScope {
    override fun <T> argument(name: String?) =
        ReadOnlyProperty<Any?, T> { _, property ->
            val actualName = name ?: property.name
            args[actualName] as? T
                ?: throw IllegalStateException("Cannot find argument for $actualName")
        }

    override fun <T> argument(name: String?, initialValue: () -> T) = object : ReadWriteProperty<Any?, T> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            val actualName = name ?: property.name
            return args.getOrPut(name ?: property.name, initialValue) as? T
                ?: throw IllegalStateException("Cannot find argument for $actualName")
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            val actualName = name ?: property.name
            args[actualName] = value
            logger.debug("AlertArgumentsScopeImpl set : property: ${property.name}")
        }
    }
}


sealed interface AlertResult<out T> {
    data class Confirmed<out T>(val value: T) : AlertResult<T>
    data object Dismissed : AlertResult<Nothing>
}

@Suppress("UNCHECKED_CAST")
@Composable
fun <T> AlertDialogHost(
    hostState: AlertDialogHostState<T>,
    modifier: Modifier = Modifier,
    shape: Shape = AlertDialogDefaults.shape,
    containerColor: Color = AlertDialogDefaults.containerColor,
    iconContentColor: Color = AlertDialogDefaults.iconContentColor,
    titleContentColor: Color = AlertDialogDefaults.titleContentColor,
    textContentColor: Color = AlertDialogDefaults.textContentColor,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
    properties: DialogProperties = DialogProperties()
) {
    hostState.currentState?.let { alertDialogState ->
        alertDialogState as AlertDialogStateImpl<T>
        val argsScope = alertDialogState.argsScope
        AlertDialog(
            modifier = modifier,
            onDismissRequest = { alertDialogState.dismiss() },
            title = { alertDialogState.title(argsScope, alertDialogState) },
            icon = { alertDialogState.icon(argsScope, alertDialogState) },
            text = { alertDialogState.text(argsScope, alertDialogState) },
            dismissButton = { alertDialogState.dismissButton(argsScope, alertDialogState) },
            confirmButton = { alertDialogState.confirmButton(argsScope, alertDialogState) },
            shape = shape,
            containerColor = containerColor,
            iconContentColor = iconContentColor,
            titleContentColor = titleContentColor,
            textContentColor = textContentColor,
            tonalElevation = tonalElevation,
            properties = properties,
        )
    }
}