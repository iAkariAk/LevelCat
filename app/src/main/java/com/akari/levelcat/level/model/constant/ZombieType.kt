package com.akari.levelcat.level.model.constant

import kotlinx.serialization.Serializable

object ZombieTypeSerializer : ConstantEnumSerializer<ZombieType>(ZombieType.entries)

@Serializable(with = ZombieTypeSerializer::class)
enum class ZombieType(override val id: Int) : ConstantEnum {
    Normal(id = 0),
    Flag(id = 1),
    TrafficCone(id = 2),
    Polevaulter(id = 3),
    Pail(id = 4),
    Newspaper(id = 5),
    Door(id = 6),
    Football(id = 7),
    Dancer(id = 8),
    BackupDancer(id = 9),
    DuckyTube(id = 10),
    Snorkel(id = 11),
    Zamboni(id = 12),
    Bobsled(id = 13),
    DolphinRider(id = 14),
    JackInTheBox(id = 15),
    Balloon(id = 16),
    Digger(id = 17),
    Pogo(id = 18),
    Yeti(id = 19),
    Bungee(id = 20),
    Ladder(id = 21),
    Catapult(id = 22),
    Gargantuar(id = 23),
    Imp(id = 24),
    Boss(id = 25),
    PeaHead(id = 26),
    WallnutHead(id = 27),
    JalapenoHead(id = 28),
    GatlingHead(id = 29),
    SquashHead(id = 30),
    TallnutHead(id = 31),
    RedeyeGargantuar(id = 32),
    QingEmpire(id = 33),
    QingGeneral(id = 34),
    ChineseRing(id = 35),
    Ninja(id = 36),
    NormalChinese(id = 37),
    FlagChinese(id = 38),
    TrafficConeChinese(id = 39),
    PolevaulterChinese(id = 40),
    PailChinese(id = 41),
    NewspaperChinese(id = 42),
    DoorChinese(id = 43),
    FootballChinese(id = 44),
    DancerChinese(id = 45),
    BackupDancerChinese(id = 46),
    DuckyTubeChinese(id = 47),
    SnorkelChinese(id = 48),
    ZamboniChinese(id = 49),
    BobsledChinese(id = 50),
    DolphinRiderChinese(id = 51),
    JackInTheBoxChinese(id = 52),
    BalloonChinese(id = 53),
    DiggerChinese(id = 54),
    PogoChinese(id = 55),
    BungeeChinese(id = 56),
    LadderChinese(id = 57),
    CatapultChinese(id = 58),
    GargantuarChinese(id = 59),
    ImpChinese(id = 60),
    BossChinese(id = 61),
    PeaHeadChinese(id = 62),
    WallnutHeadChinese(id = 63),
    JalapenoHeadChinese(id = 64),
    GatlingHeadChinese(id = 65),
    SquashHeadChinese(id = 66),
    TallnutHeadChinese(id = 67),
    Conch(id = 68),
    Shrimp(id = 69),
    Tortoise(id = 70),
    Carb(id = 71),
    Seahorse(id = 72),
    Jianyu(id = 73),
    Fisherman(id = 74),
    ZombieTransferGate(id = 75),
    ScarletWitch(id = 76),
    Darkhold(id = 77), ;

    override val displayName get() = name // FIXME i18n

    companion object {
        fun ofId(id: Int) = entries.find { it.id == id }
    }
}