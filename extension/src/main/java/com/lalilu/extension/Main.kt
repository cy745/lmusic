package com.lalilu.extension

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.lalilu.extension_core.Ext
import com.lalilu.extension_core.Extension
import com.lalilu.extension_core.Provider

@Ext
class Main : Extension {

    override fun getContentMap(): Map<String, @Composable () -> Unit> {
        return mapOf(
            "home" to this.bannerContent,
            "main" to { MainScreen() },
            "banner" to this.bannerContent,
        )
    }

    override fun getPlayableProvider(): Provider? {
        return VitsProvider
    }

    private val bannerContent: @Composable () -> Unit = {
        val imageApi =
            remember { "https://api.sretna.cn/layout/pc.php?seed=${System.currentTimeMillis() / 30000}" }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
                contentScale = ContentScale.Crop,
                model = imageApi,
                contentDescription = ""
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {
                Text(text = stringResource(id = R.string.plugin_name) + " " + BuildConfig.VERSION_NAME)
            }
        }
    }
}