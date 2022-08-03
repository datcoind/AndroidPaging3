package com.example.androidpaging3.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.androidpaging3.GlobalApp
import com.example.androidpaging3.databases.AppDatabase
import com.example.androidpaging3.models.StoredObject
import com.example.androidpaging3.paging.RoomPageLoaded
import com.example.androidpaging3.paging.RoomPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

private const val PAGE_SIZE = 20

class RoomViewModel : ViewModel() {
    private val dao = AppDatabase.getAppDbInstance(GlobalApp.context).storedObjectDao()
    private val enableErrorsFlow = MutableStateFlow(false)

    val usersFlow: Flow<PagingData<StoredObject>>

    init {
        usersFlow = getPageRoom()
    }

    fun getPageRoom(): Flow<PagingData<StoredObject>> {
        // PagingData là 1 danh sách chứa các mục dữ liệu của bạn và gọi PagingSource để tải các phần tử.
        // PagingSource để load các item chuyển vào PagingData hiển thị lên UI.
        val loader: RoomPageLoaded = { pageIndex, pageSize ->
            getUsers(pageIndex, pageSize)
        }
        // Pager dùng để lấy dữ liệu từ PagingSource trả về. là các item.
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { RoomPagingSource(loader, PAGE_SIZE) }
        ).flow.cachedIn(viewModelScope)
    }

    private suspend fun getUsers(
        pageIndex: Int,
        pageSize: Int
    ): List<StoredObject> = withContext(Dispatchers.IO) {

        delay(2000) // some delay to test loading state

        // if "Enable Errors" checkbox is checked -> throw exception
        if (enableErrorsFlow.value) throw IllegalStateException("Error!")

        // calculate offset value required by DAO
        val offset = pageIndex * pageSize

        // get page
        val list = dao.getAllPaged(pageSize, offset)

        // map UserDbEntity to User
        return@withContext list
    }
}