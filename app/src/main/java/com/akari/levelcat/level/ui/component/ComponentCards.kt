@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.akari.levelcat.level.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.akari.levelcat.level.model.constant.ConstantEnum
import com.akari.levelcat.level.util.InputPattern
import com.akari.levelcat.ui.component.EnumText
import com.akari.levelcat.ui.component.OutlinedPatternedTextField
import com.akari.levelcat.ui.component.animateEnter
import kotlin.enums.enumEntries

@Composable
fun EmptyComponentCard(
    componentName: String,
    modifier: Modifier = Modifier,
    onComponentDelete: () -> Unit = {},
    shape: Shape = CardDefaults.outlinedShape,
    colors: CardColors = CardDefaults.outlinedCardColors(),
    elevation: CardElevation = CardDefaults.outlinedCardElevation(),
    border: BorderStroke = CardDefaults.outlinedCardBorder(),
) {
    ComponentCard(
        componentName = componentName,
        modifier = modifier,
        onComponentDelete = onComponentDelete,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )
    }
}

@Composable
fun ComponentCard(
    componentName: String,
    modifier: Modifier = Modifier,
    onComponentDelete: () -> Unit = {},
    shape: Shape = CardDefaults.outlinedShape,
    colors: CardColors = CardDefaults.outlinedCardColors(),
    elevation: CardElevation = CardDefaults.outlinedCardElevation(),
    border: BorderStroke = CardDefaults.outlinedCardBorder(),
    editAreaContent: @Composable () -> Unit,
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
fun ComponentSwitch(
    propertyName: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(propertyName)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Composable
inline fun <reified E> ComponentEnumField(
    propertyName: String,
    entry: E,
    noinline onEntryChange: (E) -> Unit,
    modifier: Modifier = Modifier,
) where E : Enum<E>, E : ConstantEnum =
    ComponentEnumField<E>(
        propertyName = propertyName,
        entries = enumEntries<E>(),
        entry = entry,
        onEntryChange = onEntryChange,
        modifier = modifier,
    )

@Composable
fun <E> ComponentEnumField(
    propertyName: String,
    entries: List<E>,
    entry: E,
    onEntryChange: (E) -> Unit,
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
            entries.forEach { entry ->
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


@Composable
fun <T> ComponentListField(
    propertyName: String,
    itemListState: SnapshotStateList<T> = mutableStateListOf(),
    initialItem: () -> T,
    itemContent: @Composable (index: Int, item: T, onItemChange: (index: Int, value: T) -> Unit, onItemDelete: () -> Unit) -> Unit,
    itemKey: ((index: Int, item: T) -> Any)? = { index, _ -> index },
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaults.outlinedShape,
    colors: CardColors = CardDefaults.outlinedCardColors(),
    elevation: CardElevation = CardDefaults.outlinedCardElevation(),
    border: BorderStroke = CardDefaults.outlinedCardBorder(),
) {
    OutlinedCard(
        modifier = modifier,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    propertyName,
                    style = MaterialTheme.typography.bodyMedium
                )
                IconButton(onClick = { itemListState += initialItem() }) {
                    Icon(Icons.Outlined.Add, contentDescription = "add")
                }
            }
            HorizontalDivider()
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
            ) {
                itemsIndexed(
                    items = itemListState,
                    key = itemKey,
                ) { index, item ->
                    Box(
                        modifier = Modifier
                            .animateItem(fadeInSpec = null, fadeOutSpec = null)
                            .animateEnter(),
                    ) {
                        itemContent(
                            index,
                            item,
                            { index, value -> itemListState[index] = value },
                            { itemListState.remove(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
inline fun <reified E : Enum<E>> ComponentEnumListItem(
    item: E,
    noinline onItemChange: (E) -> Unit,
    noinline onItemDelete: () -> Unit,
    modifier: Modifier = Modifier
) = ComponentEnumListItem(
    entries = enumEntries<E>(),
    item = item,
    onItemChange = onItemChange,
    onItemDelete = onItemDelete,
    modifier = modifier
)

@Composable
fun <E : Enum<E>> ComponentEnumListItem(
    entries: List<E>,
    item: E,
    onItemChange: (E) -> Unit,
    onItemDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        EnumText(
            modifier = Modifier.fillMaxWidth(),
            entries = entries,
            entry = item,
            onEnterChange = onItemChange,
        )

        IconButton(onClick = onItemDelete) {
            Icon(Icons.Default.Delete, null)
        }
    }
}