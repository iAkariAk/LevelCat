package com.akari.levelcat.util

import android.util.Log
import com.akari.levelcat.BuildConfig

interface Logger {
    fun info(message: String, throwable: Throwable? = null)
    fun error(message: String, throwable: Throwable? = null)
    fun debug(message: String, throwable: Throwable? = null)
    fun warn(message: String, throwable: Throwable? = null)
}

@JvmInline
value class AndroidLogger(private val tag: String) : Logger {
    override fun info(message: String, throwable: Throwable?) { Log.i(tag, message, throwable) }
    override fun error(message: String, throwable: Throwable?) { Log.e(tag, message, throwable) }
    override fun debug(message: String, throwable: Throwable?) { Log.d(tag, message, throwable) }
    override fun warn(message: String, throwable: Throwable?) { Log.w(tag, message, throwable) }
    override fun toString() = "AndroidLogger(tag=$tag)"
}

@JvmInline
value class DebugOnlyAndroidLogger(private val logger: Logger) : Logger {
    override fun info(message: String, throwable: Throwable?) { if (BuildConfig.DEBUG) logger.info(message, throwable) }
    override fun error(message: String, throwable: Throwable?) { if (BuildConfig.DEBUG) error(message, throwable) }
    override fun debug(message: String, throwable: Throwable?) { if (BuildConfig.DEBUG) debug(message, throwable) }
    override fun warn(message: String, throwable: Throwable?) { if (BuildConfig.DEBUG) warn(message, throwable) }
    override fun toString() = "DebugOnlyAndroidLogger(logger=$logger)"
}

val logger: Logger = AndroidLogger("AkLevelcat")
val debugLogger = DebugOnlyAndroidLogger(logger)