package com.akari.levelcat.integratedlevel.ui.dsl

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface ComposableProvider {
    @Composable
    fun Content(modifier: Modifier)
}