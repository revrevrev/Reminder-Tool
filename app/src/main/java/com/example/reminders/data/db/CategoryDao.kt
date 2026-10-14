package com.example.reminders.data.db

import androidx.room.*
import com.example.reminders.data.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY id ASC")
    fun getAll(): Flow<List<Category>>

    @Query("SELECT * FROM categories ORDER BY id ASC")
    suspend fun getAllOnce(): List<Category>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Int): Category?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category): Long

    @Update
    suspend fun update(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun count(): Int
}
