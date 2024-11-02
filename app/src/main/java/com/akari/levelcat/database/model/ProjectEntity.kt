package com.akari.levelcat.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.akari.levelcat.data.model.ProjectSnapshot

@Entity("project_entity")
data class ProjectEntity(
    @PrimaryKey
    val id: Long,
    val minSdk: Int,
    val name: String,
    val creator: String,
    val description: String,
    val lastModifyTime: Long,
)


fun ProjectSnapshot.toEntityModel(): ProjectEntity = ProjectEntity(
    id = id,
    minSdk = version,
    name = name,
    creator = creator,
    description = description,
    lastModifyTime = lastModifyTime
)
//
fun ProjectEntity.toExternalModel(): ProjectSnapshot = ProjectSnapshot(
    id = id,
    version = minSdk,
    name = name,
    description = description,
    creator = creator,
    lastModifyTime = lastModifyTime,
)