package com.example.reminders.data.repository

import com.example.reminders.data.db.CategoryDao
import com.example.reminders.data.model.Category
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(private val dao: CategoryDao) {
    fun getAll(): Flow<List<Category>> = dao.getAll()
    suspend fun getAllOnce(): List<Category> = dao.getAllOnce()
    suspend fun getById(id: Int): Category? = dao.getById(id)
    suspend fun count(): Int = dao.count()

    suspend fun save(category: Category): Int {
        return if (category.id == 0) dao.insert(category).toInt()
        else { dao.update(category); category.id }
    }

    suspend fun delete(category: Category) = dao.delete(category)
}
