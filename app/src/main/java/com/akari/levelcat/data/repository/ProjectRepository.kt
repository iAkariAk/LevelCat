package com.akari.levelcat.data.repository

import com.akari.levelcat.data.model.Project
import com.akari.levelcat.database.dao.ProjectDao
import com.akari.levelcat.database.model.ProjectEntity
import com.akari.levelcat.database.model.toEntityModel
import com.akari.levelcat.database.model.toExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProjectRepository(private val projectDao: ProjectDao) {
    suspend fun insertProject(project: Project) = projectDao.insertProject(project.toEntityModel())
    suspend fun deleteProject(project: Project) = projectDao.deleteProject(project.toEntityModel())
    suspend fun updateProject(project: Project) = projectDao.updateProject(project.toEntityModel())
    fun getProject(id: Long) = projectDao.getProject(id).map(ProjectEntity::toExternalModel)
    fun getAllProjects(): Flow<List<Project>> =
        projectDao.getAllProjects().map { it.map(ProjectEntity::toExternalModel) }
}