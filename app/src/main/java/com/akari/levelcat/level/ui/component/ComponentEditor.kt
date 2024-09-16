@file:Suppress("FunctionName")

package com.akari.levelcat.level.ui.component

import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.akari.levelcat.level.model.constant.ConstantEnum
import com.akari.levelcat.level.util.InputPattern
import kotlin.enums.enumEntries

@LayoutScopeMarker
object ComponentScope

@Composable
fun ComponentEditor(
    modifier: Modifier = Modifier,
    name: String,
    onComponentDelete: () -> Unit,
    builderAction: @Composable ComponentScope.() -> Unit
) {
    ComponentEditor(
        modifier = modifier,
        name = { Text(name) },
        onComponentDelete = onComponentDelete,
        builderAction = builderAction
    )
}

@Composable
fun ComponentEditor(
    modifier: Modifier = Modifier,
    name: @Composable () -> Unit,
    onComponentDelete: () -> Unit,
    builderAction: @Composable ComponentScope.() -> Unit
) {
    ComponentCard(
        modifier = modifier,
        componentName = name,
        onComponentDelete = onComponentDelete,
    ) {
        with(ComponentScope) {
            builderAction()
        }
    }
}


@Composable
inline fun <reified E> ComponentScope.EnumField(
    name: String,
    state: MutableState<E>,
) where E : Enum<E>, E : ConstantEnum {
    ComponentEnumField<E>(
        propertyName = name,
        entry = state.value,
        onEntryChange = { changed ->
            state.value = changed
        }
    )
}

@Composable
fun ComponentScope.Switch(
    name: String,
    state: MutableState<Boolean>,
) {
    ComponentSwitch(
        modifier = Modifier.fillMaxWidth(),
        propertyName = name,
        checked = state.value,
        onCheckedChange = { changed ->
            state.value = changed
        }
    )
}

@Composable
fun ComponentScope.InputField(
    name: String,
    state: MutableState<String>,
    pattern: InputPattern? = null,
) {
    ComponentTextField(
        modifier = Modifier.fillMaxWidth(),
        propertyName = name,
        pattern = pattern,
        value = state.value,
        onValueChange = { changed ->
            state.value = changed
        }
    )
}

@Composable
fun <T> ComponentScope.ListField(
    name: String,
    state: SnapshotStateList<T>,
    initialItem: () -> T,
    itemContent: @Composable (index: Int, item: T, onItemChange: (index: Int, value: T) -> Unit, onItemDelete: () -> Unit) -> Unit
) {
    ComponentListField(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 500.dp)
            .padding(top = 8.dp),
        propertyName = name,
        itemListState = state,
        initialItem = initialItem,
        itemContent = itemContent,
    )
}

@Composable
inline fun <reified E> ComponentScope.EnumListField(
    name: String,
    state: SnapshotStateList<E>,
    initialItem: E = enumEntries<E>().first(),
) where E : Enum<E>, E : ConstantEnum = EnumListField(
    name = name,
    state = state,
    entries = enumEntries(),
    initialItem = initialItem
)

@Composable
fun <E> ComponentScope.EnumListField(
    name: String,
    state: SnapshotStateList<E>,
    entries: List<E>,
    initialItem: E = entries.first(),
) where E : Enum<E>, E : ConstantEnum {
    ComponentListField(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 500.dp)
            .padding(top = 8.dp),
        propertyName = name,
        itemListState = state,
        initialItem = { initialItem },
        itemContent = { index, item, onItemChange, onItemDelete ->
            ComponentEnumListItem(
                modifier = Modifier.fillMaxWidth(),
                entries = entries,
                item = item,
                onItemChange = { onItemChange(index, it) },
                onItemDelete = onItemDelete
            )
        },
    )
}
