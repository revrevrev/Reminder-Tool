package com.example.reminders.di

import android.content.Context
import androidx.room.Room
import com.example.reminders.data.db.AppDatabase
import com.example.reminders.data.db.CategoryDao
import com.example.reminders.data.db.ReminderDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "reminders.db")
            .addCallback(AppDatabase.seedCallback())
            .build()

    @Provides
    fun provideReminderDao(db: AppDatabase): ReminderDao = db.reminderDao()

    @Provides
    fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()
}
