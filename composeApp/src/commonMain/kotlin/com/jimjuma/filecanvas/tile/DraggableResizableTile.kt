package com.jimjuma.filecanvas.tile


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.jimjuma.filecanvas.TileState
import kotlin.math.roundToInt


@Composable
fun DraggableResizableTile(
    tileState: TileState,
    onMove: (Offset) -> Unit,
    onResize: (Size) -> Unit
) {
    val resizeState = remember { mutableStateOf(ResizeHandle.None) }
    val isSelected = remember { mutableStateOf(false) }
    val handleSize = 12.dp

    val borderColor by animateColorAsState(
        targetValue = if (isSelected.value) MaterialTheme.colors.primary else Color.LightGray
    )
    val elevation by animateDpAsState(
        targetValue = if (isSelected.value) 5.dp else 1.dp
    )
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .offset { IntOffset(tileState.offset.x.roundToInt(), tileState.offset.y.roundToInt()) }
            .size(tileState.size.width.dp, tileState.size.height.dp)
            .background(MaterialTheme.colors.surface, RoundedCornerShape(12.dp))
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
//            .shadow(elevation, RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
//                detectTapGestures (
//                    onTap = { isSelected.value = !isSelected.value },
//                )
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
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colors.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = tileState.name,
                    modifier = Modifier.verticalScroll(scrollState).padding(20.dp),
                    style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.onSurface)
                )
            }
        }

        // Resize handles
        if (isSelected.value) {
            ResizeHandles(
                handleSize = handleSize,
                resizeState = resizeState,
                tileState = tileState,
                onMove = onMove,
                onResize = onResize
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
//            modifier = Modifier.align(alignment),
            modifier = Modifier,
            size = handleSize,
            handle = handle,
            resizeState = resizeState,
            cursor = cursor
        ) { dragAmount ->
            handleResize(tileState, handle, dragAmount, onMove, onResize)
        }
    }}
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
    Box(
        modifier = modifier
            .size(size)
            .background(MaterialTheme.colors.surface, CircleShape)
            .border(1.dp, MaterialTheme.colors.primary, CircleShape)
            .pointerInput(handle) {
//                detectDragGestures(
//                    onDragStart = { resizeState.value = handle },
//                    onDragEnd = { resizeState.value = ResizeHandle.None },
//                    onDragCancel = { resizeState.value = ResizeHandle.None }
//                ) { change, dragAmount ->
//                    change.consume()
//                    onResize(dragAmount)
//                }
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onResize(dragAmount)
                }
            }
    )
}

fun handleResize(
    tileState: TileState,
    handle: ResizeHandle,
    dragAmount: Offset,
    onMove: (Offset) -> Unit,
    onResize: (Size) -> Unit
) {
    val minSize = 50f

    when (handle) {
        ResizeHandle.TopLeft -> {
            val newWidth = tileState.size.width - dragAmount.x
            val newHeight = tileState.size.height - dragAmount.y
            if (newWidth >= minSize && newHeight >= minSize) {
                onMove(dragAmount)
                onResize(Size(newWidth, newHeight))
            }
        }
        ResizeHandle.TopRight -> {
            val newWidth = tileState.size.width + dragAmount.x
            val newHeight = tileState.size.height - dragAmount.y
            if (newWidth >= minSize && newHeight >= minSize) {
                onResize(Size(newWidth, newHeight))
                onMove(Offset(0f, dragAmount.y))
            }
        }
        ResizeHandle.BottomLeft -> {
            val newWidth = tileState.size.width - dragAmount.x
            val newHeight = tileState.size.height + dragAmount.y
            if (newWidth >= minSize && newHeight >= minSize) {
                onResize(Size(newWidth, newHeight))
                onMove(Offset(dragAmount.x, 0f))
            }
        }
        ResizeHandle.BottomRight -> {
            val newWidth = tileState.size.width + dragAmount.x
            val newHeight = tileState.size.height + dragAmount.y
            if (newWidth >= minSize && newHeight >= minSize) {
                onResize(Size(newWidth, newHeight))
            }
        }
        else -> Unit
    }
}
