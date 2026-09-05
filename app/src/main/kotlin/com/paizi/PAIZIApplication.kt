package com.paizi

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * PAIZI Application Entry Point
 * 
 * Initializes:
 * - Dependency Injection (Hilt)
 * - Logging System (Timber)
 * - Core Services
 * - Database
 * - Configuration
 */
@HiltAndroidApp
class PAIZIApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize logging
        if (BuildConfig.DEBUG_MODE) {
            Timber.plant(Timber.DebugTree())
        }

        Timber.i("PAIZI Application Started")
        Timber.i("Version: ${BuildConfig.VERSION_NAME}")
        Timber.i("Build: ${BuildConfig.VERSION_CODE}")

        // Initialize core services
        initializeCoreServices()
    }

    private fun initializeCoreServices() {
        Timber.d("Initializing Core Services")
        // Services will be initialized by Hilt dependency injection
    }

    companion object {
        const val TAG = "PAIZI"
    }
}
