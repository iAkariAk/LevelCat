package com.akari.levelcat.level.util

import kotlinx.serialization.json.Json

val Json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
    encodeDefaults = true
    prettyPrint = true
    classDiscriminator = "Type"
}