package com.akari.levelcat.level.util

fun <E> List<E>.copy(vararg elements: Pair<E, E>) = toMutableList().apply {
    elements.forEach { (old, new) ->
        this[indexOf(old)] = new
    }
}