package com.akari.levelcat.level.util

import java.lang.reflect.Modifier

fun <T : Any> T.copyUnsafely(values: Map<String, *>): T {
    val jclass = this::class.java
    val newObj = Unsafe.allocateInstance(jclass)
    jclass.declaredFields.asSequence()
        .filter { Modifier.isPrivate(it.modifiers) }
        .onEach { it.isAccessible = true }
        .forEach { field ->
            val newValue = values.getOrElse(field.name) { field.get(this) }
            field.set(newObj, newValue)
        }
    return newObj
}
