package com.kwonyijun.yijunmusicappkt

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SongsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    lateinit var mediaPlayerService: MediaPlayerService

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MediaPlayerService.LocalBinder
            mediaPlayerService = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            // Handle disconnection if needed
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // container: ViewGroup? = The parent view that the fragment's UI should be attached to, or null if the fragment has no UI.
    // The fragment's view hierarchy will be inflated and added to this parent view.
    // savedInstance: Bundle? = This parameter allows the fragment to reconstruct its view if it is being re-created from a previous saved state.
    // It contains data that was saved in the onSaveInstanceState method.
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_songs, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)

        // PERMISSIONS
        val songsList: List<Song>
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            songsList = fetchSongs()
        } else {
            // Request permission if not granted
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            songsList = emptyList()
        }

        val songsAdapter = SongsAdapter(songsList, requireContext(), mediaPlayerService)

        recyclerView.adapter = songsAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        return view
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    val songsList = fetchSongs()
                    val mediaPlayerServiceIntent = Intent(requireContext(), MediaPlayerService::class.java)
                    val songsAdapter = SongsAdapter(songsList, requireContext(), mediaPlayerService)
                    recyclerView.adapter = songsAdapter
                } else {
                    // Permission denied
                    Toast.makeText(context, "Please allow permissions in settings.", Toast.LENGTH_LONG)
                }
            }
        }
    }

    // an object that is declared inside a class and is associated with the class itself rather than with instances of the class
    // similar to a static method or a static block in Java
    companion object {
        @JvmStatic // can be called from Java code as if it were a static method
        fun newInstance(param1: String, param2: String) =
            SongsFragment().apply {}
    }

    private fun fetchSongs(): List<Song> {
        val songsList = mutableListOf<Song>()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA,  // File path
            MediaStore.Audio.Media.DATE_ADDED
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
        val cursor = requireActivity().contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )

        if (cursor != null) {
            while (cursor.moveToNext()) {
                val title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                val artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                val album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
                val filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                val dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED))

                // Create a Song object and add it to the list
                val song = Song(title, artist, album, filePath, dateAdded)
                songsList.add(song)

                // Example: Log the information for demonstration purposes
                Log.d("SongInfo", "Title: $title, Artist: $artist, Album: $album, FilePath: $filePath, DateAdded: $dateAdded")
            }
            cursor.close()
        }
        return songsList
    }
}