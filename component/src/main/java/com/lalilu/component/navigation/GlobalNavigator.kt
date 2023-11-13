package com.lalilu.component.navigation

import com.lalilu.component.base.BottomSheetNavigator

interface GlobalNavigator {

    /**
     * 跳转至某元素的详情页
     */
    fun goToDetailOf(
        mediaId: String,
        navigator: BottomSheetNavigator? = null
    )

    /**
     * 展示一些歌曲
     */
    fun showSongs(
        mediaIds: List<String>,
        title: String? = null,
        navigator: BottomSheetNavigator? = null
    )
}