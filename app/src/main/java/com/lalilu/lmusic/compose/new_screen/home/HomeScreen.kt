package com.lalilu.lmusic.compose.new_screen.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.lalilu.R
import com.lalilu.component.LLazyColumn
import com.lalilu.component.base.TabScreen
import com.lalilu.component.base.screen.ScreenInfo
import com.lalilu.component.extension.singleViewModel
import com.lalilu.lmusic.extension.EntryPanel
import com.lalilu.lmusic.extension.dailyRecommend
import com.lalilu.lmusic.extension.historyPanel
import com.lalilu.lmusic.extension.latestPanel
import com.lalilu.lmusic.viewmodel.HistoryViewModel
import com.lalilu.lmusic.viewmodel.LibraryViewModel
import com.lalilu.lmusic.viewmodel.PlayingViewModel
import com.zhangke.krouter.annotation.Destination

@Destination("/pages/home")
object HomeScreen : TabScreen, Screen {
    private fun readResolve(): Any = HomeScreen

    @Composable
    override fun provideScreenInfo(): ScreenInfo = remember {
        ScreenInfo(
            title = R.string.screen_title_home,
            icon = R.drawable.ic_loader_line
        )
    }

    @Composable
    override fun Content() {
        val libraryVM: LibraryViewModel = singleViewModel()
        val historyVM: HistoryViewModel = singleViewModel()
        val playingVM: PlayingViewModel = singleViewModel()

        LaunchedEffect(Unit) {
            libraryVM.checkOrUpdateToday()
        }

        LLazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Spacer(
                    modifier = Modifier
                        .windowInsetsTopHeight(WindowInsets.statusBars)
                )
            }

            dailyRecommend(libraryVM = libraryVM)

            latestPanel(
                libraryVM = libraryVM,
                playingVM = playingVM
            )

            historyPanel(
                historyVM = historyVM,
                playingVM = playingVM
            )

            item {
                EntryPanel()
            }
        }
    }
}