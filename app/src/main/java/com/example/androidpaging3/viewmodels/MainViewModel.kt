package com.example.androidpaging3.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.room.Room
import com.example.androidpaging3.GlobalApp
import com.example.androidpaging3.databases.AppDatabase
import com.example.androidpaging3.models.PhotoModel
import com.example.androidpaging3.paging.RoomPagingSource
import com.example.androidpaging3.paging.VideoPagingSource
import com.example.androidpaging3.utils.FileUtils
import kotlinx.coroutines.flow.Flow

private const val TAG = "MainViewModel"

class MainViewModel : ViewModel() {
    var offset = 0

    val dao = Room.databaseBuilder(GlobalApp.context, AppDatabase::class.java, "myDB")
        .build()
        .storedObjectDao()
    val itemsRoom = Pager(PagingConfig(pageSize = 20, enablePlaceholders = true),
        pagingSourceFactory = { RoomPagingSource(dao) }).flow.cachedIn(viewModelScope)

    val photos: Flow<PagingData<PhotoModel>> = Pager(
        PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { VideoPagingSource(GlobalApp.context) }).flow.cachedIn(viewModelScope)

    private fun getPhotos(): MutableList<PhotoModel> {
        val listTemp = FileUtils.getAllPhotoFromDevice(GlobalApp.context, offset)
        Log.e(TAG, "getPhotos: " + listTemp.size)
        offset++
        return listTemp
    }
}