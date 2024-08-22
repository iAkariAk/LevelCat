package com.akari.levelcat.integratedlevel.ui

import com.akari.levelcat.integratedlevel.ui.dsl.Description
import com.akari.levelcat.integratedlevel.ui.dsl.EditorItem
import com.akari.levelcat.integratedlevel.ui.dsl.Ignored
import com.akari.levelcat.integratedlevel.ui.dsl.LabelName
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

data class ComponetConfiguration(
    val labelName: String,
    val description: String?,
    val kclass: KClass<*>,
    val properties: List<PropertyConfiguration>,
) {
    companion object Factory {
        fun fromClass(kclass: KClass<*>): ComponetConfiguration {
            return ComponetConfiguration(
                labelName = kclass.findAnnotation<LabelName>()?.name ?: kclass.simpleName
                ?: "Unknow",
                description = kclass.findAnnotation<Description>()?.content ?: "Unknow",
                kclass = kclass,
                properties = kclass.memberProperties
                    .filterNot { it.hasAnnotation<Ignored>() }
                    .map(PropertyConfiguration.Factory::fromProperty)
            )
        }
    }
}

data class PropertyConfiguration(
    val labelName: String,
    val description: String?,
    val matcher: InputPattern?,
    val editorItemClass: KClass<*>? = null,
    val kproperty: KProperty<*>,
) {
    companion object Factory {
        fun fromProperty(kproperty: KProperty<*>): PropertyConfiguration {
            return PropertyConfiguration(
                labelName = kproperty.findAnnotation<LabelName>()?.name ?: kproperty.name,
                description = kproperty.findAnnotation<Description>()?.content ?: "Unknow",
                matcher = kproperty.findInputPattern(),
                editorItemClass = kproperty.findAnnotation<EditorItem>()?.composableClass,
                kproperty = kproperty
            )
        }
    }
}

