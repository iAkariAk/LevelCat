package com.akari.levelcat.level.util

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
class ArgumentBundle {
    private val _args = mutableMapOf<String, Any?>()
    val args = _args as Map<String, Any?>

    operator fun set(name: String, value: Any?) = _args.put(name, value)
    operator fun <T> get(name: String): T = _args[name] as T
        ?: throw throw IllegalStateException("Cannot found argument: $name")

    fun <T> argument(name: String? = null) = object : ReadWriteProperty<Any?, T> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            val actualName = name ?: property.name
            return _args[actualName] as? T
                ?: throw IllegalStateException("Cannot found argument: $actualName")
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            val actualName = name ?: property.name
            _args[actualName] = value
        }
    }

    fun destructured() = Destructured()

    inner class Destructured {
        private val argValues = _args.values.toList()
        operator fun <T> component1() = argValues[0] as T
        operator fun <T> component2() = argValues[1] as T
        operator fun <T> component3() = argValues[2] as T
        operator fun <T> component4() = argValues[3] as T
        operator fun <T> component5() = argValues[4] as T
        operator fun <T> component6() = argValues[5] as T
        operator fun <T> component7() = argValues[6] as T
        operator fun <T> component8() = argValues[7] as T
        operator fun <T> component9() = argValues[8] as T
        operator fun <T> component10() = argValues[9] as T
    }
}

@ObjScopeMaker
class ArgumentBundleScope(
    private val bundle: ArgumentBundle
) {
    fun <T> argument(name: String? = null) = bundle.argument<T>(name)
}