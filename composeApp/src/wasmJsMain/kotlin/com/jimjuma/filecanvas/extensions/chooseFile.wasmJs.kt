package com.jimjuma.filecanvas.extensions

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