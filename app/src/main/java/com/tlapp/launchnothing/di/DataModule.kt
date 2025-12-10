package com.tlapp.launchnothing.di

import android.content.Context
import com.tlapp.launchnothing.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context) = AppDatabase.getDatabase(context)

    @Provides
    @Singleton
    fun provideAppDao(appDatabase: AppDatabase) = appDatabase.appDao()

    @Provides
    @Singleton
    fun provideExternalScope() = CoroutineScope(SupervisorJob() + Dispatchers.IO)
}
