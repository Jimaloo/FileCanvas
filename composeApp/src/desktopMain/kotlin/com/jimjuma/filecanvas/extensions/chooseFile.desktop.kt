package com.jimjuma.filecanvas.extensions

// jvmMain
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import java.io.File as JFile
import java.awt.FileDialog
import java.awt.Frame
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import org.jetbrains.skia.Image
import java.awt.Desktop
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

actual fun chooseFile(): CommonFile? {
    val dialog = FileDialog(Frame(), "Select a File", FileDialog.LOAD).apply {
        isVisible = true
    }
    return dialog.file?.let { CommonFile(JFile(dialog.directory, dialog.file)) }
}

actual fun extractTextFromPdf(file: CommonFile): String {
//    val jFile = file.getJFile()
//    PDDocument.load(jFile).use { document ->
//        val pdfTextStripper = org.apache.pdfbox.text.PDFTextStripper()
//        return pdfTextStripper.getText(document)
//    }
    return ""
}

actual fun renderPdfPages(file: CommonFile): List<ImageBitmap> {
    val pdfFile = File(file.name)
    val document = PDDocument.load(pdfFile)
    val renderer = PDFRenderer(document)
    val pages = mutableListOf<ImageBitmap>()

    for (pageIndex in 0 until document.numberOfPages) {
        val bufferedImage = renderer.renderImageWithDPI(pageIndex, 150f) // 150 DPI
        pages.add(bufferedImage.toComposeImageBitmap())
    }

    document.close()
    return pages
}

actual fun loadImage(file: CommonFile): ImageBitmap {
    val bytes = File(file.name).readBytes()
    val skiaImage = Image.makeFromEncoded(bytes)
    return skiaImage.toComposeImageBitmap()
}

actual class CommonFile internal constructor(private val file: File){
    actual val name: String
        get() = file.absolutePath

    actual fun extension(): String {
       return file.extension
    }
}

actual fun createCommonFile(path: String): CommonFile {
    return CommonFile(File(path))
}

actual fun openFile(filePath: String) {
    val file = File(filePath)
    if (file.exists()) {
        Desktop.getDesktop().open(file)
    } else {
        println("File does not exist: $filePath")
    }
}