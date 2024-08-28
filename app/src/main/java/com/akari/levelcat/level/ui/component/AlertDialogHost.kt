@file:OptIn(ExperimentalMaterial3Api::class)

package com.akari.levelcat.level.ui.component

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.DialogProperties
import com.akari.levelcat.level.util.ArgumentBundle
import com.akari.levelcat.level.util.ArgumentBundleScope
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

@Stable
interface AlertDialogState {
    val title: @Composable () -> Unit
    val icon: @Composable () -> Unit
    val text: @Composable AlertArgumentsScope.() -> Unit

    fun confirm()
    fun dismiss()
}

private class AlertDialogStateImpl<T>(
    private val continuation: CancellableContinuation<AlertResult<T>>,
    private val transform: AlertArgumentsScope.() -> T,
    override val title: @Composable () -> Unit,
    override val icon: @Composable () -> Unit,
    override val text: @Composable AlertArgumentsScope.() -> Unit
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


@Stable
class AlertDialogHostState<out T> {
    private val mutex = Mutex()
    var currentState by mutableStateOf<AlertDialogState?>(null)
        private set

    suspend fun <T> alert(
        transform: AlertArgumentsScope.() -> T,
        title: @Composable () -> Unit = {},
        icon: @Composable () -> Unit = {},
        text: @Composable AlertArgumentsScope.() -> Unit = {}
    ): AlertResult<T> = mutex.withLock {
        try {
            suspendCancellableCoroutine { continuation ->
                currentState = AlertDialogStateImpl(continuation, transform, title, icon, text)
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
        AlertDialog(
            modifier = modifier,
            onDismissRequest = { alertDialogState.dismiss() },
            title = alertDialogState.title,
            icon = alertDialogState.icon,
            text = { alertDialogState.argsScope.also { alertDialogState.text(it) } },
            dismissButton = { TextButton(onClick = { alertDialogState.dismiss() }) { Text("Dismiss") } },
            confirmButton = { TextButton(onClick = { alertDialogState.confirm() }) { Text("Confirm") } },
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

@Stable
class AlertDialogHostStateWithoutResult {
    val delegate = AlertDialogHostState<Unit>()

    suspend fun alert(
        title: @Composable () -> Unit = {},
        icon: @Composable () -> Unit = {},
        text: @Composable AlertArgumentsScope.() -> Unit = {}
    ): AlertResult<Unit> = delegate.alert(
        transform = { Unit },
        title = title,
        icon = icon,
        text = text
    )
}


@Composable
fun AlertDialogHostWithoutResult(
    hostState: AlertDialogHostStateWithoutResult,
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


@Stable
class AlertDialogHostStateWithBundle {
    val delegate = AlertDialogHostState<ArgumentBundle>()

    suspend fun alert(
        title: @Composable () -> Unit = {},
        icon: @Composable () -> Unit = {},
        text: @Composable ArgumentBundleScope.() -> Unit = {}
    ): AlertResult<ArgumentBundle> = delegate.alert(
        transform = {
            val bundle by argument<ArgumentBundle>()
            bundle
        },
        title = title,
        icon = icon,
        text = {
            val bundle by argument { ArgumentBundle() }
            ArgumentBundleScope(bundle).run { text() }
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
