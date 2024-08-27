@file:OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)

package com.akari.levelcat.level.model.constant

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.enums.EnumEntries

sealed interface ConstantEnum {
    val displayName: String
    val id: Int
}

sealed class ConstantEnumSerializer<E>(private val entries: EnumEntries<E>) : KSerializer<E>
        where E : ConstantEnum, E : Enum<E> {
    override val descriptor = PrimitiveSerialDescriptor("ConstantEnumSerializer", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): E {
        val id = decoder.decodeInt()
        return entries.find { it.id == id } ?: throw SerializationException("Unknown constant $id")
    }

    override fun serialize(encoder: Encoder, value: E) {
        encoder.encodeInt(value.id)
    }
}