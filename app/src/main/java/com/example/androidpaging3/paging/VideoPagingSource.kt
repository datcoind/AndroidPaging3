package com.example.androidpaging3.paging

import android.content.Context
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.androidpaging3.models.PhotoModel
import com.example.androidpaging3.utils.FileUtils

private const val TAG = "VideoPagingSource"
private const val NOTE_STARTING_PAGE_INDEX = 0
private const val NOTE_ENDING_PAGE_INDEX = 20

class VideoPagingSource(private val context: Context) :
    PagingSource<Int, PhotoModel>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoModel> {
        val position = params.key ?: NOTE_STARTING_PAGE_INDEX
        Log.e(TAG, "load: " + position + " - " + params.loadSize)
        val list = FileUtils.getAllPhotoFromDevice(context, position)
        try {
            return LoadResult.Page(
                data = list,
                prevKey = if (position == NOTE_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (list.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PhotoModel>): Int? {
        return state.anchorPosition
    }
}