package com.example.androidpaging3.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.androidpaging3.models.StoredObject

@Database(entities = [StoredObject::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun storedObjectDao(): StoredObjectDao

    companion object {
        private var DB_INSTANCE: AppDatabase? = null

        fun getAppDbInstance(context: Context): AppDatabase {
            if (DB_INSTANCE == null) {
                DB_INSTANCE = Room.databaseBuilder(context, AppDatabase::class.java, "myDB")
                    .allowMainThreadQueries()
                    .build()

            }
            return DB_INSTANCE!!
        }
    }
}