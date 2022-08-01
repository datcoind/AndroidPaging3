package com.example.androidpaging3.utils

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.example.androidpaging3.models.PhotoModel
import java.io.File

private const val TAG = "FileUtils"


class FileUtils {

    companion object {
        /**
         *  Column DATA is deprecated on android 29
         *  => get uri image with Uri.withAppendedPath(uriExternal, "" + imageId)
         *  https://stackoverflow.com/a/58203497/10727195
         * */
        fun getAllPhotoFromDevice(context: Context, offset: Int = 0, limitPhotoLoadMore: Int = 20): ArrayList<PhotoModel> {
            val arrPhoto: ArrayList<PhotoModel> = ArrayList()
            val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE
            )

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                context.contentResolver.query(
                    uri,
                    projection,
                    Bundle().apply {
                        putInt(ContentResolver.QUERY_ARG_LIMIT, limitPhotoLoadMore)
                        putInt(
                            ContentResolver.QUERY_ARG_OFFSET,
                            offset * limitPhotoLoadMore
                        )

                        // sort
                        putStringArray(
                            ContentResolver.QUERY_ARG_SORT_COLUMNS,
                            arrayOf(MediaStore.Files.FileColumns.DATE_MODIFIED)
                        )

                        putInt(
                            ContentResolver.QUERY_ARG_SORT_DIRECTION,
                            ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
                        )
                        // selection

                    }, null
                )
            } else {
                val sortOrder =
                    MediaStore.MediaColumns.DATE_MODIFIED + " DESC LIMIT $limitPhotoLoadMore OFFSET ${offset * limitPhotoLoadMore}"
                Log.e(TAG, "getAllPhotoFromDevice: " + limitPhotoLoadMore + " - " + (offset * limitPhotoLoadMore) )
                context.contentResolver.query(uri, projection, null, null, sortOrder)

            }?.use { cursor: Cursor ->
                val count = cursor.count
                val arrPath = arrayOfNulls<String>(count)
                for (i in 0 until count) {
                    cursor.moveToPosition(i)
                    val id: Long =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                    val dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                    val displayNameColumn =
                        cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
                    val name = cursor.getString(displayNameColumn)
                    val sizeColumn = cursor.getColumnIndex(MediaStore.Images.Media.SIZE)
                    val size = cursor.getDouble(sizeColumn)

                    arrPath[i] = cursor.getString(dataColumnIndex)
                    //Store the path of the image
                    val photoModel = PhotoModel(id, name, arrPath[i].toString(), size.toString())

                    val file = File(arrPath[i].toString())
                    if (file.exists()) {
                        arrPhoto.add(photoModel)
                    }
                }
                cursor.close()
            }
            return arrPhoto
        }
    }

}