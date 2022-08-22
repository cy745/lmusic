package com.lalilu.lmusic.screen.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.lalilu.lmusic.utils.extension.measure

object SmartBar {
    private val lastMainBar: MutableState<(@Composable () -> Unit)?> = mutableStateOf(null)
    private val lastExtraBar: MutableState<(@Composable () -> Unit)?> = mutableStateOf(null)
    private val mainBar: MutableState<(@Composable () -> Unit)?> = mutableStateOf(null)
    private val extraBar: MutableState<(@Composable () -> Unit)?> = mutableStateOf(null)
    val contentPaddingForSmartBar = mutableStateOf(0)
    val contentPaddingForSmartBarDp = mutableStateOf(0.dp)

    @Composable
    @OptIn(ExperimentalAnimationApi::class)
    fun BoxScope.SmartBarContent(modifier: Modifier = Modifier) {
        val density = LocalDensity.current
        val hasContent = mainBar.value != null || extraBar.value != null

        Column(
            modifier = modifier
                .clickable(enabled = false) { }
                .align(Alignment.BottomCenter)
                .background(color = MaterialTheme.colors.background.copy(alpha = 0.95f))
                .fillMaxWidth()
                .measure { _, height ->
                    contentPaddingForSmartBar.value = height
                    density.run { contentPaddingForSmartBarDp.value = height.toDp() + 20.dp }
                }
        ) {
            AnimatedVisibility(visible = hasContent) {
                Spacer(modifier = Modifier.height(10.dp))
            }
            AnimatedContent(targetState = extraBar.value) {
                it?.invoke()
            }
            AnimatedVisibility(visible = extraBar.value != null) {
                Spacer(modifier = Modifier.height(10.dp))
            }
            AnimatedContent(targetState = mainBar.value) {
                it?.invoke()
            }
            AnimatedVisibility(visible = mainBar.value != null) {
                Spacer(modifier = Modifier.height(10.dp))
            }
            AnimatedVisibility(visible = hasContent) {
                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        }
    }

    @Composable
    fun RestoreOnDispose() {
        DisposableEffect(Unit) {
            onDispose { restoreLastState() }
        }
    }

    fun restoreLastState() {
        mainBar.value = lastMainBar.value
        extraBar.value = lastExtraBar.value
    }

    fun setMainBar(
        toggle: Boolean = false,
        only: Boolean = true,
        item: (@Composable () -> Unit)?
    ): SmartBar {
        if (toggle && mainBar.value === item) {
            lastMainBar.value = mainBar.value
            mainBar.value = null
            return this
        }
        if (only) {
            lastExtraBar.value = extraBar.value
            extraBar.value = null
        }
        lastMainBar.value = mainBar.value
        mainBar.value = item
        return this
    }

    fun setExtraBar(
        toggle: Boolean = false,
        only: Boolean = false,
        item: (@Composable () -> Unit)?
    ): SmartBar {
        if (toggle && extraBar.value === item) {
            lastExtraBar.value = extraBar.value
            extraBar.value = null
            return this
        }
        if (only) {
            lastMainBar.value = mainBar.value
            mainBar.value = null
        }
        lastExtraBar.value = extraBar.value
        extraBar.value = item
        return this
    }
}