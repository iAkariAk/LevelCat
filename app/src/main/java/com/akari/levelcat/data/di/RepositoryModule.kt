package com.akari.levelcat.data.di

import android.content.Context
import com.akari.levelcat.data.repository.LevelRepository
import com.akari.levelcat.data.repository.ProjectRepository
import com.akari.levelcat.database.dao.ProjectDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun provideProjectRepository(
        dao: ProjectDao,
        levelRepository: LevelRepository
    ) = ProjectRepository(
        projectDao = dao,
        levelRepository = levelRepository
    )

    @Provides
    fun provideLevelRepository(
        @ApplicationContext context: Context
    ) = LevelRepository(context)
}