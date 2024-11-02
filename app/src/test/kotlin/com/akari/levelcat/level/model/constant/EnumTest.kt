package com.akari.levelcat.level.model.constant

import com.akari.levelcat.level.LevelJson
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.junit.Test
import kotlin.test.assertEquals

class EnumTest {
    @Serializable
    data class TestModel(
        val zombie: ZombieType
    )

    val model = TestModel(zombie = ZombieType.Boss)
    val modelJson = """
        {
            "zombie": 25
        }
    """.trimIndent()

    @Test
    fun serializeEnum() {
        val jsonFromModel = LevelJson.encodeToString(model)
        assertEquals(jsonFromModel, modelJson)
    }

    @Test
    fun deserializeEnum() {
        val modelFromJson = LevelJson.decodeFromString<TestModel>(modelJson)
        assertEquals(modelFromJson, model)
    }
}