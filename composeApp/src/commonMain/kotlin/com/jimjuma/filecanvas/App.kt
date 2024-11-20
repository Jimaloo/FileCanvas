package com.jimjuma.filecanvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
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
import com.jimjuma.filecanvas.tile.DraggableResizableTile
import kotlin.math.roundToInt

@Composable
fun App() {
    var sidebarVisible by remember { mutableStateOf(true) }
    MaterialTheme {
        Column(Modifier.fillMaxSize()) {
            // Top Section
            TopSection()

            // Main Content
            Box(Modifier.fillMaxSize()) {
                // Left Sidebar
//                Box(Modifier.fillMaxHeight()) {
//                    GridBackground()
                DotGrid()
                TileContainer()
//                }

                if (sidebarVisible) {
                    FloatingSidebar { sidebarVisible = false }
                } else {

                    IconButton(
                        onClick = { sidebarVisible = true },
                        modifier = Modifier.padding(16.dp)
                            .align(Alignment.TopStart)
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
                    RightSidebar()
                }
            }
        }
    }
}

@Composable
fun FloatingSidebar(onClose: () -> Unit) {
    Surface(
        modifier = Modifier.width(250.dp).fillMaxHeight().padding(16.dp).background(Color.White)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        elevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Sidebar",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onClose) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close Sidebar"
                    )
                }
            }

        }
    }
}

@Composable
fun TopSection() {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color(0xFFE0E0E0)).padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Home")

    }
}

@Composable
fun DotGrid(
    cellSize: Dp = 40.dp,
    rowCount: Int = 1000,
    columnCount: Int = 1000,
    dotRadius: Dp = 2.dp,
    dotColor: Color = Color.LightGray
) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    Box(
        modifier = Modifier.fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale *= zoom
                    offsetX += pan.x
                    offsetY += pan.y
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            with(drawContext.canvas.nativeCanvas) {

            val cellSizePx = cellSize.toPx() /2
            val dotRadiusPx = dotRadius.toPx()/2
            scale(scale) {
                translate(offsetX, offsetY) {
                    // Iterate through rows and columns to calculate dot positions
                    for (row in 0 until rowCount) {
                        for (col in 0 until columnCount) {
                            val x = col * cellSizePx
                            val y = row * cellSizePx

                            // Draw a circle (dot) at the intersection point
                            drawCircle(
                                color = dotColor,
                                radius = dotRadiusPx,
                                center = Offset(x, y)
                            )
                        }
                    }
                }}
            }


        }
    }
}

@Composable
fun RightSidebar() {
    Box(
        Modifier.width(48.dp).fillMaxHeight().background(Color.White)
            .border(1.dp, Color.LightGray)
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


@Composable
fun GridBackground() {
    Box {
        Canvas(Modifier.fillMaxSize()) {

            drawGrid(
                gridSize = 40.dp,
                gridColor = Color.LightGray.copy(alpha = 0.5f)
            )
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
    val name: String, val offset: Offset, val size: Size
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


