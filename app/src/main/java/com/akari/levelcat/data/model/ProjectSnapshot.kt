package com.akari.levelcat.data.model

import androidx.compose.runtime.Stable
import com.akari.levelcat.level.model.Level

@Stable
data class ProjectSnapshot(
    val id: Long = System.nanoTime(),
    val version: Int = Level.VERSION,
    val name: String,
    val creator: String,
    val description: String,
    val lastModifyTime: Long = System.currentTimeMillis(),
) {
    companion object {
        fun Empty() = ProjectSnapshot(
            name = "",
            version = 0,
            creator = "",
            description = "",
            lastModifyTime = 0,
        )
    }
}
