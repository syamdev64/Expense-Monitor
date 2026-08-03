package com.example.expensemonitor.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        ExpenseEntity::class,
        MonthlyBudgetEntity::class
    ],
    version = 4
)
abstract class ExpenseDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun settingsDao(): SettingsDao


    companion object {

        @Volatile
        private var INSTANCE: ExpenseDatabase? = null

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE monthtotal ADD COLUMN isBiometricEnabled INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): ExpenseDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context,
                    ExpenseDatabase::class.java,
                    "expense_db"
                )
                    .addMigrations(MIGRATION_3_4)
                    .build()

                INSTANCE = instance

                instance
            }

        }

    }

}