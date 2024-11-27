package com.jimjuma.filecanvas.extensions

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.semantics.Role.Companion.Image

// commonMain
expect fun chooseFile(): CommonFile?
expect fun extractTextFromPdf(file: CommonFile): String

expect class CommonFile {
    val name: String
    fun extension(): String
}


