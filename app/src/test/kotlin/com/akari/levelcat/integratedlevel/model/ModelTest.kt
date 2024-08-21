package com.akari.levelcat.integratedlevel.model

import com.akari.levelcat.integratedlevel.Json
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals

class ModelTest {
    @Test
    fun serializeModel() {
        val model = Level(
            components = listOf(
                LevelProperty(
                    initPlantColumn = 3,
                    name = "Test",
                    numWaves = 30,
                    startingSun = 8848,
                    startingTime = 0,
                    startingWave = 1,
                    wavesPerFlag = 1
                )
            )
        )
        val json = """
                {
                    "Version": 1,
                    "Components": [
                        {
                            "Type": "LevelProperty",
                            "InitPlantColumn": 3,
                            "Name": "Test",
                            "NumWaves": 30,
                            "StartingSun": 8848,
                            "StartingTime": 0,
                            "StartingWave": 1,
                            "WavesPerFlag": 1
                        }
                    ]
                }""".trimIndent()
        val encodedJson = Json.encodeToString(model)
        assertEquals(encodedJson, json)
    }

    @Test
    fun deserializeModel() {
        val json = """
            {
                "Version": 1,
                "Components": [
                    {
                        "Type":"LevelProperty",
                        "Name":"Play Now",
                        "Creator":"Wind",
                        "Background":1,
                        "InitPlantColumn":0,
                        "EasyUpgrade":false,
                        "NumWaves":30,
                        "WavesPerFlag":10,
                        "StartingSun":50,
                        "StartingWave":9,
                        "StartingTime":2400,
                        "AllowedZombies":[0,2,4,6,5,8,17,18,23]
                    }
                ]
            }
        """.trimIndent()

        val model = Level(
            version = 1,
            components = listOf(
                LevelProperty(
                    name = "Play Now",
                    creator = "Wind",
                    background = 1,
                    initPlantColumn = 0,
                    easyUpgrade = false,
                    numWaves = 30,
                    wavesPerFlag = 10,
                    startingSun = 50,
                    startingWave = 9,
                    startingTime = 2400,
                    allowedZombies = listOf(0, 2, 4, 6, 5, 8, 17, 18, 23),
                )
            )
        )
        val decodedJson = Json.decodeFromString<Level>(json)
        assertEquals(decodedJson, model)
    }
}