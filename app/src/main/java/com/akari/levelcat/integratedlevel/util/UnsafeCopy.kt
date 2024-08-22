package com.akari.levelcat.integratedlevel.util

fun <T> T.copyUnsafely(args: Map<String, Any?>): T {
//    val copyFunction =
//        T::class.memberFunctions.find { it.name == "copy" }!!
//    val argsOfCallBy = args.mapKeys { (name, value) ->
//        copyFunction.parameters.find { parameter -> name == parameter.name }!!
//    }
//    return copyFunction.callBy(
//        mapOf(copyFunction.parameters.first() to this) + argsOfCallBy
//    ) as T
//    val jclass = T::class.java
//    val copy = jclass.declaredMethods.find { it.name == "copy" }!!.apply { isAccessible = true }
//    val copyedObj = copy(this, TODO("不会")) as T
//    Log.d("copyedObj", copyedObj.toString())
//    args.forEach { (name, value) ->
//        val property = jclass.getDeclaredField(name)
//        property.isAccessible = true
//        property.set(copyedObj, value)
//    }
//    return copyedObj

    requireNotNull(this) { "this cannot be null" }
    val jclass = this!!::class.java
    val newObj = Unsafe.allocateInstance(jclass)

    jclass.fields.asSequence()
        .onEach { it.isAccessible = true }
        .forEach { field ->
            val newValue = args.getOrElse(field.name) {  field.get(this) }
            field.set(newObj, newValue)
        }
    return newObj
}
