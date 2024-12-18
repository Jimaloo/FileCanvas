package com.jimjuma.filecanvas.tile

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.jimjuma.filecanvas.TileState
import com.jimjuma.filecanvas.extensions.openFile
import filecanvas.composeapp.generated.resources.Res
import filecanvas.composeapp.generated.resources.arrow_bottom_left
import filecanvas.composeapp.generated.resources.arrow_bottom_right
import filecanvas.composeapp.generated.resources.arrow_up_left
import filecanvas.composeapp.generated.resources.arrow_up_right
import filecanvas.composeapp.generated.resources.document
import filecanvas.composeapp.generated.resources.subtract__alt
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt

@Composable
fun DraggableResizableTile(
    tileState: TileState,
    onMove: (Offset) -> Unit,
    onResize: (Size) -> Unit
) {
    val resizeState = remember { mutableStateOf(ResizeHandle.None) }
    val isSelected = remember { mutableStateOf(false) }
    val handleSize = 25.dp

    val borderColor by animateColorAsState(
        targetValue = if (isSelected.value) MaterialTheme.colors.primary else Color.LightGray
    )
    val elevation by animateDpAsState(
        targetValue = if (isSelected.value) 8.dp else 4.dp
    )
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .offset { IntOffset(tileState.offset.x.roundToInt(), tileState.offset.y.roundToInt()) }
            .size(tileState.size.width.dp, tileState.size.height.dp)
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(12.dp),
                clip = true
            )
            .background(MaterialTheme.colors.surface, RoundedCornerShape(12.dp))
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { isSelected.value = !isSelected.value },
                    onDoubleTap = {
                        openFile(tileState.name)
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isSelected.value = true },
                    onDragEnd = { isSelected.value = false },
                    onDragCancel = { isSelected.value = false }
                ) { change, dragAmount ->
                    if (resizeState.value == ResizeHandle.None) {
                        change.consume()
                        onMove(dragAmount)
                    }
                }

            }
    ) {
        // Content inside the tile
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Icon(
                    modifier = Modifier.size(32.dp).padding(vertical = 4.dp),
                    painter = painterResource(Res.drawable.document),
                    contentDescription = null
                )

                Text(
                    text = tileState.name,
                    modifier = Modifier.verticalScroll(scrollState)
                        .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                    style = MaterialTheme.typography.caption.copy(color = Color.Blue)
                )
                Divider()
                tileState.imageBitmaps?.let {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(it.size) { index ->
                            Image(
                                bitmap = it[index],
                                contentDescription = "PDF Page $index",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
                tileState.imageBitmap?.let {
                    Image(
                        it,
                        ""
                    )
                }
            }
        }

        if (isSelected.value) {
            ResizeHandles(
                handleSize = handleSize,
                resizeState = resizeState,
                tileState = tileState,
                onMove = onMove,
                onResize = { newSize ->
                    tileState.size = newSize
//                    onResize(newSize)
                }
            )
        }
    }
}

@Composable
private fun ResizeHandles(
    handleSize: Dp,
    resizeState: MutableState<ResizeHandle>,
    tileState: TileState,
    onMove: (Offset) -> Unit,
    onResize: (Size) -> Unit
) {
    val handles = listOf(
        Triple(Alignment.TopStart, ResizeHandle.TopLeft, "nw-resize"),
        Triple(Alignment.TopEnd, ResizeHandle.TopRight, "ne-resize"),
        Triple(Alignment.BottomStart, ResizeHandle.BottomLeft, "sw-resize"),
        Triple(Alignment.BottomEnd, ResizeHandle.BottomRight, "se-resize")
    )
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        handles.forEach { (alignment, handle, cursor) ->
            ResizeHandle(
                modifier = Modifier.align(alignment),
                size = handleSize,
                handle = handle,
                resizeState = resizeState,
                cursor = cursor
            ) { dragAmount ->
                handleResize(tileState, handle, dragAmount, onMove, onResize)
            }
        }
    }
}

@Composable
fun ResizeHandle(
    modifier: Modifier,
    size: Dp,
    handle: ResizeHandle,
    resizeState: MutableState<ResizeHandle>,
    cursor: String,
    onResize: (Offset) -> Unit
) {
    val icon: Painter = when (handle) {
        ResizeHandle.TopLeft -> painterResource(Res.drawable.arrow_up_left)
        ResizeHandle.TopRight -> painterResource(Res.drawable.arrow_up_right)
        ResizeHandle.BottomLeft -> painterResource(Res.drawable.arrow_bottom_left)
        ResizeHandle.BottomRight -> painterResource(Res.drawable.arrow_bottom_right)
        else -> painterResource(Res.drawable.arrow_up_left)
    }
    Box(
        modifier = modifier
            .size(size)
            .padding(4.dp)
//            .background(Color.Red, CircleShape)
//            .border(1.dp, MaterialTheme.colors.primary, CircleShape)
            .pointerInput(handle) {
                detectDragGestures(
                    onDragStart = { resizeState.value = handle },
                    onDragEnd = { resizeState.value = ResizeHandle.None },
                    onDragCancel = { resizeState.value = ResizeHandle.None }
                ) { change, dragAmount ->
                    change.consume()
                    onResize(dragAmount)
                }
//                detectDragGestures { change, dragAmount ->
//                    change.consume()
//                    onResize(dragAmount)
//                }
            }
    ){
        Icon(
            painter = icon,
            contentDescription = "Resize handle",
            modifier = Modifier.size(size),
            tint = MaterialTheme.colors.primary
        )
    }
}

fun handleResize(
    tileState: TileState,
    handle: ResizeHandle,
    dragAmount: Offset,
    onMove: (Offset) -> Unit,
    onResize: (Size) -> Unit
) {
    val minSize = 200f

    when (handle) {
        ResizeHandle.TopLeft -> {
            val newWidth = tileState.size.width - dragAmount.x
            val newHeight = tileState.size.height - dragAmount.y
            if (newWidth >= minSize && newHeight >= minSize) {
                onMove(dragAmount)
                tileState.size = Size(newWidth, newHeight)
                onResize(Size(newWidth, newHeight))
            }
        }

        ResizeHandle.TopRight -> {
            val newWidth = tileState.size.width + dragAmount.x
            val newHeight = tileState.size.height - dragAmount.y
            if (newWidth >= minSize && newHeight >= minSize) {
                onResize(Size(newWidth, newHeight))
                tileState.size = Size(newWidth, newHeight)
                onMove(Offset(0f, dragAmount.y))
            }
        }

        ResizeHandle.BottomLeft -> {
            val newWidth = tileState.size.width - dragAmount.x
            val newHeight = tileState.size.height + dragAmount.y
            if (newWidth >= minSize && newHeight >= minSize) {
                onResize(Size(newWidth, newHeight))
                tileState.size = Size(newWidth, newHeight)
                onMove(Offset(dragAmount.x, 0f))
            }
        }

        ResizeHandle.BottomRight -> {
            val newWidth = tileState.size.width + dragAmount.x
            val newHeight = tileState.size.height + dragAmount.y
            if (newWidth >= minSize && newHeight >= minSize) {
                onResize(Size(newWidth, newHeight))
                tileState.size = Size(newWidth, newHeight)
            }
        }

        else -> Unit
    }
}
