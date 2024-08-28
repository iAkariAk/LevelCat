package com.akari.levelcat.data.model

import androidx.compose.runtime.Stable
import com.akari.levelcat.level.model.Level
import com.akari.levelcat.level.model.component.LevelProperty

@Stable
data class Project(
    val id: Long = System.nanoTime(),
    val name: String,
    val creator: String,
    val lastModifyTime: Long = System.currentTimeMillis(),
    val level: Level =  Level(
        version = 1,
        components = listOf(
            LevelProperty(
                creator = creator,
                name = name,
            )
        )
    )
) {
    companion object {
        val Empty = Project(
            id = 0,
            name = "",
            creator = "",
            lastModifyTime = 0,
        )
    }
}
