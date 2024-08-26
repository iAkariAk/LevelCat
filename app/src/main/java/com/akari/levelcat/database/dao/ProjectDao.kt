package com.akari.levelcat.database.dao

import androidx.room.*
import com.akari.levelcat.database.model.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Insert
    suspend fun insertProject(project: ProjectEntity)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)

    @Query("SELECT * FROM project_entity ORDER BY lastModifyTime ASC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM project_entity WHERE id = :id")
    fun getProject(id: Long): Flow<ProjectEntity>

}