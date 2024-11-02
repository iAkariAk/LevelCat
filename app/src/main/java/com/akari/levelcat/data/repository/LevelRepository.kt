package com.akari.levelcat.data.repository

import android.content.Context
import com.akari.levelcat.level.LevelCodec
import com.akari.levelcat.level.model.Level
import com.akari.levelcat.util.getValue
import java.lang.ref.WeakReference
import kotlin.io.path.deleteIfExists
import kotlin.io.path.div
import kotlin.io.path.readText
import kotlin.io.path.writeText

class LevelRepository(
    context: Context
) {
    private val context by WeakReference(context)
    private val HOME_PATH = context.getDir("levels", Context.MODE_PRIVATE).toPath()

    private fun getLevelPath(id: Long) = HOME_PATH / "$id.json"

    fun delete(id: Long) {
        getLevelPath(id).deleteIfExists()
    }

    fun save(id: Long, content: Level) {
        val path = getLevelPath(id)
        val json = LevelCodec.toJson(content)
        path.writeText(json)
    }

    fun get(id: Long): Level {
        val path = getLevelPath(id)
        return try {
            LevelCodec.fromJson(path.readText())
        } catch (_: Throwable) {
            Level.Empty
        }
    }
}