package com.jimjuma.filecanvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Home
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jimjuma.filecanvas.extensions.CommonFile
import com.jimjuma.filecanvas.extensions.chooseFile
import com.jimjuma.filecanvas.extensions.extractTextFromPdf
import com.jimjuma.filecanvas.tile.DraggableResizableTile
import kotlin.math.roundToInt

@Composable
fun App() {
    var sidebarVisible by remember { mutableStateOf(true) }
    var selectedFile by remember { mutableStateOf<CommonFile?>(null) }
    var pdfText by remember { mutableStateOf("") }
    var tiles by remember {
        mutableStateOf(
            mutableListOf<TileState>(
                TileState(
                    name = "File 1", offset = Offset(100f, 100f), size = Size(100f, 100f)
                ), TileState(
                    name = "File 2", offset = Offset(300f, 200f), size = Size(150f, 150f)
                )
            )
        )
    }

    MaterialTheme {
        Box(Modifier.fillMaxSize()) {
            DotGrid()
            Column(Modifier.fillMaxSize()) {
                // Top Section
                Row(
                    modifier = Modifier.fillMaxWidth().background(Color.White).padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Home")
                }

                Divider(modifier = Modifier.height(.5.dp))

                Row(
                    modifier = Modifier
                        .background(Color.White)
                        .fillMaxWidth()
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text("Pick PDF File", modifier = Modifier.clickable {
                        val file = chooseFile()
                        if (file != null) {
                            when {
                                file.extension().equals("pdf", ignoreCase = true) -> {
                                    selectedFile = file
                                    pdfText = extractTextFromPdf(file) // Function to extract text from the PDF
                                    tiles = tiles.toMutableList().apply {
                                        val pdfContents = "${selectedFile?.name}\n\n$pdfText"
                                        add(
                                            TileState(
                                                name = pdfContents,
                                                offset = Offset(300f, 700f),
                                                size = Size(300f, 700f)
                                            )
                                        )
                                    }
                                }
                                file.extension().lowercase() in listOf("jpg", "jpeg", "png", "bmp", "gif") -> {
                                    selectedFile = file
//                                    val imageBitmap = loadImage(file) // Function to load image into ImageBitmap
                                    tiles = tiles.toMutableList().apply {
                                        add(
                                            TileState(
                                                name = file.name,
                                                offset = Offset(300f, 700f),
                                                size = Size(300f, 700f),
                                                imageBitmap = null
                                            )
                                        )
                                    }
                                }
                                else -> {
                                    pdfText = "Please select a valid PDF or image file."
                                }
                            }
                        }
                    })
                    Text(" | ")
                    Text(" Center zoom ")
                    Text(" | ")
                    Text(" Zoom in ")
                    Text(" | ")
                    Text(" Zoom out ")
                }

                Divider(modifier = Modifier.height(.5.dp))

                Row(modifier = Modifier.fillMaxWidth().weight(1f)) {

                    Column(
                        modifier = Modifier.width(48.dp).background(Color.White).fillMaxHeight()
                    ) {
                        IconButton(onClick = { /* TODO */ }) {
                            Icon(Icons.Default.Settings, "Settings")
                        }

                        IconButton(onClick = { /* TODO */ }) {
                            Icon(Icons.Default.Settings, "Settings")
                        }
                    }

                    Divider(modifier = Modifier.width(1.dp))
                    if (sidebarVisible) {
                        FloatingSidebar { sidebarVisible = false }
                    } else {
                        IconButton(
                            onClick = { sidebarVisible = true },
                            modifier = Modifier.padding(16.dp)
                                .background(
                                    Color(0xFF6200EA),
                                    RoundedCornerShape(50)
                                )
                        ) {
                            Icon(
                                Icons.Outlined.Home,
                                contentDescription = "Open Sidebar",
                                tint = Color.White
                            )
                        }
                    }

                    Box(Modifier.weight(1f)) {
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

                    Divider(modifier = Modifier.width(.5.dp).fillMaxHeight())

                    RightSidebar()
                }
            }
        }
    }
}

@Composable
fun FloatingSidebar(onClose: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(150.dp)
            .background(Color.White)
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
                    onClose
                },
                contentDescription = "Close Sidebar"
            )

        }

    }

}

@Composable
fun DotGrid(
    cellSize: Dp = 40.dp,
    dotRadius: Dp = 2.dp,
    dotColor: Color = Color.LightGray
) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.5f, 3f)
                    offsetX += pan.x
                    offsetY += pan.y
                }
            }
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
        Modifier.width(48.dp).fillMaxHeight().background(Color.White)
//            .border(1.dp, Color.LightGray)
    ) {
        Column(
            Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.Notifications, "Notifications")
            }
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.Settings, "Settings")
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
    val name: String, val offset: Offset, val size: Size, val imageBitmap: ImageBitmap? = null
)

@Composable
fun TileContainer() {
    var tiles by remember {
        mutableStateOf(
            listOf(
                TileState(
                    name = "File 1", offset = Offset(100f, 100f), size = Size(100f, 100f)
                ), TileState(
                    name = "File 2", offset = Offset(300f, 200f), size = Size(150f, 150f)
                )
            )
        )
    }

    Box(Modifier.fillMaxSize()) {
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
}


