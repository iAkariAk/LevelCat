@file:OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)

package com.akari.levelcat.level.model.constant

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi

interface ConstantEnum {
    val displayName: String
    val id: Int
}
// TODO
//class ConstantEnumSerializer<E : ConstantEnum>(val dataSerializer: KSerializer<E>) : KSerializer<E> {
//    override val descriptor = dataSerializer.descriptor
//
//    override fun deserialize(decoder: Decoder): E {
//        TODO()
//    }
//
//    override fun serialize(encoder: Encoder, value: E) {
//    }
//
//}