package com.jimjuma.filecanvas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.skiko.Cursor

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "FileCanvas",
    ) {

        App()

    }
}