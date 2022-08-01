package com.example.androidpaging3.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.androidpaging3.GlobalApp
import com.example.androidpaging3.models.PhotoModel
import com.example.androidpaging3.paging.VideoPageLoaded
import com.example.androidpaging3.paging.VideoPagingSource
import com.example.androidpaging3.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

private const val TAG = "MainViewModel"
private const val PAGE_SIZE = 20

class MainViewModel : ViewModel() {

    private val enableErrorsFlow = MutableStateFlow(false)

    val photosFlow: Flow<PagingData<PhotoModel>>

    init {
        photosFlow = getPagePhoto()
    }

    fun getPagePhoto(): Flow<PagingData<PhotoModel>> {
        val loader: VideoPageLoaded = { pageIndex, pageSize ->
            getPhotos(pageIndex, pageSize)
        }
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { VideoPagingSource(loader, PAGE_SIZE) }
        ).flow.cachedIn(viewModelScope)
    }

    private suspend fun getPhotos(
        pageIndex: Int,
        pageSize: Int
    ): List<PhotoModel> = withContext(Dispatchers.IO) {
        delay(2000) // some delay to test loading state

        // if "Enable Errors" checkbox is checked -> throw exception
        if (enableErrorsFlow.value) throw IllegalStateException("Error!")

        // calculate offset value required by DAO
        val offset = pageIndex * pageSize

        // get page
        val list = FileUtils.getAllPhotoFromDevice(GlobalApp.context, pageIndex, pageSize)

        // map UserDbEntity to User
        return@withContext list
    }
}