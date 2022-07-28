package com.example.androidpaging3.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.androidpaging3.models.StoredObject

@Database(entities = [StoredObject::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun storedObjectDao(): StoredObjectDao
}