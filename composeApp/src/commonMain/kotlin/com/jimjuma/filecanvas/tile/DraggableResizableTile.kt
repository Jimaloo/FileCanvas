package com.jimjuma.filecanvas.tile


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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

//@Composable
//fun DraggableResizableTile(
//    tileState: TileState,
//    onMove: (Offset) -> Unit,
//    onResize: (Size) -> Unit
//) {
//    val resizeState = remember { mutableStateOf(ResizeHandle.None) }
//    val isSelected = remember { mutableStateOf(false) }
//    val handleSize = 6.dp
//    Box(modifier = Modifier.offset {
//        IntOffset(
//            tileState.offset.x.roundToInt(),
//            tileState.offset.y.roundToInt()
//        )
//    }.size(tileState.size.width.dp, tileState.size.height.dp)
//        .background(Color.White, RoundedCornerShape(4.dp)).border(
//            width = if (isSelected.value) 2.dp else 1.dp,
//            color = if (isSelected.value) Color(0xFF2196F3) else Color.LightGray,
//            shape = RoundedCornerShape(4.dp)
//        ).shadow(if (isSelected.value) 1.dp else 1.dp, RoundedCornerShape(4.dp))
//        .pointerInput(Unit) {
//            detectDragGestures(onDragStart = { isSelected.value = true },
//                onDragEnd = { },
//                onDragCancel = { }) { change, dragAmount ->
//                if (resizeState.value == ResizeHandle.None) {
//                    change.consume()
//                    onMove(dragAmount)
//                }
//            }
//        }) {
////        Content
//        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            Text(
//                tileState.name,
//                color = Color.Black
//            )
//        }
////        Show resize handles only when selected
//        if (isSelected.value) {
////                    Corners
//            listOf(
//                Triple(Alignment.TopStart, ResizeHandle.TopLeft, "nw-resize"),
//                Triple(Alignment.TopEnd, ResizeHandle.TopRight, "ne-resize"),
//                Triple(Alignment.BottomStart, ResizeHandle.BottomLeft, "sw-resize"),
//                Triple(Alignment.BottomEnd, ResizeHandle.BottomRight, "se-resize")
//            ).forEach { (alignment, handle, cursor) ->
//                ResizeHandle(
//                    Modifier.align(alignment),
//                    handleSize, handle, resizeState, cursor
//                ) { dragAmount ->
//                    when (handle) {
//                        ResizeHandle.TopLeft -> {
//                            val newWidth = tileState.size.width - dragAmount.x
//                            val newHeight = tileState.size.height - dragAmount.y
//                            if (newWidth >= 50f && newHeight >= 50f) {
//                                onMove(dragAmount)
//                                onResize(Size(newWidth, newHeight))
//                            }
//                        }
//
//                        ResizeHandle.TopRight -> {
//                            val newWidth = tileState.size.width + dragAmount.x
//                            val newHeight =
//                                tileState.size.height - dragAmount.y
//                            if (newWidth >= 50f && newHeight >= 50f) {
//                                onResize(Size(newWidth, newHeight))
//                                onMove(
//                                    Offset(
//                                        0f,
//                                        dragAmount.y
//                                    )
//                                )
//                            }
//                        }
//
//                        ResizeHandle.BottomLeft -> {
//                            val newWidth = tileState.size.width - dragAmount.x
//                            val newHeight =
//                                tileState.size.height + dragAmount.y
//                            if (newWidth >= 50f && newHeight >= 50f) {
//                                onResize(Size(newWidth, newHeight))
//                                onMove(
//                                    Offset(
//                                        dragAmount.x,
//                                        0f
//                                    )
//                                )
//                            }
//                        }
//
//                        ResizeHandle.BottomRight -> {
//                            val newWidth = tileState.size.width + dragAmount.x
//                            val newHeight =
//                                tileState.size.height + dragAmount.y
//                            if (newWidth >= 50f && newHeight >= 50f) {
//                                onResize(Size(newWidth, newHeight))
//                            }
//                        }
//
//                        else -> {}
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun ResizeHandle(
//    modifier: Modifier,
//    size: Dp,
//    handle: ResizeHandle,
//    resizeState: MutableState<ResizeHandle>,
//    cursor: String,
//    onResize: (Offset) -> Unit
//) {
//    Box(
//        modifier = modifier.offset(
//            x = when {
//                handle == ResizeHandle.TopLeft || handle == ResizeHandle.BottomLeft -> (-size / 2)
//                handle == ResizeHandle.TopRight || handle == ResizeHandle.BottomRight
//                -> size / 2
//
//                else -> 0.dp
//            }, y = when {
//                handle == ResizeHandle.TopLeft || handle == ResizeHandle.TopRight -> (-size / 2)
//                handle == ResizeHandle.BottomLeft || handle == ResizeHandle.BottomRight
//                -> size / 2
//
//                else -> 0.dp
//            }
//        ).size(size).background(Color.White, CircleShape)
//            .border(1.dp, Color(0xFF2196F3), CircleShape).shadow(2.dp, CircleShape)
//            .pointerInput(handle) {
//                detectDragGestures(
//                    onDragStart = { resizeState.value = handle },
//                    onDragEnd = { resizeState.value = ResizeHandle.None },
//                    onDragCancel = {
//                        resizeState.value = ResizeHandle.None
//                    }) { change, dragAmount ->
//                    change.consume()
//                    onResize(dragAmount)
//                }
//            })
//}
@Composable
fun DraggableResizableTile(
    tileState: TileState,
    onMove: (Offset) -> Unit,
    onResize: (Size) -> Unit
) {
    val resizeState = remember { mutableStateOf(ResizeHandle.None) }
    val isSelected = remember { mutableStateOf(false) }
    val handleSize = 6.dp
    Box(modifier = Modifier.offset {
        IntOffset(
            tileState.offset.x.roundToInt(),
            tileState.offset.y.roundToInt()
        )
    }.size(tileState.size.width.dp, tileState.size.height.dp)
        .background(MaterialTheme.colors.surface, RoundedCornerShape(8.dp)).border(
            width = if (isSelected.value) 2.dp else 1.dp,
            color = if (isSelected.value) MaterialTheme.colors.primary else Color.LightGray,
            shape = RoundedCornerShape(8.dp)
        ).shadow(
            elevation = if (isSelected.value) 4.dp else 1.dp,
            shape = RoundedCornerShape(8.dp)
        )
        .pointerInput(Unit) {
            detectDragGestures(onDragStart = { isSelected.value = true },
                onDragEnd = { },
                onDragCancel = { }) { change, dragAmount ->
                if (resizeState.value == ResizeHandle.None) {
                    change.consume()
                    onMove(dragAmount)
                }
            }
        }) {
        // Content
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                tileState.name,
                color = MaterialTheme.colors.onSurface
            )
        }
//     Show resize handles only when selected
        if (isSelected.value) {
            val resizeHandles = listOf(
                Triple(Alignment.TopStart, ResizeHandle.TopLeft, "nw-resize"),
                Triple(Alignment.TopEnd, ResizeHandle.TopRight, "ne-resize"),
                Triple(Alignment.BottomStart, ResizeHandle.BottomLeft, "sw-resize"),
                Triple(Alignment.BottomEnd, ResizeHandle.BottomRight, "se-resize")
            )
            resizeHandles.forEach { (alignment, handle, cursor) ->
                ResizeHandle(
                    Modifier.align(
                        alignment
                    ), handleSize, handle, resizeState, cursor
                ) { dragAmount -> handleResize(tileState, handle, dragAmount, onMove, onResize) }
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
    Box(
        modifier = modifier.size(size).background(MaterialTheme.colors.surface, CircleShape)
            .border(1.dp, MaterialTheme.colors.primary, CircleShape).pointerInput(handle) {
                detectDragGestures(onDragStart = { resizeState.value = handle },
                    onDragEnd = { resizeState.value = ResizeHandle.None },
                    onDragCancel = {
                        resizeState.value = ResizeHandle.None
                    }) { change, dragAmount ->
                    change.consume()
                    onResize(dragAmount)
                }
            })
}

fun handleResize(
    tileState: TileState,
    handle: ResizeHandle,
    dragAmount: Offset,
    onMove: (Offset) -> Unit,
    onResize: (Size) -> Unit
) {
    when (handle) {
        ResizeHandle.TopLeft -> {
            val newWidth = tileState.size.width - dragAmount.x
            val newHeight =
                tileState.size.height - dragAmount.y
            if (newWidth >= 50f && newHeight >= 50f) {
                onMove(dragAmount)
                onResize (Size(newWidth, newHeight))
            }
        }

        ResizeHandle.TopRight -> {
            val newWidth = tileState.size.width + dragAmount.x
            val newHeight =
                tileState.size.height - dragAmount.y
            if (newWidth >= 50f && newHeight >= 50f) {
                onResize(Size(newWidth, newHeight))
                onMove(Offset(0f, dragAmount.y))
            }
        }

        ResizeHandle.BottomLeft -> {
            val newWidth = tileState.size.width - dragAmount.x
            val newHeight =
                tileState.size.height + dragAmount.y
            if (newWidth >= 50f && newHeight >= 50f) {
                onResize(Size(newWidth, newHeight))
                onMove(Offset(dragAmount.x, 0f))
            }
        }

        ResizeHandle.BottomRight -> {
            val newWidth = tileState.size.width + dragAmount.x
            val newHeight =
                tileState.size.height + dragAmount.y
            if (newWidth >= 50f && newHeight >= 50f) {
                onResize(Size(newWidth, newHeight))
            }
        }

        else -> {}
    }
}
