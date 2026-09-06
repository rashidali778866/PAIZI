package com.paizi.core.logging

import timber.log.Timber

/**
 * Logging Manager
 * 
 * Handles:
 * - Event logging
 * - Error tracking
 * - Performance metrics
 */
object LoggingManager {

    fun init(debugMode: Boolean) {
        if (debugMode) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
    }

    fun logEvent(event: String, data: Map<String, Any> = emptyMap()) {
        Timber.i("[EVENT] $event - $data")
    }

    fun logError(error: Exception, message: String = "") {
        Timber.e(error, "[ERROR] $message")
    }

    fun logWarning(message: String) {
        Timber.w("[WARNING] $message")
    }
}

private class ReleaseTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // In release, don't log to console
        // Could send to remote logging service instead
    }
}
