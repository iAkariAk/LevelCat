package com.akari.levelcat.level

import com.akari.levelcat.level.model.Level
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


val LevelJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
    encodeDefaults = true
    prettyPrint = true
    classDiscriminator = "Type"
}

object LevelCodec {
    fun fromJson(json: String) = LevelJson.decodeFromString<Level>(json)
    fun toJson(level: Level) = LevelJson.encodeToString(level)
}