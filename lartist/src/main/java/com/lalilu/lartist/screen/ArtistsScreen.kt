package com.lalilu.lartist.screen

import androidx.compose.runtime.Composable
import com.lalilu.component.LLazyColumn
import com.lalilu.component.base.DynamicScreen
import com.lalilu.component.base.ScreenInfo
import com.lalilu.lartist.R
import com.lalilu.lartist.component.ArtistCard
import com.lalilu.component.R as ComponentR

data class ArtistsScreen(
    val artistsName: List<String> = emptyList()
) : DynamicScreen() {
    override fun getScreenInfo(): ScreenInfo = ScreenInfo(
        title = R.string.artist_screen_title,
        icon = ComponentR.drawable.ic_user_line
    )

    @Composable
    override fun Content() {
        ArtistsScreen()
    }
}

@Composable
private fun DynamicScreen.ArtistsScreen() {

    LLazyColumn {
        item {
            ArtistCard(index = 1, artistName = "", songCount = 20)
        }
    }
}

//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//private fun DynamicScreen.ArtistsScreen(
//    title: String = "所有艺术家",
//    sortFor: String = Sortable.SORT_FOR_ARTISTS,
//    artistIdsText: String? = null,
//    playingVM: IPlayingViewModel = singleViewModel(),
//    artistsVM: ArtistsViewModel = singleViewModel(),
//) {
//    val artists = artistsVM.artists
//    val scope = rememberCoroutineScope()
//    val listState = rememberLazyListState()
//    val showPanelState = remember { mutableStateOf(false) }
//    val currentPlaying by LPlayer.runtime.info.playingFlow.collectAsState(null)
//
//    val scrollProgress = remember(listState) {
//        derivedStateOf {
//            if (listState.layoutInfo.totalItemsCount == 0) return@derivedStateOf 0f
//            listState.firstVisibleItemIndex / listState.layoutInfo.totalItemsCount.toFloat()
//        }
//    }
//
////    val supportSortPresets = remember {
////        listOf(
////            SortPreset.SortByAddTime,
////            SortPreset.SortByTitle,
////            SortPreset.SortByDuration,
////            SortPreset.SortByItemCount
////        )
////    }
////    val supportSortRules = remember {
////        listOf(
////            SortRule.Normal,
////            SortRule.Title,
////            SortRule.ItemsCount,
////            SortRule.ItemsDuration,
////            SortRule.FileSize
////        )
////    }
////    val supportGroupRules = remember { emptyList<GroupRule>() }
////    val supportOrderRules = remember {
////        listOf(
////            OrderRule.Normal,
////            OrderRule.Reverse,
////            OrderRule.Shuffle
////        )
////    }
//
////    LaunchedEffect(artistIdsText) {
////        artistsVM.updateByIds(
////            sortFor = sortFor,
////            ids = artistIdsText.getIds(),
////            supportGroupRules = supportGroupRules,
////            supportSortRules = supportSortRules,
////            supportOrderRules = supportOrderRules
////        )
////    }
//
//    SmartFloatBtns.RegisterFloatBtns(
//        progress = scrollProgress,
//        items = listOf(
//            SmartFloatBtns.FloatBtnItem(
//                title = "排序",
//                icon = ComponentR.drawable.ic_sort_desc,
//                callback = { showAll ->
//                    showPanelState.value = true
//                    showAll.value = false
//                }
//            ),
//            SmartFloatBtns.FloatBtnItem(
//                icon = ComponentR.drawable.ic_focus_3_line,
//                title = "定位当前播放歌曲",
//                callback = {
//                    if (currentPlaying != null) {
//                        scope.launch {
//                            var targetIndex = -1
//                            val startIndex = listState.firstVisibleItemIndex
//
//                            // 从当前可见的元素Index开始往后找
//                            for (i in startIndex until artists.value.size) {
//                                if (i == startIndex) continue
//
//                                if (currentPlaying!!.subTitle.contains(artists.value[i].name)) {
//                                    targetIndex = i
//                                    break
//                                }
//                            }
//
//                            // 若无法往后找到，则从头开始找
//                            if (targetIndex == -1) {
//                                targetIndex = artists.value.indexOfFirst { artist ->
//                                    currentPlaying!!.subTitle.contains(artist.name)
//                                }
//                            }
//
//                            // 若找到则跳转
//                            if (targetIndex != -1) {
//                                listState.scrollToItem(targetIndex)
//                            }
//                        }
//                    }
//                }
//            ),
//            SmartFloatBtns.FloatBtnItem(
//                icon = ComponentR.drawable.ic_arrow_up_s_line,
//                title = "回到顶部",
//                callback = { scope.launch { listState.scrollToItem(0) } }
//            ),
//            SmartFloatBtns.FloatBtnItem(
//                icon = ComponentR.drawable.ic_arrow_down_s_line,
//                title = "滚动到底部",
//                callback = { scope.launch { listState.scrollToItem(listState.layoutInfo.totalItemsCount) } }
//            )
//        )
//    )
//
//    SortPanelWrapper(
//        sortFor = sortFor,
//        showPanelState = showPanelState,
//        supportListAction = { emptyList() },
//        sp = koinInject<SettingsSp>()
//    ) { sortRuleStr ->
//        SmartContainer.LazyColumn(state = listState) {
//            item(key = "Header") {
//                NavigatorHeader(
//                    title = title,
//                    subTitle = "共 ${artists.value.size} 条记录"
//                )
//            }
//
//            itemsIndexed(
//                items = artists.value,
//                key = { _, item -> item.id },
//                contentType = { _, _ -> LArtist::class }
//            ) { index, item ->
//                ArtistCard(
//                    modifier = Modifier.animateItemPlacement(),
//                    index = index,
//                    artistName = item.name,
//                    songCount = item.requireItemsCount(),
//                    isPlaying = {
//                        playingVM.isItemPlaying { playing ->
//                            playing.let { it as? LSong }
//                                ?.let { song -> song.artists.any { it.name == item.name } }
//                                ?: false
//                        }
//                    },
//                    onClick = {
////                        navigator.navigate(ArtistDetailScreenDestination(item.name))
//                    }
//                )
//            }
//        }
//    }
//}