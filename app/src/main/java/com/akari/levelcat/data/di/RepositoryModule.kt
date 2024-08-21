package com.akari.levelcat.data.di

import com.akari.levelcat.data.repository.ProjectRepository
import com.akari.levelcat.database.dao.ProjectDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun provideProjectRepository(dao: ProjectDao): ProjectRepository = ProjectRepository(dao)
}