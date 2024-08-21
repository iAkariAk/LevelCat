package com.akari.levelcat.data.model

import com.akari.levelcat.integratedlevel.model.Level
import com.akari.levelcat.integratedlevel.model.LevelProperty

data class Project(
    val id: Long,
    val name: String,
    val creator: String,
    val lastModifyTime: Long,
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
