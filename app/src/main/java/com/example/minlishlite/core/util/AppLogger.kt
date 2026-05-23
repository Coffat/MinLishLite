package com.example.minlishlite.core.util

import android.util.Log
import com.example.minlishlite.BuildConfig

object AppLogger {
    private const val TAG = "MinLishLite"

    fun d(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message)
        }
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            if (throwable != null) {
                Log.e(TAG, message, throwable)
            } else {
                Log.e(TAG, message)
            }
        }
    }
}
