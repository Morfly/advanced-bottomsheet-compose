@file:OptIn(ExperimentalFoundationApi::class)

package io.morfly.bottomsheet.sample.bottomsheet.draggable

import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.morfly.bottomsheet.sample.bottomsheet.BottomSheetContent
import io.morfly.bottomsheet.sample.bottomsheet.MapScreenContent

enum class SheetValue { Peek, PartiallyExpanded, Expanded }

@Composable
fun CustomDraggableDemoScreen() {
    val density = LocalDensity.current

    val state = remember {
        AnchoredDraggableState(
            initialValue = SheetValue.PartiallyExpanded,
            positionalThreshold = { 0f },
            velocityThreshold = { 0f },
            animationSpec = SpringSpec(),
        )
    }

    BoxWithConstraints {
        val layoutHeight = constraints.maxHeight

        MapScreenContent(mapUiBottomPadding = 0.dp)

        Surface(
            modifier = Modifier
                .offset {
                    val sheetOffsetY = state.requireOffset()
                    IntOffset(x = 0, y = sheetOffsetY.toInt())
                }
                .anchoredDraggable(
                    state = state,
                    orientation = Orientation.Vertical,
                )
                .onSizeChanged { sheetSize ->
                    val sheetHeight = sheetSize.height
                    val newAnchors = DraggableAnchors {
                        with(density) {
                            // Bottom sheet height is 56 dp.
                            SheetValue.Peek at (layoutHeight - 56.dp.toPx())
                            // Offset is 60% which means the bottom sheet takes 40% of the screen.
                            SheetValue.PartiallyExpanded at (layoutHeight * 0.6f)
                            // Bottom sheet height is equal to the height of its content.
                            // If the height of the content is bigger than the screen - fill the entire screen.
                            SheetValue.Expanded at maxOf(layoutHeight - sheetHeight, 0).toFloat()
                        }
                    }
                    state.updateAnchors(newAnchors, state.targetValue)
                }
        ) {
            BottomSheetContent(userScrollEnabled = false)
        }
    }
}