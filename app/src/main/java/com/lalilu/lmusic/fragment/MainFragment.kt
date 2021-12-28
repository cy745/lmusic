package com.lalilu.lmusic.fragment

import android.media.MediaMetadata
import android.view.View
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.lalilu.R
import com.lalilu.databinding.FragmentMainBinding
import com.lalilu.lmusic.adapter.LMusicFragmentStateAdapter
import com.lalilu.lmusic.base.DataBindingConfig
import com.lalilu.lmusic.base.DataBindingFragment
import com.lalilu.lmusic.event.SharedViewModel
import com.lalilu.lmusic.service.LMusicPlayerModule
import com.lalilu.lmusic.service.MSongService
import com.lalilu.lmusic.ui.seekbar.OnSeekBarChangeListenerAdapter
import com.lalilu.lmusic.utils.HapticUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainFragment : DataBindingFragment() {

    @Inject
    lateinit var mEvent: SharedViewModel

    @Inject
    lateinit var playerModule: LMusicPlayerModule

    private var mPagerAdapter: LMusicFragmentStateAdapter? = null

    override fun getDataBindingConfig(): DataBindingConfig {
        mPagerAdapter = LMusicFragmentStateAdapter(mActivity!!)
            .addFragment(PlayingFragment())
            .addFragment(NavigatorFragment())

        return DataBindingConfig(R.layout.fragment_main, BR.ev, mEvent)
            .addParam(BR.pagerAdapter, mPagerAdapter)
    }

    override fun onViewCreated() {
        val binding = (mBinding as FragmentMainBinding)
        val seekBar = (mBinding as FragmentMainBinding).maSeekBar

        val child = binding.fmViewpager?.getChildAt(0)
        if (child is RecyclerView) child.overScrollMode = View.OVER_SCROLL_NEVER

        // 从 metadata 中获取歌曲的总时长传递给 SeekBar
        playerModule.metadata.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            val sum = it.getLong(MediaMetadata.METADATA_KEY_DURATION)
            seekBar.setSumDuration(sum)
        }

        playerModule.songPosition.observe(viewLifecycleOwner) {
            seekBar.updatePosition(it)
        }

        // 为 SeekBar 添加监听器
        seekBar.onSeekBarChangeListener = object : OnSeekBarChangeListenerAdapter() {
            override fun onStopTrackingTouch(position: Long) {
                playerModule.mediaController?.transportControls?.seekTo(position)
            }

            override fun onClick() {
                playerModule.mediaController?.transportControls?.sendCustomAction(
                    MSongService.ACTION_PLAY_PAUSE, null
                )
                HapticUtils.haptic(seekBar.rootView)
            }

            override fun onProgressToMax() {
                HapticUtils.haptic(seekBar.rootView)
            }

            override fun onProgressToMin() {
                HapticUtils.haptic(seekBar.rootView)
            }

            override fun onProgressToMiddle() {
                HapticUtils.haptic(seekBar.rootView)
            }
        }
    }
}