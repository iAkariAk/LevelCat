@file:Suppress("FunctionName")

package com.akari.levelcat.level.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.akari.levelcat.level.model.constant.ConstantEnum
import com.akari.levelcat.level.util.InputPattern
import com.akari.levelcat.level.util.ObjScopeMaker
import java.util.*
import kotlin.enums.enumEntries

data class ComponentConfig(
    val name: String,
    val properties: List<ComponentPropertyConfig<*>>
)

sealed interface ComponentPropertyConfig<T> {
    val name: String

    data class InputField(
        override val name: String,
        val state: MutableState<String>,
        val pattern: InputPattern? = null,
    ) : ComponentPropertyConfig<String>

    data class Switch(
        override val name: String,
        val state: MutableState<Boolean>,
    ) : ComponentPropertyConfig<Boolean>

    data class EnumField<E>(
        override val name: String,
        val state: MutableState<E>,
        val entries: List<E>,
    ) : ComponentPropertyConfig<E> where E : Enum<E>, E : ConstantEnum

    data class EnumList<E>(
        override val name: String,
        val state: SnapshotStateList<E>,
        val entries: List<E>,
        val initialItem: E,
    ) : ComponentPropertyConfig<E> where E : Enum<E>, E : ConstantEnum
}

fun ComponentConfig(
    name: String,
    builderAction: ComponentConfigBuilderScope.() -> Unit
): ComponentConfig {
    val scope = ComponentConfigBuilderScopeImpl(name)
    scope.run(builderAction)
    return scope.build()
}

@ObjScopeMaker
interface ComponentConfigBuilderScope {
    fun InputField(
        name: String,
        state: MutableState<String>,
        pattern: InputPattern? = null
    )

    fun <E> EnumList(
        name: String,
        state: SnapshotStateList<E>,
        entries: List<E>,
        initialItem: E = entries.first()
    ) where E : Enum<E>, E : ConstantEnum

    fun <E> EnumField(
        name: String,
        state: MutableState<E>,
        entries: List<E>
    ) where E : Enum<E>, E : ConstantEnum


    fun Switch(
        name: String,
        state: MutableState<Boolean>
    )
}

inline fun <reified E> ComponentConfigBuilderScope.EnumField(
    name: String,
    state: MutableState<E>,
) where E : Enum<E>, E : ConstantEnum = EnumField(
    name = name,
    state = state,
    entries = enumEntries<E>()
)

inline fun <reified E> ComponentConfigBuilderScope.EnumList(
    name: String,
    state: SnapshotStateList<E>,
    initialItem: E = enumEntries<E>().first()
) where E : Enum<E>, E : ConstantEnum = EnumList(
    name = name,
    state = state,
    entries = enumEntries<E>(),
    initialItem = initialItem
)


private class ComponentConfigBuilderScopeImpl(
    val name: String
) : ComponentConfigBuilderScope {
    val properties = LinkedList<ComponentPropertyConfig<*>>()

    override fun InputField(
        name: String,
        state: MutableState<String>, pattern: InputPattern?
    ) {
        val config = ComponentPropertyConfig.InputField(name, state, pattern)
        properties.add(config)
    }

    override fun <E> EnumField(
        name: String,
        state: MutableState<E>,
        entries: List<E>,
    ) where E : Enum<E>, E : ConstantEnum {
        val config = ComponentPropertyConfig.EnumField(name, state, entries)
        properties.add(config)
    }

    override fun <E> EnumList(
        name: String,
        state: SnapshotStateList<E>,
        entries: List<E>,
        initialItem: E,
    ) where E : Enum<E>, E : ConstantEnum {
        val config = ComponentPropertyConfig.EnumList(name, state, entries, initialItem)
        properties.add(config)
    }

    override fun Switch(
        name: String,
        state: MutableState<Boolean>
    ) {
        val config = ComponentPropertyConfig.Switch(name, state)
        properties.add(config)
    }

    fun build(): ComponentConfig {
        return ComponentConfig(
            name = name,
            properties = properties
        )
    }
}

@Composable
fun ComponentEditor(
    modifier: Modifier = Modifier,
    onComponentDelete: () -> Unit,
    name: String,
    builderAction: ComponentConfigBuilderScope.() -> Unit
) {
    ComponentEditor(
        modifier = modifier,
        onComponentDelete = onComponentDelete,
        componentConfig = ComponentConfig(name, builderAction)
    )
}

@Composable
fun ComponentEditor(
    modifier: Modifier = Modifier,
    onComponentDelete: () -> Unit,
    componentConfig: ComponentConfig
) {
    ComponentCard(
        modifier = modifier,
        onComponentDelete = onComponentDelete,
        componentName = componentConfig.name
    ) {
        componentConfig.properties.forEach { propertyConfig ->
            when (propertyConfig) {
                is ComponentPropertyConfig.EnumField -> EnumFieldProperty(
                    modifier = Modifier.fillMaxWidth(),
                    config = propertyConfig
                )

                is ComponentPropertyConfig.InputField -> InputProperty(
                    modifier = Modifier.fillMaxWidth(),
                    config = propertyConfig
                )

                is ComponentPropertyConfig.Switch -> SwitchProperty(
                    modifier = Modifier.fillMaxWidth(),
                    config = propertyConfig
                )

                is ComponentPropertyConfig.EnumList -> EnumListProperty(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 500.dp),
                    config = propertyConfig
                )
            }
        }
    }
}

@Composable
private fun <E> EnumFieldProperty(
    config: ComponentPropertyConfig.EnumField<E>,
    modifier: Modifier = Modifier,
) where E : Enum<E>, E : ConstantEnum {
    ComponentEnumField(
        propertyName = "Background",
        entries = config.entries,
        entry = config.state.value,
        onEntryChange = { changed ->
            config.state.value = changed
        }
    )
}

@Composable
private fun SwitchProperty(
    config: ComponentPropertyConfig.Switch,
    modifier: Modifier = Modifier,
) {
    ComponentSwitch(
        modifier = Modifier.fillMaxWidth(),
        propertyName = config.name,
        checked = config.state.value,
        onCheckedChange = { changed ->
            config.state.value = changed
        }
    )
}

@Composable
private fun InputProperty(
    config: ComponentPropertyConfig.InputField,
    modifier: Modifier = Modifier,
) {
    ComponentTextField(
        modifier = Modifier.fillMaxWidth(),
        propertyName = config.name,
        pattern = config.pattern,
        value = config.state.value,
        onValueChange = { changed ->
            config.state.value = changed
        }
    )
}

@Composable
private fun <E> EnumListProperty(
    config: ComponentPropertyConfig.EnumList<E>,
    modifier: Modifier = Modifier,
) where E : Enum<E>, E : ConstantEnum {
    ComponentListField(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 500.dp)
            .padding(top = 8.dp),
        propertyName = config.name,
        itemListState = config.state,
        initialItem = { config.initialItem },
        itemContent = { index, item, onItemChange, onItemDelete ->
            ComponentEnumListItem(
                modifier = Modifier.fillMaxWidth(),
                entries = config.entries,
                item = item,
                onItemChange = { onItemChange(index, it) },
                onItemDelete = onItemDelete
            )
        },
    )
}
