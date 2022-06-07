package com.lalilu.lmusic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import com.lalilu.lmusic.datasource.MMediaSource
import com.lalilu.lmusic.manager.LyricManager
import com.lalilu.lmusic.service.MSongBrowser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class MainViewModel @Inject constructor(
    val mediaBrowser: MSongBrowser,
    val mediaSource: MMediaSource
) : ViewModel(), CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO
    val songLyric = LyricManager.currentLyricLiveData

    fun playSongWithPlaylist(items: List<MediaItem>, index: Int) =
        launch(Dispatchers.Main) {
            mediaBrowser.browser?.apply {
                clearMediaItems()
                setMediaItems(items)
                seekToDefaultPosition(index)
                prepare()
                play()
            }
        }
}