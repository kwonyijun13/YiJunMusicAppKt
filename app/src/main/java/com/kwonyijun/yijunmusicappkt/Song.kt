package com.kwonyijun.yijunmusicappkt

// Model Class: holds info about each song
data class Song(
    val title: String,
    val artist: String,
    val album: String,
    val filePath: String,
    val dateAdded: Long
)
