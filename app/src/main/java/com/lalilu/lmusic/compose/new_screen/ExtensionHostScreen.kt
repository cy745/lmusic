package com.lalilu.lmusic.compose.new_screen

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.lalilu.extension_core.ExtensionLoadResult
import com.lalilu.extension_core.ExtensionManager
import com.ramcosta.composedestinations.annotation.Destination


@Destination
@Composable
fun ExtensionHostScreen(
    className: String
) {
    val extensionResult by ExtensionManager
        .requireExtensionByClassName(className)
        .collectAsState(null)

    extensionResult?.Place(
        contentKey = "main",
        errorPlaceHolder = {
            val message = when (extensionResult) {
                is ExtensionLoadResult.Error -> (extensionResult as ExtensionLoadResult.Error).message
                is ExtensionLoadResult.OutOfDated -> "Extension $className is out of dated."
                else -> "Extension $className is not found."
            }
            Text(text = message)
        },
    )
}