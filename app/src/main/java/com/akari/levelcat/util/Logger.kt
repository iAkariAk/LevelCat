package com.akari.levelcat.util

import android.util.Log

interface Logger {
    fun info(message: String, throwable: Throwable? = null)
    fun error(message: String, throwable: Throwable? = null)
    fun debug(message: String, throwable: Throwable? = null)
    fun warn(message: String, throwable: Throwable? = null)
}

@JvmInline
value class AndroidLogger(val tag: String) : Logger {
    override fun info(message: String, throwable: Throwable?) { Log.i(tag, message, throwable) }
    override fun error(message: String, throwable: Throwable?) {  Log.e(tag, message, throwable) }
    override fun debug(message: String, throwable: Throwable?) {  Log.d(tag, message, throwable) }
    override fun warn(message: String, throwable: Throwable?) { Log.w(tag, message, throwable) }
}

val logger: Logger = AndroidLogger("AkLevelcat")