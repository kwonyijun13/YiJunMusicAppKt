package com.kwonyijun.yijunmusicappkt

// to notify the activity when the playback state changes
interface MediaPlayerCallback {
    fun onPlaybackStateChanged(isMediaPlaying: Boolean)
}
