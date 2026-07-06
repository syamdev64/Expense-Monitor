package com.example.expensemonitor.di

import android.content.Context
import androidx.room.Room
import com.example.expensemonitor.roomdb.ExpenseDao
import com.example.expensemonitor.roomdb.ExpenseDatabase
import com.example.expensemonitor.roomdb.SettingsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): ExpenseDatabase {

        return Room.databaseBuilder(
            context,
            ExpenseDatabase::class.java,
            "expense_db"
        ).build()

    }

    @Provides
    fun provideExpenseDao(
        db: ExpenseDatabase
    ): ExpenseDao {

        return db.expenseDao()

    }

    @Provides
    fun provideSettingsDao(
        db: ExpenseDatabase
    ): SettingsDao {

        return db.settingsDao()

    }

}