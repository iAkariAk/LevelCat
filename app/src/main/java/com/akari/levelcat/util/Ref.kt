package com.akari.levelcat.util

import java.lang.ref.Reference
import kotlin.reflect.KProperty

operator fun <T> Reference<T>.getValue(thisRef: Any?, kproperty: KProperty<*>) = get()