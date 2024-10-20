package com.akari.levelcat.level.model

import com.akari.levelcat.level.model.constant.GameConstants
import com.akari.levelcat.level.util.patternOf

object ModelPatterns {
    val InColumn = patternOf("Column must be range between 0..${GameConstants.GRID_SIZE_X_MAX}") {
        it.toIntOrNull()?.let { it in 0..GameConstants.GRID_SIZE_X_MAX } ?: false
    }
    val InRow = patternOf("Row must be range between 0..${GameConstants.GRID_SIZE_Y_MAX}") {
        it.toIntOrNull()?.let { it in 0..GameConstants.GRID_SIZE_Y_MAX } ?: false
    }
}