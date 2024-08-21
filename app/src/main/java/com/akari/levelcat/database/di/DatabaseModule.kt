package com.akari.levelcat.database.di

import android.content.Context
import androidx.room.Room
import com.akari.levelcat.database.LevelcatDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ): LevelcatDatabase = Room.databaseBuilder(
        context,
        LevelcatDatabase::class.java,
        "LevelCatDatabase"
    ).fallbackToDestructiveMigration()
        .build()
}