package com.example.bankapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class annotated with @HiltAndroidApp to enable Hilt dependency injection.
 */
@HiltAndroidApp
class BankApp : Application()
