package com.akari.levelcat.integratedlevel.ui.dsl

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class LabelName(val name: String)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class Description(val content: String)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
@InputPattern
annotation class Ignored

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class InputPattern

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
@InputPattern
annotation class IntRange(val min: Int, val max: Int)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
@InputPattern
annotation class FloatRange(val min: Float, val max: Float)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
@InputPattern
annotation class IntOnly

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
@InputPattern
annotation class EnumElement<T>(val enumClass: KClass<out Enum<T>>) where  T : Enum<T>, T : I18nedEnum

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
@InputPattern
annotation class EditorItem(val composableClass: KClass<out ComposableProvider>)



