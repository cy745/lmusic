package com.lalilu.lmusic.domain.entity

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "m_playlist")
data class MPlaylist(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "playlist_id")
    val playlistId: Long? = null,

    @ColumnInfo(name = "playlist_title")
    val playlistTitle: String = "New Playlist.",

    @ColumnInfo(name = "playlist_cover_uri")
    var playlistCoverUri: Uri = Uri.EMPTY,

    @ColumnInfo(name = "playlist_info")
    val playlistInfo: String = "empty.",

    @ColumnInfo(name = "playlist_create_time")
    val playlistCreateTime: Date = Date()
)