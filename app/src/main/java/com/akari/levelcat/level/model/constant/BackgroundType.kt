package com.akari.levelcat.level.model.constant

import kotlinx.serialization.Serializable

object BackgroundSerializer : ConstantEnumSerializer<BackgroundType>(BackgroundType.entries)

@Serializable(with = BackgroundSerializer::class)
enum class BackgroundType(
    override val id: Int
) : ConstantEnum {
    Day(id = 0),
    Night(id = 1),
    Pool(id = 2),
    Fog(id = 3),
    Roof(id = 4),
    Boss(id = 5),
    GreatWall(id = 6),
    GreatWallNight(id = 7),
    PanSiDong(id = 8),
    BaiGuLing(id = 9),
    LeiYinSi(id = 10),
    HuoYanShan(id = 11),
    NiuMoGongDiCeng(id = 12),
    NiuMoGongZhongCeng(id = 13),
    NiuMoGongDingCeng(id = 14),
    DragonPalace(id = 15),
    GreatWallPool(id = 16),
    Backyard(id = 17),
    HighGround(id = 18);

    override val displayName get() = name
}