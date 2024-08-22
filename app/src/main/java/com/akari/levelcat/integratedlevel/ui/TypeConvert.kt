package com.akari.levelcat.integratedlevel.ui

import android.util.Log
import kotlin.reflect.KType
import kotlin.reflect.typeOf

fun String.toTyped(type: KType): Any? {
    Log.d("toTyped", "type: $type")
    return when (type) {
        typeOf<Int>() -> toInt()
        typeOf<Long>() -> toLong()
        typeOf<Float>() -> toFloat()
        typeOf<Double>() -> toDouble()
        typeOf<Boolean>() -> toBoolean()
        typeOf<Int?>() -> toIntOrNull()
        typeOf<Long?>() -> toLongOrNull()
        typeOf<Float?>() -> toFloatOrNull()
        typeOf<Double?>() -> toDoubleOrNull()
        typeOf<Boolean?>() -> toBooleanStrictOrNull()
        typeOf<String>() -> this
        typeOf<String?>() -> takeIf(String::isEmpty)
        else -> throw IllegalArgumentException("Unsupported type: $type")
    }.also {
        Log.d("toTyped", "result: $it")
    }
}