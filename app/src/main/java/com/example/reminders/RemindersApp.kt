package com.example.reminders

import android.app.Application
import com.example.reminders.util.NotificationUtil
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RemindersApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationUtil.createChannels(this)
    }
}
