package com.jimjuma.filecanvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Home
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jimjuma.filecanvas.composables.LinkPreviewCard
import com.jimjuma.filecanvas.extensions.CommonFile
import com.jimjuma.filecanvas.extensions.chooseFile
import com.jimjuma.filecanvas.extensions.loadImage
import com.jimjuma.filecanvas.extensions.renderPdfPages
import com.jimjuma.filecanvas.tile.DraggableResizableTile
import filecanvas.composeapp.generated.resources.Res
import filecanvas.composeapp.generated.resources.add__alt
import filecanvas.composeapp.generated.resources.document
import filecanvas.composeapp.generated.resources.document__add
import filecanvas.composeapp.generated.resources.ibm_cloud__vpc_file_storage
import filecanvas.composeapp.generated.resources.idea
import filecanvas.composeapp.generated.resources.subtract__alt
import filecanvas.composeapp.generated.resources.trash
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt

@Composable
fun App() {
    var sidebarVisible by remember { mutableStateOf(true) }
    var selectedFile by remember { mutableStateOf<CommonFile?>(null) }
    var pdfText by remember { mutableStateOf("") }
    var scale by remember { mutableStateOf(1f) }
    var tiles by remember {
        mutableStateOf(
            mutableListOf<TileState>(
            )
        )
    }

    MaterialTheme {
        Box(Modifier.fillMaxSize()) {
            DotGrid(scale)
            Column(Modifier.fillMaxSize()) {
                // Top Section
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .background(Color(0xFFF5F7F8))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Home")
                }

                Divider(modifier = Modifier.height(.3.dp))

                Row(
                    modifier = Modifier
                        .background(Color(0xFFF5F7F8))
                        .fillMaxWidth()
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically,

                    ) {


                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp).clickable {

                            val file = chooseFile()
                            if (file != null) {
                                when {
                                    file.extension().equals("pdf", ignoreCase = true) -> {
                                        selectedFile = file
                                        tiles = tiles.toMutableList().apply {
                                            val pdfPages = renderPdfPages(file)
                                            add(
                                                TileState(
                                                    name = selectedFile?.name ?: "",
                                                    offset = Offset(
                                                        300f,
                                                        700f
                                                    ), // Offset each page slightly
                                                    size = Size(300f, 400f),
                                                    imageBitmaps = pdfPages
                                                )
                                            )

                                        }
                                    }

                                    file.extension().lowercase() in listOf(
                                        "jpg",
                                        "jpeg",
                                        "png",
                                        "bmp",
                                        "gif"
                                    ) -> {
                                        selectedFile = file

                                        tiles = tiles.toMutableList().apply {
                                            val imageBitmap =
                                                loadImage(file) // Platform-specific image loader
                                            add(
                                                TileState(
                                                    name = file.name,
                                                    offset = Offset(300f, 700f),
                                                    size = Size(300f, 400f),
                                                    imageBitmap = imageBitmap,
                                                )
                                            )
                                        }
                                    }

                                    else -> {
                                        pdfText = "Please select a valid PDF or image file."
                                    }
                                }
                            }

                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(32.dp).padding(horizontal = 6.dp),
                            painter =
                            painterResource(Res.drawable.document__add),
                            contentDescription = ""
                        )
                        Text("Select file")
                    }



                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(32.dp).padding(horizontal = 6.dp),
                            painter = painterResource(Res.drawable.add__alt),
                            contentDescription = ""
                        )
                        Text(" Center zoom ")
                    }



                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp).clickable {
                            scale += .1f
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(32.dp).padding(horizontal = 6.dp),
                            painter = painterResource(Res.drawable.add__alt),
                            contentDescription = ""
                        )
                        Text(" Zoom in")
                    }


                    Text(
                        text = "${scale.toInt() * 100}",
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )

                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp).clickable {
                            scale -= .1f
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(32.dp).padding(horizontal = 6.dp),
                            painter = painterResource(Res.drawable.subtract__alt),
                            contentDescription = ""
                        )
                        Text(" Zoom out ")
                    }

                }

                Divider(modifier = Modifier.height(.3.dp))

                Row(modifier = Modifier.fillMaxWidth().weight(1f)) {

                    Column(
                        modifier = Modifier.width(48.dp)
                            .background(Color(0xFFF5F7F8))
                            .fillMaxHeight()
                    ) {
                        IconButton(
                            onClick = {
                                sidebarVisible = true
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(35.dp).padding(horizontal = 6.dp),
                                painter =
                                painterResource(Res.drawable.ibm_cloud__vpc_file_storage),
                                contentDescription = ""
                            )
                        }
                    }

                    Divider(
                        modifier = Modifier.width(.3.dp).fillMaxHeight()
                    )

                    if (sidebarVisible) {
                        FloatingSidebar(tiles.map { it.name }) { sidebarVisible = false }
                    }
                    Divider(
                        modifier = Modifier.width(.3.dp).fillMaxHeight()
                    )

                    Box(Modifier.weight(1f).scale(scale)) {
                        LinkPreviewCard("https://www.nytimes.com/2024/12/18/health/ozempic-food-rfk-elon-musk.html")
                        tiles.forEachIndexed { index, tile ->
                            DraggableResizableTile(tileState = tile, onMove = { dragAmount ->
                                tiles = tiles.toMutableList().apply {
                                    this[index] = this[index].copy(
                                        offset = this[index].offset + dragAmount
                                    )
                                }
                            }, onResize = { newSize ->
                                tiles = tiles.toMutableList().apply {
                                    this[index] = this[index].copy(
                                        size = newSize
                                    )
                                }
                            })
                        }
                    }

                    Divider(modifier = Modifier.width(.3.dp).fillMaxHeight())

                    RightSidebar()
                }
            }
        }
    }
}

