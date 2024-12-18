package com.jimjuma.filecanvas.extensions

// Fetch metadata from the provided URL
expect suspend fun fetchLinkMetadata(url: String): LinkMetadata

// Open the link in the default browser
expect fun openInBrowser(url: String)

data class LinkMetadata(
    val title: String,
    val description: String,
    val imageUrl: String?,
    val url: String
)