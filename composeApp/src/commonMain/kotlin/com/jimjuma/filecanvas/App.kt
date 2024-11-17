package com.jimjuma.filecanvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.jimjuma.filecanvas.tile.DraggableResizableTile
import kotlin.math.roundToInt

@Composable
fun App() {
    MaterialTheme {
        Column(Modifier.fillMaxSize()) {
            // Top Section
            TopSection()

            // Main Content
            Row(Modifier.fillMaxSize()) {
                // Left Sidebar
                LeftSidebar()

                // Main Canvas Area
                Box(Modifier.weight(1f).fillMaxHeight()) {
                    GridBackground()
                    TileContainer()
                }

                // Right Sidebar
                RightSidebar()
            }
        }
    }
}
@Composable
fun TopSection() {
    Column {
        // Project Name Bar
        Row(
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Color.White)
                .border(1.dp, Color.LightGray),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Test project",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.h6
            )
        }

        // Toolbar
        Row(
            Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(Color.White)
                .border(1.dp, Color.LightGray),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(Modifier.width(8.dp))

            // Style dropdown
            OutlinedButton(
                onClick = { /* TODO */ },
                modifier = Modifier.height(32.dp)
            ) {
                Text("Style")
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }

            // Main actions
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.Email, "Page")
            }
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.Email, "Board")
            }
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.Email, "File")
            }

            // Secondary actions
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.Email, "Link")
            }
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.Add, "Add")
            }
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.Email, "Remove")
            }
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.Email, "Help")
            }
        }
    }
}

@Composable
fun LeftSidebar() {
    Box(
        Modifier
            .width(48.dp)
            .fillMaxHeight()
            .background(Color.White)
            .border(1.dp, Color.LightGray)
    ) {
        Column(
            Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.Menu, "Menu")
            }
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.Refresh, "Refresh")
            }
        }
    }
}

@Composable
fun RightSidebar() {
    Box(
        Modifier
            .width(48.dp)
            .fillMaxHeight()
            .background(Color.White)
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
    Canvas(Modifier.fillMaxSize()) {
        drawGrid(gridSize = 40.dp,
            gridColor = Color.LightGray.copy(alpha = 0.5f))
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
    val name: String,
    val offset: Offset,
    val size: Size
)

@Composable
fun TileContainer() {
    var tiles by remember {
        mutableStateOf(
            listOf(
                TileState(
                    name = "File 1",
                    offset = Offset(100f, 100f),
                    size = Size(100f, 100f)
                ),
                TileState(
                    name = "File 2",
                    offset = Offset(300f, 200f),
                    size = Size(150f, 150f)
                )
            )
        )
    }

    Box(Modifier.fillMaxSize()) {
        tiles.forEachIndexed { index, tile ->
//            DraggableResizableTile(
//                tileState = tile,
//                onMove = { dragAmount ->
//                    tiles = tiles.toMutableList().apply {
//                        this[index] = this[index].copy(
//                            offset = this[index].offset + dragAmount
//                        )
//                    }
//                }
//            )
            DraggableResizableTile(
                tileState = tile,
                onMove = { dragAmount ->
                    tiles = tiles.toMutableList().apply {
                        this[index] = this[index].copy(
                            offset = this[index].offset + dragAmount
                        )
                    }
                },
                onResize = { newSize ->
                    tiles = tiles.toMutableList().apply {
                        this[index] = this[index].copy(
                            size = newSize
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun DraggableResizableTile1(
    tileState: TileState,
    onMove: (Offset) -> Unit
) {
    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    tileState.offset.x.roundToInt(),
                    tileState.offset.y.roundToInt()
                )
            }
            .size(tileState.size.width.dp,
                tileState.size.height.dp)
            .background(Color.Cyan, RoundedCornerShape(4.dp))
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onMove(dragAmount)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(tileState.name, color = Color.Black)
    }
}