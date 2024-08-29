package com.akari.levelcat.level.model.constant

import kotlinx.serialization.Serializable

object BackgroundSerializer : ConstantEnumSerializer<BackgroundType>(BackgroundType.entries)

@Serializable(with = BackgroundSerializer::class)
enum class BackgroundType(
    override val id: Int
) : ConstantEnum {
    Background0(id = 0),
    Background1(id = 1),
    Background2(id = 2),
    Background3(id = 3),
    Background4(id = 4);

    override val displayName get() = name
}