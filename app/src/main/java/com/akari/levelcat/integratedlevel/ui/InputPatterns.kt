package com.akari.levelcat.integratedlevel.ui

import com.akari.levelcat.integratedlevel.ui.dsl.FloatRange
import com.akari.levelcat.integratedlevel.ui.dsl.IntOnly
import com.akari.levelcat.integratedlevel.ui.dsl.IntRange
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.full.hasAnnotation
import com.akari.levelcat.integratedlevel.ui.dsl.InputPattern as AnnotationInputPattern

fun interface InputPattern {
    fun match(input: String): Boolean
}

// typealias InputPattern = (input: String) -> Boolean

private fun patternOf(pattern: InputPattern) = pattern

val IntOnlyPattern = patternOf { it.toIntOrNull() != null }
val FloatPattern = patternOf { it.toFloatOrNull() != null }

class IntRangePattern(val range: kotlin.ranges.IntRange) : InputPattern {
    override fun match(input: String) = input.toIntOrNull() in range
}

class FloatRangePattern(val range: ClosedFloatingPointRange<Float>) : InputPattern {
    override fun match(input: String) = input.toFloatOrNull()?.let { it in range } ?: false
}

fun KAnnotatedElement.findInputPattern(): InputPattern? =
    annotations.find { it::class.hasAnnotation<AnnotationInputPattern>() }
        ?.toPattern()

private fun Any.toPattern(): InputPattern {
    val kclass = this::class
    require(kclass.hasAnnotation<AnnotationInputPattern>()) {
        "InputPattern must be annotated with @InputPattern"
    }

    return when (this) {
        is IntRange -> IntRangePattern(min..max)
        is FloatRange -> FloatRangePattern(min..max)
        is IntOnly -> IntOnlyPattern
        else -> error("Unsupported input pattern: $this")
    }
}