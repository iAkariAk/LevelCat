package com.akari.levelcat.data.repository

import com.akari.levelcat.data.model.ProjectSnapshot
import com.akari.levelcat.database.dao.ProjectDao
import com.akari.levelcat.database.model.ProjectEntity
import com.akari.levelcat.database.model.toEntityModel
import com.akari.levelcat.database.model.toExternalModel
import com.akari.levelcat.level.model.Level
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProjectRepository(
    private val projectDao: ProjectDao,
    private val levelRepository: LevelRepository
) {
    suspend fun deleteProject(project: ProjectSnapshot) {
        projectDao.deleteProject(project.toEntityModel())
        levelRepository.delete(project.id)
    }
    suspend fun insertProject(project: ProjectSnapshot, initialLevel: Level = Level.Empty) {
        projectDao.insertProject(project.toEntityModel())
        levelRepository.save(project.id, initialLevel)
    }


    suspend fun updateProject(project: ProjectSnapshot, level: Level? = null) {
        projectDao.updateProject(project.toEntityModel())
        level?.let { levelRepository.save(project.id, it) }
    }

    suspend fun deleteAllProjects() = projectDao.deleteAllProjects()
    fun getProject(id: Long) = projectDao.getProject(id).map(ProjectEntity::toExternalModel)
    fun getAllProjects(): Flow<List<ProjectSnapshot>> =
        projectDao.getAllProjects().map { it.map(ProjectEntity::toExternalModel) }

    fun openProjectLevel(id: Long) = levelRepository.get(id)
}