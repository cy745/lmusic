package com.lalilu.lmusic.screen.library

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.unit.dp
import com.blankj.utilcode.util.TimeUtils
import com.lalilu.lmedia.entity.LSong
import com.lalilu.lmusic.screen.LibraryNavigateBar
import com.lalilu.lmusic.screen.ScreenActions
import com.lalilu.lmusic.screen.component.SmartBar
import com.lalilu.lmusic.screen.component.SmartContainer
import com.lalilu.lmusic.screen.component.button.TextWithIconButton
import com.lalilu.lmusic.screen.component.card.SongCard
import com.lalilu.lmusic.service.runtime.LMusicRuntime
import com.lalilu.lmusic.utils.extension.LocalWindowSize
import com.lalilu.lmusic.utils.extension.average
import com.lalilu.lmusic.viewmodel.LibraryViewModel
import com.lalilu.lmusic.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun SongsScreen(
    mainViewModel: MainViewModel,
    libraryViewModel: LibraryViewModel,
) {
    val songs by libraryViewModel.songs.observeAsState(emptyList())
    val currentPlaying by LMusicRuntime.playingLiveData.observeAsState()

    val windowSize = LocalWindowSize.current
    val gridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()

    val selectedItem = mainViewModel.selectedItem
    var isSelecting by mainViewModel.isSelecting

    LaunchedEffect(isSelecting) {
        if (isSelecting) {
            SmartBar.setMainBar {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    TextWithIconButton(
                        text = "取消",
                        color = Color(0xFF006E7C),
                        onClick = {
                            isSelecting = false
                            selectedItem.clear()
                        }
                    )
                    Text(text = "已选择: ${selectedItem.size}")
                }
            }
        } else {
            SmartBar.setMainBar(item = LibraryNavigateBar)
        }
    }

    val navToSongAction = ScreenActions.navToSong(hapticType = HapticFeedbackType.LongPress)

    val onSongSelected: (LSong) -> Unit = {
        isSelecting = true
        if (selectedItem.contains(it)) {
            selectedItem.remove(it)
        } else {
            selectedItem.add(it)
        }
    }

    val onSongPlay: (LSong) -> Unit = { song ->
        if (isSelecting) {
            onSongSelected(song)
        } else {
            mainViewModel.playSongWithPlaylist(songs, song)
        }
    }

    val getIsSelected = { it: LSong -> selectedItem.contains(it) }

    val scrollToCurrentPlaying = remember {
        {
            scope.launch {
                if (currentPlaying == null) return@launch

                val index = songs.indexOfFirst { it.id == currentPlaying!!.id }
                if (index >= 0) {

                    // 获取当前可见元素的平均高度
                    fun getHeightAverage() = gridState.layoutInfo.visibleItemsInfo
                        .average { it.size.height }

                    // 获取精确的位移量（只能对可见元素获取）
                    fun getTargetOffset() = gridState.layoutInfo.visibleItemsInfo
                        .find { it.index == index }
                        ?.offset?.y

                    // 获取粗略的位移量（通过对可见元素的高度求平均再通过index的差，计算出粗略值）
                    fun getRoughTargetOffset() =
                        getHeightAverage() * (index - gridState.firstVisibleItemIndex - 1)

                    // 若获取不到精确的位移量，则计算粗略位移量并开始scroll
                    if (getTargetOffset() == null) {
                        gridState.animateScrollBy(
                            getRoughTargetOffset(),
                            SpringSpec(stiffness = Spring.StiffnessVeryLow)
                        )
                    }

                    // 若可以获取到精确的位移量，则直接滚动到目标歌曲位置
                    getTargetOffset()?.let {
                        gridState.animateScrollBy(
                            it.toFloat(),
                            SpringSpec(stiffness = Spring.StiffnessVeryLow)
                        )
                    }
                }
            }
        }
    }

    SmartContainer.LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Fixed(if (windowSize.widthSizeClass == WindowWidthSizeClass.Expanded) 2 else 1),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val now = System.currentTimeMillis()
        songs.groupBy { song ->
            song.dateAdded?.let { it - (it % 60) }?.times(1000L) ?: 0L
        }.forEach { (dateAdded, list) ->
            item(
                key = dateAdded,
                contentType = LSong::dateAdded,
                span = { GridItemSpan(maxLineSpan) }
            ) {
                Text(
                    modifier = Modifier.padding(
                        top = 20.dp,
                        bottom = 10.dp,
                        start = 20.dp,
                        end = 20.dp
                    ),
                    style = MaterialTheme.typography.h6,
                    text = when {
                        now - dateAdded < 300000 -> "刚刚"
                        now - dateAdded < 3600000 -> "${(now - dateAdded) / 60000}分钟前"
                        now - dateAdded < 86400000 -> "${(now - dateAdded) / 3600000}小时前"
                        else -> TimeUtils.millis2String(dateAdded, "M月d日 HH:mm")
                    }
                )
            }
            itemsIndexed(
                items = list,
                key = { _, item -> item.id },
                contentType = { _, _ -> LSong::class }
            ) { index, item ->
                SongCard(
                    index = index,
                    getSong = { item },
                    loadDelay = { 200L },
                    getIsSelected = getIsSelected,
                    onItemClick = onSongPlay,
                    onItemLongClick = { navToSongAction(it.id) },
                    onItemImageClick = onSongPlay,
                    onItemImageLongClick = onSongSelected
                )
            }
        }
    }
}