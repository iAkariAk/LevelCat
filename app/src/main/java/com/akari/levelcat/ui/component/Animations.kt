package com.akari.levelcat.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun Modifier.animateEnter(
    initialScale: Float = 0.3f,
    animationSpec: AnimationSpec<Float> = tween(1000)
): Modifier {
    val scale = remember { Animatable(initialScale) }
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = animationSpec
        )
    }

    return this then Modifier.graphicsLayer(scaleX = scale.value, scaleY = scale.value)
}