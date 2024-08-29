@file:Suppress("unused")

package com.akari.levelcat.level.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.akari.levelcat.level.util.InputPatterns.FloatRange
import com.akari.levelcat.level.util.InputPatterns.IntRange


interface InputPattern {
    val errorMessage: String
    fun match(input: String): Boolean
}

fun InputPattern?.match(input: String) = this?.match(input) ?: true

inline fun patternOf(
    errorMessage: String = "Invalid input",
    crossinline match: (String) -> Boolean,
) = object : InputPattern {
    override val errorMessage = errorMessage
    override fun match(input: String) = match(input)
}

inline fun regexPatternOf(
    errorMessage: String,
    crossinline regexString: () -> String
) = patternOf(errorMessage) { input ->
    val regex = Regex(regexString())
    regex.matches(input)
}

fun InputPattern.withErrorMessage(errorMessage: String) =
    patternOf(errorMessage, ::match)

infix fun InputPattern.and(other: InputPattern) =
    patternOf("Value must match both ${this.errorMessage} and ${other.errorMessage}") {
        this.match(it) && other.match(it)
    }

infix fun InputPattern.or(other: InputPattern) =
    patternOf("Value must match either ${this.errorMessage} or ${other.errorMessage}") {
        this.match(it) || other.match(it)
    }

infix fun InputPattern.xor(other: InputPattern) =
    patternOf("Value must match exactly one of ${this.errorMessage} and ${other.errorMessage}") {
        this.match(it) xor other.match(it)
    }

@Suppress("MemberVisibilityCanBePrivate")
object InputPatterns {
    val Any = patternOf { true }
    val EmptyOnly = patternOf("Value must be empty") { it.isEmpty() }
    val IntOnly = patternOf("Value must be an integer") { it.toIntOrNull() != null }
    val BooleanOnly =
        patternOf("Value must be a boolean which in either 'true' or 'false") { it.toBooleanStrictOrNull() != null }
    val FloatOnly = patternOf { it.toFloatOrNull() != null }
    val IntOrEmpty = (EmptyOnly or IntOnly).withErrorMessage("Value must be an integer or empty")
    val FloatOrEmpty = (EmptyOnly or FloatOnly).withErrorMessage("Value must be a float or empty")
    val BooleanOrEmpty =
        (BooleanOnly or EmptyOnly).withErrorMessage("Value must be a boolean or empty which in either 'true' or 'false' or empty")
    val NotEmpty = patternOf("Value must not be empty") { it.isNotEmpty() }
    val NotBlank = patternOf("Value must not be blank") { it.isNotBlank() }

    class IntRange(val range: kotlin.ranges.IntRange) : InputPattern {
        override val errorMessage = "Value must be between ${range.first} and ${range.last}"
        override fun match(input: String) = input.toIntOrNull() in range
    }

    class FloatRange(val range: ClosedFloatingPointRange<Float>) : InputPattern {
        override val errorMessage = "Value must be between ${range.start} and ${range.endInclusive}"
        override fun match(input: String) = input.toFloatOrNull()?.let { it in range } ?: false
    }
}

@Composable
fun rememberIntRangePattern(range: kotlin.ranges.IntRange) = remember { IntRange(range) }

@Composable
fun rememberFloatRangePattern(range: ClosedFloatingPointRange<Float>) =
    remember { FloatRange(range) }