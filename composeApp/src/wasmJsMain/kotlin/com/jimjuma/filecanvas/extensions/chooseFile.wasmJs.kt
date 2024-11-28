package com.jimjuma.filecanvas.extensions

import androidx.compose.ui.graphics.ImageBitmap

actual fun chooseFile(): CommonFile? {
    TODO("Not yet implemented")
}

actual fun extractTextFromPdf(file: CommonFile): String {
    TODO("Not yet implemented")
}

actual class CommonFile {
    actual val name: String
        get() = TODO("Not yet implemented")

    actual fun extension(): String {
        TODO("Not yet implemented")
    }
}

actual fun renderPdfPages(file: CommonFile): List<ImageBitmap> {
    TODO("Not yet implemented")
}

actual fun loadImage(file: CommonFile): ImageBitmap {
    TODO("Not yet implemented")
}

actual fun createCommonFile(path: String): CommonFile {
    TODO("Not yet implemented")
}

actual fun openFile(filePath: String) {
}