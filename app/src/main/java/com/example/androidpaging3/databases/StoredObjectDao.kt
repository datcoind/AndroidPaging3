package com.example.androidpaging3.databases

import androidx.room.*
import com.example.androidpaging3.models.StoredObject

@Dao
interface StoredObjectDao {

    @Query("SELECT * FROM StoredObject ORDER BY _id LIMIT :limit OFFSET :offset")
    fun getAllPaged(limit: Int = 10, offset: Int = 1): List<StoredObject>

    @Update
    suspend fun update(item: StoredObject): Int

    @Insert
    suspend fun insert(storedObject: StoredObject): Long

    @Delete
    suspend fun delete(storedObject: StoredObject)
}