package com.example.androidpaging3.paging

import android.content.Context
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.androidpaging3.models.PhotoModel
import com.example.androidpaging3.models.StoredObject
import com.example.androidpaging3.utils.FileUtils

private const val TAG = "VideoPagingSource"
private const val NOTE_STARTING_PAGE_INDEX = 0

typealias VideoPageLoaded = suspend (pageIndex: Int, pageSize: Int) -> List<PhotoModel>

class VideoPagingSource(  private val loader: VideoPageLoaded,
                          private val pageSize: Int) :
    PagingSource<Int, PhotoModel>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoModel> {
        val position = params.key ?: NOTE_STARTING_PAGE_INDEX
        Log.e(TAG, "load: " + position + " - " + params.loadSize)
        val photos = loader.invoke(position, params.loadSize)
        return try {
            LoadResult.Page(
                data = photos,
                prevKey = if (position == NOTE_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (photos.isEmpty()) null else position + (params.loadSize / pageSize)
            )
        } catch (e: Exception) {
            // failed to load users -> need to return LoadResult.Error
            LoadResult.Error(
                throwable = e
            )
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PhotoModel>): Int? {
        // get the most recently accessed index in the users list:
        val anchorPosition = state.anchorPosition ?: return null
        // convert item index to page index:
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        // page doesn't have 'currentKey' property, so need to calculate it manually:
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }
}