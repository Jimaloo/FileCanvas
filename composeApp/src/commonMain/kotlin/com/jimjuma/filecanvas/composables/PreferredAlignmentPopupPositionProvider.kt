package com.jimjuma.filecanvas.composables

import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.PopupPositionProvider

/**
 * PopupPositionProvider that will adjust anchor and alignment to keep the popup inside `coerceInsideBounds`, even
 * if that means moving it around or flipping the orientation.
 *
 * This gives similar behaviour to Apple's `NSPopover.showRelativeToRect:ofView:preferredEdge:`, but using Compose
 * Alignment semantics.
 */
class PreferredAlignmentPopupPositionProvider(
    private val preferredAnchor: BiasAlignment,
    private val preferredAlignment: BiasAlignment,
    private val preferredOffsetPx: (flippedHorizontally: Boolean, flippedVertically: Boolean) -> IntOffset,
): PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        val offset = preferredOffsetPx(false, false)
        val preferredBounds = calculateCoercedBounds(
            anchor = preferredAnchor,
            alignment = preferredAlignment,
            anchorBounds = anchorBounds,
            offsetPx = offset,
            windowSize = windowSize,
            layoutDirection = layoutDirection,
            popupContentSize = popupContentSize,
        )
        // The coerced bounds don't overlap the anchor element, so this is a suitable position.
        //
        // anchorBounds are translated to ensure that a deliberately overlapping offset (i.e. a negative one)
        // is not deemed overlapping.
        if (!preferredBounds.overlaps(anchorBounds.translate(offset))) {
            return preferredBounds.topLeft
        }
        // Does not fit container, default to preferredBounds to at least somewhat respect the layout intention
        if (popupContentSize != preferredBounds.size) {
            return  preferredBounds.topLeft
        }

        val horizontallyFlippedOffset = preferredOffsetPx(true, false)
        val horizontallyFlippedBounds = calculateCoercedBounds(
            anchor = preferredAnchor.flippedHorizontally,
            alignment = preferredAlignment.flippedHorizontally,
            anchorBounds = anchorBounds,
            offsetPx = horizontallyFlippedOffset,
            windowSize = windowSize,
            layoutDirection = layoutDirection,
            popupContentSize = popupContentSize,
        )
        if (!horizontallyFlippedBounds.overlaps(anchorBounds.translate(horizontallyFlippedOffset))) {
            return horizontallyFlippedBounds.topLeft
        }

        val verticallyFlippedOffset = preferredOffsetPx(false, true)
        val verticallyFlippedBounds = calculateCoercedBounds(
            anchor = preferredAnchor.flippedVertically,
            alignment = preferredAlignment.flippedVertically,
            anchorBounds = anchorBounds,
            offsetPx = verticallyFlippedOffset,
            windowSize = windowSize,
            layoutDirection = layoutDirection,
            popupContentSize = popupContentSize
        )
        if (!verticallyFlippedBounds.overlaps(anchorBounds.translate(verticallyFlippedOffset))) {
            return verticallyFlippedBounds.topLeft
        }

        // No way to avoid overlapping the content.
        return preferredBounds.topLeft
    }

    private fun calculateCoercedBounds(
        anchor: Alignment,
        alignment: Alignment,
        anchorBounds: IntRect,
        offsetPx: IntOffset,
        windowSize: IntSize,

        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntRect {
        val naivePosition = calculateNaivePosition(
            anchor = anchor,
            alignment = alignment,
            anchorBounds = anchorBounds,
            offsetPx = offsetPx,
            layoutDirection = layoutDirection,
            popupContentSize = popupContentSize,
        )
        return IntRect(offset = naivePosition, size = popupContentSize)
            .coercedInside(windowSize.toIntRect())
    }


    // Adapted from DesktopPopup.desktop.kt
    private fun calculateNaivePosition(
        anchor: Alignment,
        alignment: Alignment,
        anchorBounds: IntRect,
        offsetPx: IntOffset,

        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val anchorPoint = anchor.align(IntSize.Zero, anchorBounds.size, layoutDirection)
        val tooltipArea = IntRect(
            IntOffset(
                anchorBounds.left + anchorPoint.x - popupContentSize.width,
                anchorBounds.top + anchorPoint.y - popupContentSize.height,
            ),
            IntSize(
                popupContentSize.width * 2,
                popupContentSize.height * 2
            )
        )
        val position = alignment.align(popupContentSize, tooltipArea.size, layoutDirection)
        return tooltipArea.topLeft + position + offsetPx
    }

}

private fun IntRect.coercedInside(container: IntRect): IntRect {
    var result = copy()

    val excessWidth = (result.width - container.width).coerceAtLeast(0)
    val excessHeight = (result.height - container.height).coerceAtLeast(0)
    result = result.copy(
        right = result.right - excessWidth,
        bottom = result.bottom - excessHeight
    )

    val excessLeft = container.left - result.left
    val excessTop = container.top - result.top
    result = result.translate(
        translateX = excessLeft.coerceAtLeast(0),
        translateY = excessTop.coerceAtLeast(0),
    )

    val excessRight = result.right - container.right
    val excessBottom = result.bottom - container.bottom
    result = result.translate(
        translateX = -excessRight.coerceAtLeast(0),
        translateY = -excessBottom.coerceAtLeast(0),
    )

    return result
}

private val BiasAlignment.flippedHorizontally: BiasAlignment get() = copy(horizontalBias = -horizontalBias)
private val BiasAlignment.flippedVertically: BiasAlignment get() = copy(verticalBias = -verticalBias)