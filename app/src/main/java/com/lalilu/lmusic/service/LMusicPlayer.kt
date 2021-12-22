package com.lalilu.lmusic.service

import android.media.MediaPlayer
import com.lalilu.lmusic.manager.LMusicVolumeManager
import javax.inject.Inject

class LMusicPlayer @Inject constructor() : MediaPlayer() {
    val volumeManager = LMusicVolumeManager(this)
}