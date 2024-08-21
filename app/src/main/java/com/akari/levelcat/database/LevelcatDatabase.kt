package com.akari.levelcat.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.akari.levelcat.database.dao.ProjectDao
import com.akari.levelcat.database.model.ProjectEntity

@Database(version = 1, entities = [ProjectEntity::class], exportSchema = false)
abstract class LevelcatDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
}