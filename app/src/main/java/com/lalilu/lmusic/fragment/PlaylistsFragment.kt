package com.lalilu.lmusic.fragment

import androidx.databinding.library.baseAdapters.BR
import com.lalilu.R
import com.lalilu.lmusic.adapter.PlaylistsAdapter
import com.lalilu.lmusic.base.DataBindingConfig
import com.lalilu.lmusic.base.DataBindingFragment
import com.lalilu.lmusic.domain.entity.MPlaylist
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class PlaylistsFragment : DataBindingFragment() {

    @Inject
    lateinit var mAdapter: PlaylistsAdapter

    override fun getDataBindingConfig(): DataBindingConfig {
        mAdapter.setDiffNewData(
            arrayListOf(
                MPlaylist(0, "全部歌曲")
            )
        )
        return DataBindingConfig(R.layout.fragment_list_playlists)
            .addParam(BR.playlistAdapter, mAdapter)
    }
}