package com.jimjuma.filecanvas.composables

import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.PopupPositionProvider

private fun calculateBoundsRelativeToUsableScreen(
    usableScreenBounds: IntRect,
    anchorBoundsInScreen: IntRect,
    contentSize: IntSize,
    popupPositionProvider: PopupPositionProvider,
    layoutDirection: LayoutDirection,
) : IntRect {
    // Because PopoverPositionProvider takes a *size* for windowSize (really screen size, in our case),
    // coordinates need to be translated to/from a zero-origin rect before and after, to account for any top/left
    // insets in the usable screen bounds.
    val anchorBoundsRelativeToUsableScreen = anchorBoundsInScreen.translate(-usableScreenBounds.topLeft)

    val boundsRelativeToUsableScreen = popupPositionProvider.calculatePosition(
        anchorBounds = anchorBoundsRelativeToUsableScreen,
        windowSize = usableScreenBounds.size,
        layoutDirection = layoutDirection,
        popupContentSize = contentSize
    ).let { IntRect(it, contentSize) }

    return boundsRelativeToUsableScreen
}

/*calculatePosition(
anchorBounds = IntRect(offset = CURSOR_POS, size = IntSize(1,1)),
windowSize = USABLE_SCREEN_SIZE,
layoutDirection = LocalLayoutDirection.current,
popupContentSize: CONTENT_SIZE,
)*/

//val state = rememberDialogState(size = DpSize.Unspecified)
//val density = LocalDensity.current
//val layoutDirection = LocalLayoutDirection.current
//
//// Currently this is "one shot", but could be adapted to use a listener on the Window if it was
//// necessary to get an updating value.
//val usableScreenBounds = remember(parentWindow) { DisplayUtil.getUsableScreenBounds(parentWindow) }
//
//remember(state.size, usableScreenBounds, anchorBoundsInScreen, popupPositionProvider) {
//    // Wait for measurement before placing and making visible
//    if (state.size == DpSize.Unspecified) return@remember
//
//    state.position = with(density) {
//        val origin = calculateBoundsRelativeToUsableScreen(
//            usableScreenBounds = usableScreenBounds,
//            anchorBoundsInScreen = anchorBoundsInScreen,
//            contentSize = state.size.roundToPx(density),
//            popupPositionProvider = popupPositionProvider,
//            layoutDirection = layoutDirection
//        ).translate(usableScreenBounds.topLeft).topLeft
//
//        WindowPosition(
//            x = origin.x.toDp(),
//            y = origin.y.toDp(),
//        )
//    }
//}
//
//DialogWindow(state = state, visible = state.size.isSpecified, ... )