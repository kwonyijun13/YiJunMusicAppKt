package com.kwonyijun.yijunmusicappkt

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog

// used to inflate item layout and binding data to each item
// private val onItemClickListener: (Song) -> Unit is a lambda function taking a 'Song' argument
class SongsAdapter(private val songs: List<Song>,
    private val context: Context, private val mediaPlayerService: MediaPlayerService) :
    RecyclerView.Adapter<SongsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the item layout
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)

        // HERE


        return ViewHolder(view)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Define views in the item layout
        val albumImage: ImageView = itemView.findViewById(R.id.album_image)
        val titleTextView: TextView = itemView.findViewById(R.id.song_title_textView)
        val artistTextView: TextView = itemView.findViewById(R.id.artist_name_textView)
        val sortImageView: ImageView = itemView.findViewById(R.id.sort_imageview)

        init {
            // on song press
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selectedSong = songs[position]

                    mediaPlayerService?.playSong(selectedSong.filePath)
                }
            }

            // on sort press
            sortImageView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val bottomSheetDialog = BottomSheetDialog(context)
                    val view = LayoutInflater.from(context).inflate(R.layout.layout_bottom_music_popup, null)

                    // views
                    val albumImageView = view.findViewById<ImageView>(R.id.album_imageView)
                    albumImageView.setImageDrawable(albumImage.drawable)
                    val title = view.findViewById<TextView>(R.id.title_textView)
                    title.text = titleTextView.text
                    val artist = view.findViewById<TextView>(R.id.artist_name_textView)
                    artist.text = artistTextView.text

                    // ADD TO PLAYLIST
                    val addToPlaylist = view.findViewById<TextView>(R.id.add_to_playlist_textView)
                    addToPlaylist.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }

                    // EDIT SONG
                    val editSong = view.findViewById<TextView>(R.id.edit_textView)
                    editSong.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }

                    // DELETE SONG
                    val deleteSong = view.findViewById<TextView>(R.id.delete_textView)
                    deleteSong.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }

                    bottomSheetDialog.setContentView(view)
                    bottomSheetDialog.show()
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Bind data to views in the item layout
        val song = songs[position]
        // album image
        val albumId = getAlbumId(song.album, holder.itemView.context)
        val albumArtUri = getAlbumArtUri(albumId)
        Glide.with(holder.itemView.context)
            .load(albumArtUri)
            .into(holder.albumImage)

        holder.titleTextView.text = song.title
        holder.artistTextView.text = song.artist
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    private fun getAlbumId(albumName: String?, context: Context): Long {
        val projection = arrayOf(MediaStore.Audio.Albums._ID)
        val selection = "${MediaStore.Audio.Albums.ALBUM} = ?"
        val selectionArgs = arrayOf(albumName ?: "")
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )

        var albumId: Long = -1

        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(MediaStore.Audio.Albums._ID)
                albumId = it.getLong(columnIndex)
            }
        }

        return albumId
    }

    private fun getAlbumArtUri(albumId: Long): Uri {
        return ContentUris.withAppendedId(
            Uri.parse("content://media/external/audio/albumart"),
            albumId
        )
    }
}
