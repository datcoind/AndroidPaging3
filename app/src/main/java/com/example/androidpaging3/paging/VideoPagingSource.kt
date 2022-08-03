package com.example.androidpaging3.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.androidpaging3.models.PhotoModel

private const val TAG = "VideoPagingSource"

typealias VideoPageLoaded = suspend (pageIndex: Int, pageSize: Int) -> List<PhotoModel>

class VideoPagingSource(
    private val loader: VideoPageLoaded,
    private val pageSize: Int
) :
    PagingSource<Int, PhotoModel>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoModel> {
        val position = params.key ?: 0
        Log.e(TAG, "load: " + position + " - " + params.loadSize)
        val photos = loader.invoke(position, params.loadSize)
        return try {
            LoadResult.Page(
                data = photos,
                prevKey = if (position == 0) null else position - 1,
                nextKey = if (photos.isEmpty()) null else position + 1
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