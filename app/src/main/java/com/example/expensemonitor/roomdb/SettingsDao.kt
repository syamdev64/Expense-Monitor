package com.example.expensemonitor.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: MonthlyBudgetEntity)

    @Query("SELECT * FROM monthtotal WHERE id = 1")
    fun getSettings(): Flow<MonthlyBudgetEntity?>

}