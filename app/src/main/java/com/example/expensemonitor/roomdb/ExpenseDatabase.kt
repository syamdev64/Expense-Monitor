package com.example.expensemonitor.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ExpenseEntity::class,
        MonthlyBudgetEntity::class
    ],
    version = 2
)
abstract class ExpenseDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun settingsDao(): SettingsDao


    companion object {

        @Volatile
        private var INSTANCE: ExpenseDatabase? = null

        fun getDatabase(context: Context): ExpenseDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context,
                    ExpenseDatabase::class.java,
                    "expense_db"
                ).build()

                INSTANCE = instance

                instance
            }

        }

    }

}