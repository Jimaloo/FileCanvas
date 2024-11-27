package com.jimjuma.filecanvas.extensions

// jvmMain
import java.io.File as JFile
import java.awt.FileDialog
import java.awt.Frame
import org.apache.pdfbox.pdmodel.PDDocument

actual fun chooseFile(): CommonFile? {
    val fileDialog = FileDialog(Frame(), "Select PDF File", FileDialog.LOAD)
    fileDialog.isVisible = true
    val selectedFile = fileDialog.files?.firstOrNull()
    return if (selectedFile != null && selectedFile.extension.equals("pdf", ignoreCase = true)) {
        CommonFile(selectedFile)
    } else {
        null
    }
}

actual fun extractTextFromPdf(file: CommonFile): String {
    val jFile = file.getJFile()
    PDDocument.load(jFile).use { document ->
        val pdfTextStripper = org.apache.pdfbox.text.PDFTextStripper()
        return pdfTextStripper.getText(document)
    }
}

actual class CommonFile(private val jFile: JFile) {
    actual val name: String
        get() = jFile.name

    actual fun extension(): String = jFile.extension

    fun getJFile(): JFile = jFile
}
