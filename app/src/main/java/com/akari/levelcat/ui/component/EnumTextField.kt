@file:OptIn(ExperimentalMaterial3Api::class)

package com.akari.levelcat.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import kotlin.enums.enumEntries

@Composable
inline fun <reified E : Enum<E>> ExposedDropdownMenuBoxScope.EnumExposedDropdownMenu(
    expanded: Boolean,
    crossinline onExpandedChange: (Boolean) -> Unit,
    crossinline onEntryChange: (E) -> Unit,
) {
    ExposedDropdownMenu(
        expanded = expanded,
        onDismissRequest = { onExpandedChange(false) },
    ) {
        enumEntries<E>().forEach { entry ->
            DropdownMenuItem(
                onClick = {
                    onEntryChange(entry)
                    onExpandedChange(false)
                },
                text = { Text(entry.name) },
                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
            )
        }
    }
}

@Composable
inline fun <reified E : Enum<E>> OutlinedEnumTextField(
    entry: E,
    crossinline onEnterChange: (E) -> Unit,
    modifier: Modifier = Modifier,
    crossinline entryName: (E) -> String = { it.name }
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(),
            value = entryName(entry),
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
        )

        EnumExposedDropdownMenu<E>(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            onEntryChange = onEnterChange
        )
    }
}

@Composable
inline fun <reified E : Enum<E>> EnumText(
    entry: E,
    crossinline onEnterChange: (E) -> Unit,
    modifier: Modifier = Modifier,
    crossinline entryName: (E) -> String = { it.name },
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    style: TextStyle = LocalTextStyle.current
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .clickable { expanded = true },
            text = entryName(entry),
            color = color,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            style = style,
        )

        EnumExposedDropdownMenu<E>(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            onEntryChange = onEnterChange
        )
    }
}