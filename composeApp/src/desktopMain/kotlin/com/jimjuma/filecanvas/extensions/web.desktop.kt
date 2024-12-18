package com.jimjuma.filecanvas.extensions

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.gson.gson
import org.jsoup.Jsoup
import java.awt.Desktop
import java.net.URI

actual suspend fun fetchLinkMetadata(url: String): LinkMetadata {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            gson()
        }
    }

    // Fetch HTML content
    val response: HttpResponse = httpClient.get(url)
    val htmlContent: String = response.body()

    // Parse metadata using Jsoup
    val document = Jsoup.parse(htmlContent)
    val title = document.select("meta[property=og:title]").attr("content")
        ?: document.title()
    val description = document.select("meta[property=og:description]").attr("content")
        ?: ""
    val imageUrl = document.select("meta[property=og:image]").attr("content")

    httpClient.close()

    return LinkMetadata(
        title = title,
        description = description,
        imageUrl = imageUrl.takeIf { it.isNotBlank() },
        url = url
    )
}

actual fun openInBrowser(url: String) {
    if (Desktop.isDesktopSupported()) {
        Desktop.getDesktop().browse(URI(url))
    }
}