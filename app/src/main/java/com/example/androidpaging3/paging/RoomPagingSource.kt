package com.example.androidpaging3.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.androidpaging3.databases.StoredObjectDao
import com.example.androidpaging3.models.StoredObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val TAG = "VideoPagingSource"
private const val NOTE_STARTING_PAGE_INDEX = 1
private const val NOTE_ENDING_PAGE_INDEX = 20

class RoomPagingSource(private val dao: StoredObjectDao) :
    PagingSource<Int, StoredObject>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoredObject> {
        val position = params.key ?: NOTE_STARTING_PAGE_INDEX
        Log.e(TAG, "loadRoom: " + position + " - " + params.loadSize)
        try {
            var list = listOf<StoredObject>()
            GlobalScope.launch {
                 list = dao.getAllPaged()
            }

            return LoadResult.Page(
                data = list,
                prevKey = if (position == NOTE_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (list.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, StoredObject>): Int? {
        return state.anchorPosition
    }
}