@file:OptIn(ExperimentalMaterial3Api::class)

package com.akari.levelcat.level.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.akari.levelcat.level.model.constant.ConstantEnum
import com.akari.levelcat.level.util.InputPattern
import kotlin.enums.enumEntries


@Composable
fun ComponentCard(
    componentName: String,
    modifier: Modifier = Modifier,
    onComponentDelete: () -> Unit = {},
    shape: Shape = CardDefaults.outlinedShape,
    colors: CardColors = CardDefaults.outlinedCardColors(),
    elevation: CardElevation = CardDefaults.outlinedCardElevation(),
    border: BorderStroke = CardDefaults.outlinedCardBorder(),
    editAreaContent: @Composable /*ComponentCardScope.*/() -> Unit,
) {
    OutlinedCard(
        modifier = modifier,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = componentName,
                    style = MaterialTheme.typography.titleMedium,
                )
                IconButton(onClick = onComponentDelete) {
                    Icon(Icons.Outlined.Delete, contentDescription = "delete")
                }

            }
            HorizontalDivider()
            editAreaContent()
        }
    }
}

@Composable
fun ComponentTextField(
    propertyName: String,
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = {},
    onValueValidated: (String) -> Unit = {},
    onValueUnvalidated: (String) -> Unit = {},
    pattern: InputPattern? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
) {
    OutlinedPatternedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        label = { Text(propertyName) },
        value = value,
        onValueChange = onValueChange,
        onValueValidated = onValueValidated,
        onValueUnvalidated = onValueUnvalidated,
        pattern = pattern,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        prefix = prefix,
        suffix = suffix,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors
    )
}

@Composable
inline fun <reified E> ComponentEnumField(
    propertyName: String,
    entry: E,
    crossinline onEntryChange: (E) -> Unit,
    modifier: Modifier = Modifier,
) where E : Enum<E>, E : ConstantEnum {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            value = entry.displayName,
            onValueChange = {},
            label = { Text(propertyName) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            readOnly = true,
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            enumEntries<E>().forEach { entry ->
                DropdownMenuItem(
                    text = { Text(entry.displayName) },
                    onClick = {
                        onEntryChange(entry)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}