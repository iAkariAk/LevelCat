package com.akari.levelcat.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.akari.levelcat.data.model.Project
import com.akari.levelcat.integratedlevel.model.Level
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Entity("project_entity")
@TypeConverters(LevelConverter::class)
data class ProjectEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val creator: String,
    val lastModifyTime: Long,
    val level: Level
)

class LevelConverter {
    @TypeConverter
    fun fromLevel(level: Level): String = Json.encodeToString(level)
    @TypeConverter
    fun toLevel(level: String): Level = Json.decodeFromString(level)
}


fun Project.toEntityModel(): ProjectEntity = ProjectEntity(
    id = id,
    name = name,
    creator = creator,
    lastModifyTime = lastModifyTime,
    level = level
)
//
fun ProjectEntity.toExternalModel(): Project = Project(
    id = id,
    name = name,
    creator = creator,
    lastModifyTime = lastModifyTime,
    level = level
)