@Composable
fun FloatingSidebar(files: List<String>, onClose: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(150.dp)
            .background(Color(0xFFF5F7F8))
//            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(10.dp)) {
            Text(
                "Sidebar"
            )
            Spacer(modifier = Modifier.weight(1f))

            Icon(
                Icons.Default.Close,
                modifier = Modifier.clickable {
                    onClose()
                },
                contentDescription = "Close Sidebar"
            )

        }
        files.map {
            Row(modifier = Modifier.padding(5.dp)) {
                Icon(
                    painter = painterResource(Res.drawable.document),
                    modifier = Modifier.size(20.dp),
                    contentDescription = ""
                )
                Text(
                    text = it,
                    style = MaterialTheme.typography.overline,
                    modifier = Modifier.padding(
                        horizontal =
                        5.dp
                    )
                )
            }
        }

    }

}

@Composable
fun DotGrid(
    scale: Float,
    cellSize: Dp = 40.dp,
    dotRadius: Dp = 1.dp,
    dotColor: Color = Color.LightGray
) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    Box(
        modifier = Modifier
            .fillMaxSize().scale(scale)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.5f, 3f)
                    offsetX += pan.x
                    offsetY += pan.y
                }
            }.background(Color.White)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val cellSizePx = cellSize.toPx()
            val dotRadiusPx = dotRadius.toPx()

            // Calculate visible bounds in the canvas
            val startX = ((-offsetX / scale) / cellSizePx).toInt() - 1
            val endX = ((size.width / scale - offsetX / scale) / cellSizePx).toInt() + 1
            val startY = ((-offsetY / scale) / cellSizePx).toInt() - 1
            val endY = ((size.height / scale - offsetY / scale) / cellSizePx).toInt() + 1

            // Draw the grid
            for (row in startY..endY) {
                for (col in startX..endX) {
                    val x = col * cellSizePx * scale + offsetX
                    val y = row * cellSizePx * scale + offsetY

                    drawCircle(
                        color = dotColor,
                        radius = dotRadiusPx,
                        center = Offset(x, y)
                    )
                }
            }
        }
    }
}

@Composable
fun RightSidebar() {
    Box(
        Modifier.width(48.dp).fillMaxHeight().background(Color(0xFFF5F7F8))
    ) {
        Column(
            Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = {}
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(35.dp)
                            .padding(6.dp),
                        painter = painterResource(Res.drawable.trash),
                        contentDescription = "", tint = Color.Red
                    )

                }
            }

            IconButton(
                onClick = {}
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(35.dp)
                            .padding(6.dp),
                        painter = painterResource(Res.drawable.idea),
                        contentDescription = ""

                    )

                }
            }
        }
    }
}

fun DrawScope.drawGrid(gridSize: Dp, gridColor: Color) {
    val gridPixel = gridSize.toPx()
    val horizontalLines = (size.height / gridPixel).roundToInt()
    val verticalLines = (size.width / gridPixel).roundToInt()

    for (i in 0..horizontalLines) {
        val y = i * gridPixel
        drawLine(
            color = gridColor,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1f
        )
    }

    for (i in 0..verticalLines) {
        val x = i * gridPixel
        drawLine(
            color = gridColor,
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = 1f
        )
    }
}

data class TileState(
    var name: String,
    var offset: Offset,
    var size: Size,
    var imageBitmap: ImageBitmap? = null,
    var imageBitmaps: List<ImageBitmap>? = null
)


