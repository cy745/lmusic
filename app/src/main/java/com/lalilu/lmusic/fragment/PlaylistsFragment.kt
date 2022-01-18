package com.lalilu.lmusic.fragment

import androidx.databinding.library.baseAdapters.BR
import androidx.navigation.fragment.findNavController
import com.lalilu.R
import com.lalilu.lmusic.adapter.MSongPlaylistsAdapter
import com.lalilu.lmusic.base.DataBindingConfig
import com.lalilu.lmusic.base.DataBindingFragment
import com.lalilu.lmusic.domain.entity.MPlaylist
import com.lalilu.lmusic.event.DataModule
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class PlaylistsFragment : DataBindingFragment() {

    @Inject
    lateinit var mAdapter: MSongPlaylistsAdapter

    @Inject
    lateinit var dataModule: DataModule

    override fun getDataBindingConfig(): DataBindingConfig {
        mAdapter.setOnItemClickListener { adapter, _, position ->
            val playlist = adapter.data[position] as MPlaylist

            findNavController().navigate(
                PlaylistsFragmentDirections.playlistDetail(
                    playlistId = playlist.playlistId,
                    title = playlist.playlistTitle
                )
            )
        }

        return DataBindingConfig(R.layout.fragment_playlist)
            .addParam(BR.playlistAdapter, mAdapter)
    }

    override fun onViewCreated() {
        dataModule.allPlaylist.observe(viewLifecycleOwner) {
            mAdapter.setDiffNewData(it?.toMutableList())
        }
    }
}