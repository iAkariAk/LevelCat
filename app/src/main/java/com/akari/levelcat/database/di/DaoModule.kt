package com.akari.levelcat.database.di

import com.akari.levelcat.database.LevelcatDatabase
import com.akari.levelcat.database.dao.ProjectDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {
    @Provides
    fun provideProjectDao(
        database: LevelcatDatabase
    ): ProjectDao = database.projectDao()
}