package com.example.androidpaging3.models

import java.io.Serializable

data class PhotoModel(
    var id: Long,
    var namePhoto: String,
    var pathFile: String,
    var sizePhoto: String
) : Serializable