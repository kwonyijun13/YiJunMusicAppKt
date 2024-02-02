package com.kwonyijun.yijunmusicappkt

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    // CURRENTLY PLAYING BOTTOM LAYOUT VIEWS
    lateinit var currentlyPlayingSongBottomView: LinearLayout
    lateinit var bottomAlbumImage: ImageView
    lateinit var bottomTitle: TextView
    lateinit var bottomArtist: TextView
    lateinit var bottomPreviousButton: ImageButton
    lateinit var bottomNextButton: ImageButton
    lateinit var bottomPlaybackButton: ImageButton

    lateinit var mediaPlayerService: MediaPlayerService
    private var isServiceBound = false

    // Bind with MediaPlayerService using ServiceConnection
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MediaPlayerService.LocalBinder
            mediaPlayerService = binder.getService()
            isServiceBound = true
            updateUI()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
        }
    }

    private fun bindMediaPlayerService() {
        val intent = Intent(this, MediaPlayerService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
        }
    }
    // END OF BINDING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindMediaPlayerService()

        // VIEWPAGER & TABLAYOUT
        val viewPager: ViewPager2 = findViewById(R.id.viewPager2)
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)

        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        /* tabLayout's titles
         TabLayoutMediator is used to sync a 'TabLayout' & 'ViewPager2'
         "tab, position ->" This is a lambda function that gets executed for each tab during the setup process.
         It's called for each tab, and you can use it to customize the appearance and behavior of each tab.
        */
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Songs"
                1 -> tab.text = "Playlists"
                else -> throw IllegalArgumentException("Invalid position")
            }
        }.attach()

        // CURRENTLYPLAYINGSONG BOTTOM LAYOUT
        currentlyPlayingSongBottomView = findViewById(R.id.custom_bottom_view)
        bottomAlbumImage = findViewById(R.id.album_imageView)
        bottomTitle = findViewById(R.id.song_title_textView)
        bottomArtist = findViewById(R.id.artist_name_textView)
        bottomPreviousButton = findViewById(R.id.previous_imageButton)
        bottomNextButton = findViewById(R.id.next_imageButton)
        bottomPlaybackButton = findViewById(R.id.playback_imageButton)
    }

    fun updateUI() {
        if (mediaPlayerService != null) {
            if (mediaPlayerService.isPlaying() == true) {
                currentlyPlayingSongBottomView.visibility = View.VISIBLE

            }
        }
    }
}

// ? is the safe call operator to avoid a NullPointerException