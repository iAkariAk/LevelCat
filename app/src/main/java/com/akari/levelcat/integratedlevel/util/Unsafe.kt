@file:SuppressLint("DiscouragedPrivateApi")
@file:Suppress("UNCHECKED_CAST")

package com.akari.levelcat.integratedlevel.util

import android.annotation.SuppressLint

object Unsafe {
    private val unsafeClass = Class.forName("sun.misc.Unsafe")
    private val unsafe = unsafeClass
        .getDeclaredField("theUnsafe").apply { isAccessible = true }
        .get(null)

    private val _allocateInstance =
        unsafeClass.getDeclaredMethod("allocateInstance", Class::class.java)

    fun <T> allocateInstance(clazz: Class<T>): T = _allocateInstance.invoke(unsafe, clazz) as T
    inline fun <reified T> allocateInstance(): T = Unsafe.allocateInstance(T::class.java)
}