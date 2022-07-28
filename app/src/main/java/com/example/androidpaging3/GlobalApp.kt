package com.example.androidpaging3

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class GlobalApp : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: GlobalApp

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}