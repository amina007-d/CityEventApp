package com.example.cityeventproject.core.logging

import android.util.Log
import com.example.cityeventproject.BuildConfig

object AppLogger {

    private const val GLOBAL_TAG = "CityEventApp"

    private fun buildMessage(message: String, params: Map<String, Any?>?): String {
        if (params.isNullOrEmpty()) return message
        val structured = params.entries.joinToString(" ") { "${it.key}=${it.value}" }
        return "$message | $structured"
    }

    fun d(tag: String, message: String, params: Map<String, Any?>? = null) {
        if (BuildConfig.DEBUG) Log.d("$GLOBAL_TAG-$tag", buildMessage(message, params))
    }

    fun i(tag: String, message: String, params: Map<String, Any?>? = null) {
        if (BuildConfig.DEBUG) Log.i("$GLOBAL_TAG-$tag", buildMessage(message, params))
    }

    fun w(tag: String, message: String, params: Map<String, Any?>? = null, tr: Throwable? = null) {
        Log.w("$GLOBAL_TAG-$tag", buildMessage(message, params), tr)
    }

    fun e(tag: String, message: String, params: Map<String, Any?>? = null, tr: Throwable? = null) {
        Log.e("$GLOBAL_TAG-$tag", buildMessage(message, params), tr)
    }
}
