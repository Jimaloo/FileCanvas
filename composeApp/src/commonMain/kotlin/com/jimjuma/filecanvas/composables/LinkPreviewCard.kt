package com.jimjuma.filecanvas.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jimjuma.filecanvas.extensions.LinkMetadata
import com.jimjuma.filecanvas.extensions.fetchLinkMetadata
import com.jimjuma.filecanvas.extensions.openInBrowser
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

@Composable
fun LinkPreviewCard(url: String) {
    var metadata by remember { mutableStateOf<LinkMetadata?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(url) {
        isLoading = true
        try {
            metadata = fetchLinkMetadata(url)
        } catch (e: Exception) {
            metadata = null // Handle errors gracefully
        }
        isLoading = false
    }

    if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        return
    }

    metadata?.let {
        Card(
            shape = RoundedCornerShape(8.dp),
            backgroundColor = MaterialTheme.colors.surface,
            elevation = 4.dp,
            modifier = Modifier
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                // Image
                it.imageUrl?.let { imageUrl ->
                    val painterResource: Resource<Painter> =
                        asyncPainterResource(imageUrl) {

                            // CoroutineContext to be used while loading the image.
                            coroutineContext = Job() + Dispatchers.Default
                        }

                    KamelImage({
                        painterResource
                    },
                        modifier = Modifier
                            .height(100.dp)
                            .padding(bottom = 8.dp),
                        contentDescription = "Link image")

                }

                // Title
                Text(
                    text = it.title,
                    style = MaterialTheme.typography.h6,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Description
                Text(
                    text = it.description,
                    style = MaterialTheme.typography.body2,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                // Open link button
                Button(
                    onClick = { openInBrowser(it.url) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Open Link")
                }
            }
        }
    } ?: run {
        Text(
            text = "Failed to load metadata for $url",
            color = Color.Red,
            modifier = Modifier.padding(16.dp)
        )
    }
}