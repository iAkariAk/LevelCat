@file:OptIn(FlowPreview::class)

package com.akari.levelcat.integratedlevel.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.akari.levelcat.integratedlevel.model.Component
import com.akari.levelcat.integratedlevel.util.copyUnsafely
import kotlinx.coroutines.FlowPreview
import kotlin.reflect.KClass

class CoffeeCat private constructor(private val componetConfiguration: ComponetConfiguration) {
    companion object {
        fun fromClass(kclass: KClass<*>) = CoffeeCat(ComponetConfiguration.fromClass(kclass))
        inline fun <reified T> fromClass() = fromClass(T::class)
    }

    private val args = mutableMapOf<PropertyConfiguration, Any?>()


    @Composable
    fun Editor(
        componet: Component,
        onComponentChange: (Component) -> Unit,
        onComponentDelete: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
//        componetConfiguration.kclass.functions.find { it.name == "copy" }?.let { function ->
//            function.callBy(
//                function.parameters.mapIndexed { index, value ->
//                    value to args.values.elementAt(
//                        index
//                    )
//                }.toMap()
//            )
//        }

        OutlinedCard(
            modifier = modifier,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = componetConfiguration.labelName,
                        style = MaterialTheme.typography.titleLarge
                    )
                    IconButton(onClick = onComponentDelete) {
                        Icon(Icons.Outlined.Delete, contentDescription = "delete")
                    }
                }

                HorizontalDivider()
                componetConfiguration.properties.forEach { property ->
                    ItemEditor(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        property = property,
                        onValueChange = { arg ->
                            args[property] = arg
                            val newComponent = componet.copyUnsafely(
                                args.mapKeys { (key, _) -> key.kproperty.name }
                            )
                            onComponentChange(newComponent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ItemEditor(
    property: PropertyConfiguration,
    onValueChange: (Any?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var value by remember { mutableStateOf("") }
    OutlinedTextField(
        modifier = modifier,
        label = { Text(property.labelName) },
        value = value,
        onValueChange = {
            value = it
            onValueChange(value.toTyped(property.kproperty.returnType))
        }
    )
}