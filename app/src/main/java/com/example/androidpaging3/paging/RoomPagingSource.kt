package com.example.androidpaging3.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.androidpaging3.models.StoredObject

private const val TAG = "VideoPagingSource"
private const val NOTE_STARTING_PAGE_INDEX = 1

typealias RoomPageLoaded = suspend (pageIndex: Int, pageSize: Int) -> List<StoredObject>

class RoomPagingSource(
    private val loader: RoomPageLoaded,
    private val pageSize: Int
) :
    PagingSource<Int, StoredObject>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoredObject> {
        // get the index of page to be loaded (it may be NULL, in this case let's load the first page with index = 0)
        val pageIndex = params.key ?: 0
        Log.e(TAG, "load: " + pageIndex + " - " + params.loadSize )
        return try {
            // loading the desired page of users
            val users = loader.invoke(pageIndex, params.loadSize)
            // success! now we can return LoadResult.Page
            return LoadResult.Page(
                data = users,
                // index of the previous page if exists
                prevKey = if (pageIndex == 0) null else pageIndex - 1,
                // index of the next page if exists;
                // please note that 'params.loadSize' may be larger for the first load (by default x3 times)
                nextKey = if (users.size == params.loadSize) pageIndex + (params.loadSize / pageSize) else null
            )
        } catch (e: Exception) {
            // failed to load users -> need to return LoadResult.Error
            LoadResult.Error(
                throwable = e
            )
        }
    }

    override fun getRefreshKey(state: PagingState<Int, StoredObject>): Int? {
        // get the most recently accessed index in the users list:
        val anchorPosition = state.anchorPosition ?: return null
        // convert item index to page index:
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        // page doesn't have 'currentKey' property, so need to calculate it manually:
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

}