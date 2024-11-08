package com.akari.levelcat.level.model

import com.akari.levelcat.level.LevelCodec
import com.akari.levelcat.level.model.component.LevelProperty
import com.akari.levelcat.level.model.component.PlantVaseCount
import com.akari.levelcat.level.model.component.VaseLevel
import com.akari.levelcat.level.model.component.ZombieVaseCount
import com.akari.levelcat.level.model.constant.BackgroundType
import com.akari.levelcat.level.model.constant.SeedType
import com.akari.levelcat.level.model.constant.ZombieType
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
        val encodedJson = LevelCodec.toJson(model)
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
                    background = BackgroundType.Night,
                    initPlantColumn = 0,
                    easyUpgrade = false,
                    numWaves = 30,
                    wavesPerFlag = 10,
                    startingSun = 50,
                    startingWave = 9,
                    startingTime = 2400,
                    allowedZombies = listOf(0, 2, 4, 6, 5, 8, 17, 18, 23).mapNotNull(ZombieType.Companion::ofId),
                )
            )
        )
        val encodeJson = LevelCodec.fromJson(json)
        assertEquals(encodeJson, model)
    }

    @Test
    fun serializeVaseLevel() {
        val model = Level(
            version = 1,
            components = listOf(
                LevelProperty(
                    name = "Play Now",
                    creator = "Wind",
                    background = BackgroundType.Day,
                    initPlantColumn = 0,
                    easyUpgrade = false,
                    numWaves = 30,
                    wavesPerFlag = 10,
                    startingSun = 50,
                    startingWave = 9,
                    startingTime = 2400,
                    allowedZombies = listOf(0, 2, 4, 6, 5, 8, 17, 18, 23).mapNotNull(ZombieType.Companion::ofId),
                ),
                VaseLevel(
                    minColumn = 3,
                    maxColumn = 4,
                    plantVases = listOf(
                        PlantVaseCount(
                            type = SeedType.Peashooter,
                            count = 233
                        ),
                        PlantVaseCount(
                            type = SeedType.Sunshroom,
                            count = 2333
                        ),
                    ),
                    zombieVases = listOf(
                        ZombieVaseCount(
                            type = ZombieType.Normal,
                            count = 233
                        ),
                        ZombieVaseCount(
                            type = ZombieType.Boss,
                            count = 233
                        ),
                    ),
                    numPlantVases = 5,
                    numZombieVases = 5
                )
            ),
        )
        val encodedModel = LevelCodec.toJson(model)
        val json = """
            {
                "Version": 1,
                "Components": [
                    {
                        "Type": "LevelProperty",
                        "AllowedZombies": [
                            0,
                            2,
                            4,
                            6,
                            5,
                            8,
                            17,
                            18,
                            23
                        ],
                        "Background": 0,
                        "Creator": "Wind",
                        "EasyUpgrade": false,
                        "InitPlantColumn": 0,
                        "Name": "Play Now",
                        "NumWaves": 30,
                        "StartingSun": 50,
                        "StartingTime": 2400,
                        "StartingWave": 9,
                        "WavesPerFlag": 10
                    },
                    {
                        "Type": "VaseLevel",
                        "MinColumn": 3,
                        "MaxColumn": 4,
                        "PlantVases": [
                            [
                                0,
                                233
                            ],
                            [
                                9,
                                2333
                            ]
                        ],
                        "ZombieVases": [
                            [
                                0,
                                233
                            ],
                            [
                                25,
                                233
                            ]
                        ],
                        "NumPlantVases": 5,
                        "NumZombieVases": 5
                    }
                ]
            }
        """.trimIndent()
        assertEquals(json, encodedModel)

        val decodedJson = LevelCodec.fromJson(json)
        assertEquals(model, decodedJson)
    }
}