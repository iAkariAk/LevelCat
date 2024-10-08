package com.akari.levelcat.level.model.constant

import kotlinx.serialization.Serializable

object SeedTypeSerializer : ConstantEnumSerializer<SeedType>(SeedType.entries)

@Serializable(with = SeedTypeSerializer::class)
enum class SeedType(override val id: Int) : ConstantEnum {
    Peashooter(id = 0),
    Sunflower(id = 1),
    Cherrybomb(id = 2),
    Wallnut(id = 3),
    Potatomine(id = 4),
    Snowpea(id = 5),
    Chomper(id = 6),
    Repeater(id = 7),
    Puffshroom(id = 8),
    Sunshroom(id = 9),
    Fumeshroom(id = 10),
    Gravebuster(id = 11),
    Hypnoshroom(id = 12),
    Scaredyshroom(id = 13),
    Iceshroom(id = 14),
    Doomshroom(id = 15),
    Lilypad(id = 16),
    Squash(id = 17),
    Threepeater(id = 18),
    Tanglekelp(id = 19),
    Jalapeno(id = 20),
    Spikeweed(id = 21),
    Torchwood(id = 22),
    Tallnut(id = 23),
    Seashroom(id = 24),
    Plantern(id = 25),
    Cactus(id = 26),
    Blover(id = 27),
    Splitpea(id = 28),
    Starfruit(id = 29),
    Pumpkinshell(id = 30),
    Magnetshroom(id = 31),
    Cabbagepult(id = 32),
    Flowerpot(id = 33),
    Kernelpult(id = 34),
    InstantCoffee(id = 35),
    Garlic(id = 36),
    Umbrella(id = 37),
    Marigold(id = 38),
    Melonpult(id = 39),
    Gatlingpea(id = 40),
    Twinsunflower(id = 41),
    Gloomshroom(id = 42),
    Cattail(id = 43),
    Wintermelon(id = 44),
    GoldMagnet(id = 45),
    Spikerock(id = 46),
    Cobcannon(id = 47),
    Imitater(id = 48),
    ExplodeONut(id = 49),
    GiantWallnut(id = 50),
    Sprout(id = 51),
    Leftpeater(id = 52),
    TimeStopper(id = 53),
    IcebergLettuce(id = 54),
    CherryHoverBomb(id = 55),
    None(id = 56),
    Duplicator(id = 57),
    ChinaFlowerpot(id = 58),
    Gourd(id = 59),
    PickledPepper(id = 60),
    DraGonPalaceSeaWeed(id = 61),
    DraGonPalaceSeaStar(id = 62),
    DraGonPalaceTwine(id = 63),
    DraGonPalaceSpew(id = 64),
    DraGonPalaceAneMone(id = 65),
    BonkChoy(id = 66),
    SnapDragon(id = 67),
    Laserbean(id = 68),
    Carrot(id = 69),
    SuperChomper2(id = 70),
    ExplodeNut2(id = 71),
    GoldBloom(id = 72),
    Hurrikale(id = 73),
    AngelStarfruit(id = 74),
    FireKing(id = 75),
    SuperChomper(id = 76),
    SnowFumeShroom(id = 77),
    VineNut(id = 78),
    StoneNut(id = 79),
    DragonChomper(id = 80);

    override val displayName get() = name
}