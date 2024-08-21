package com.akari.levelcat.ui.util

import androidx.compose.runtime.Composable
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime

@Composable
fun formatMillisecondAsI18nString(milliseconds: Long): String {
    val instant = Instant.fromEpochMilliseconds(milliseconds)
    val datetime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val formatter = LocalDateTime.Formats.ISO // FIXME: I18n datetime formatter
    return datetime.format(formatter)
}