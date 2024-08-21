package com.akari.levelcat.integratedlevel

import kotlinx.serialization.json.Json

val Json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
    encodeDefaults = true
    prettyPrint = true
    classDiscriminator = "Type"
}