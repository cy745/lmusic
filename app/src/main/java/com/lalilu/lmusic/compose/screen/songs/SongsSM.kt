package com.lalilu.lmusic.compose.screen.songs

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.lalilu.common.base.BaseSp
import com.lalilu.common.base.Playable
import com.lalilu.common.ext.requestFor
import com.lalilu.component.extension.ItemSelector
import com.lalilu.component.extension.toState
import com.lalilu.component.viewmodel.SongsSp
import com.lalilu.component.viewmodel.findInstance
import com.lalilu.lmedia.LMedia
import com.lalilu.lmedia.entity.BaseMatchable
import com.lalilu.lmedia.entity.LSong
import com.lalilu.lmedia.extension.ListAction
import com.lalilu.lmedia.extension.SortDynamicAction
import com.lalilu.lmedia.extension.SortStaticAction
import com.lalilu.lmedia.extension.Sortable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject

internal sealed interface SongsScreenAction {
    data object ToggleSortPanel : SongsScreenAction
    data object LocaleToPlayingItem : SongsScreenAction
    data class SearchFor(val keyword: String) : SongsScreenAction
}

internal sealed interface SongsScreenEvent {
    data class ScrollToItem(val key: String) : SongsScreenEvent
}

internal class SongsSM(
    private val mediaIds: List<String>,
) : ScreenModel {
    // 持久化元素的状态
    val showSortPanel = mutableStateOf(false)
    val supportSortActions = setOf<ListAction?>(
        SortStaticAction.Normal,
        SortStaticAction.Title,
        SortStaticAction.AddTime,
        SortStaticAction.Shuffle,
        SortStaticAction.Duration,
        requestFor(named("sort_rule_play_count")),
        requestFor(named("sort_rule_last_play_time")),
    ).filterNotNull()
        .toSet()

    // 事件流
    private val eventFlow = MutableSharedFlow<SongsScreenEvent>()
    fun event(): SharedFlow<SongsScreenEvent> = eventFlow
    fun action(action: SongsScreenAction) = screenModelScope.launch {
        when (action) {
            SongsScreenAction.LocaleToPlayingItem -> {
                eventFlow.emit(SongsScreenEvent.ScrollToItem(""))
            }

            SongsScreenAction.ToggleSortPanel -> {
                showSortPanel.value = !showSortPanel.value
            }

            is SongsScreenAction.SearchFor -> {
                searcher.search(action.keyword)
            }

            else -> {}
        }
    }

    // 数据流
    private fun flow(): Flow<List<LSong>> {
        return if (mediaIds.isEmpty()) LMedia.getFlow<LSong>()
        else LMedia.flowMapBy<LSong>(mediaIds)
    }

    val searcher = ItemSearcher(flow())
    val sorter = ItemSorter(searcher.output, supportSortActions)
    val songs = sorter.output.toState(emptyMap(), screenModelScope)
    val selector = ItemSelector<Playable>()
}

internal class ItemSearcher<T : BaseMatchable>(
    sourceFlow: Flow<List<T>>
) {
    private val keywordStr = MutableStateFlow("")
    private val keywordFlow = keywordStr.map {
        when {
            it.isBlank() -> emptyList()
            it.contains(' ') -> it.split(' ')
            else -> listOf(it)
        }
    }

    val output: Flow<List<T>> = sourceFlow.combine(keywordFlow) { source, keywords ->
        source.filter { item -> keywords.all { item.matchStr.contains(it) } }
    }

    fun search(keyword: String) {
        keywordStr.value = keyword
    }

    fun clear() {
        keywordStr.value = ""
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
internal class ItemSorter<T : Sortable>(
    sourceFlow: Flow<List<T>>,
    private val supportSortActions: Set<ListAction>,
) {
    private val baseSp: BaseSp by inject(SongsSp::class.java)
    private val sortActionKey = baseSp.obtain("SONGS_SORT_RULE_KEY", "")

    private val sortActionFlow = sortActionKey
        .flow(true)
        .mapLatest { key ->
            supportSortActions.findInstance<ListAction> { it::class.java.name == key }
                ?: SortStaticAction.Normal
        }

    val output = sortActionFlow.flatMapLatest { action ->
        when (action) {
            is SortStaticAction -> sourceFlow.mapLatest { action.doSort(it, false) }
            is SortDynamicAction -> action.doSort(sourceFlow, false)
            else -> flowOf(emptyMap())
        }
    }

    fun selectSortAction(action: ListAction) {
        sortActionKey.value = action::class.java.name
    }

    fun isSortActionSelected(action: ListAction): Boolean {
        // 初次启动时若key值为空，则默认为Normal
        if (sortActionKey.value.isBlank()) {
            return action::class.java == SortStaticAction.Normal::class.java
        }

        return sortActionKey.value == action::class.java.name
    }
}