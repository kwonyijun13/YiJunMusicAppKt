package com.kwonyijun.yijunmusicappkt

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import java.io.IOException

class MediaPlayerService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private var mediaPlayer: MediaPlayer? = null
    var isMediaPlaying: Boolean = false

    // BINDER
    inner class LocalBinder : Binder() {
        fun getService(): MediaPlayerService = this@MediaPlayerService
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setOnPreparedListener(this)
        mediaPlayer?.setOnCompletionListener(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Handle incoming requests to start the service
        if (intent?.action == ACTION_PLAY) {
            val songUri = intent.getStringExtra(EXTRA_SONG_URI)
            playSong(songUri)
        }
        return START_STICKY
    }

    override fun onPrepared(mp: MediaPlayer?) {
        // Media player is prepared, start playing
        mediaPlayer?.start()
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        Log.d("SEOYEON", "onDestroy() called")
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return LocalBinder()
    }

    companion object {
        const val ACTION_PLAY = "com.example.ACTION_PLAY"
        const val EXTRA_SONG_URI = "com.example.EXTRA_SONG_URI"
    }

    fun playSong(songUri: String?) {
        mediaPlayer?.reset()
        try {
            mediaPlayer?.setDataSource(songUri)
            mediaPlayer?.prepareAsync() // prepare asynchronously to avoid blocking the main thread
            isMediaPlaying = true
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun pauseSong() {
        if (isMediaPlaying) {
            mediaPlayer?.pause()
            isMediaPlaying = false
        }
    }

    fun isPlaying(): Boolean {
        return if (isMediaPlaying) {
            true
        } else {
            false
        }
    }

    override fun onCompletion(p0: MediaPlayer?) {
        TODO("Not yet implemented")
    }
}
