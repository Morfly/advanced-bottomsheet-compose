@file:OptIn(ExperimentalFoundationApi::class)

package io.morfly.composelayouts.bottomsheet.simplified

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import io.morfly.composelayouts.bottomsheet.BottomSheetContent
import io.morfly.composelayouts.bottomsheet.BottomSheetScreenBody
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private enum class SheetValue { Peek, PartiallyExpanded, Expanded }

@Composable
fun CustomSimplifiedBottomSheetDemo() {
    val state = remember {
        AnchoredDraggableState(
            initialValue = SheetValue.Peek,
            positionalThreshold = { 0f },
            velocityThreshold = { 0f },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium,
            ),
        )
    }

    BottomSheetScaffold(
        state = state,
        sheetContent = { BottomSheetContent() },
        content = { BottomSheetScreenBody() }
    )
}


@Composable
private fun BottomSheetScaffold(
    state: AnchoredDraggableState<SheetValue>,
    sheetContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    SubcomposeLayout { constraints ->
        val layoutWidth = constraints.maxWidth
        val layoutHeight = constraints.maxHeight

        val sheetPlaceable = subcompose(slotId = "sheet") {
            BottomSheet(
                state = state,
                layoutHeight = layoutHeight,
                sheetContent = sheetContent,
            )
        }[0].measure(constraints)

        val bodyPlaceable = subcompose(slotId = "body") {
            Surface(modifier) {
                content()
            }
        }[0].measure(constraints)

        layout(width = layoutWidth, height = layoutHeight) {
            val sheetOffsetY = state.requireOffset().roundToInt()
            val sheetOffsetX = 0

            bodyPlaceable.placeRelative(x = 0, y = 0)
            sheetPlaceable.placeRelative(sheetOffsetX, sheetOffsetY)
        }
    }
}

@Composable
private fun BottomSheet(
    state: AnchoredDraggableState<SheetValue>,
    layoutHeight: Int,
    sheetContent: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    Surface(
        shadowElevation = 1.dp,
        tonalElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .nestedScroll(
                remember(state) {
                    BottomSheetNestedScrollConnection(
                        anchoredDraggableState = state,
                        orientation = Orientation.Vertical,
                        onFling = { velocity ->
                            scope.launch { state.settle(velocity) }
                        }
                    )
                },
            )
            .anchoredDraggable(
                state = state,
                orientation = Orientation.Vertical
            )
            .onSizeChanged { layoutSize ->
                val sheetHeight = layoutSize.height
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
            },
    ) {
        sheetContent()
    }
}

/**
 * Copy of [androidx.compose.material3.ConsumeSwipeWithinBottomSheetBoundsNestedScrollConnection]
 */
@Suppress("FunctionName", "SameParameterValue")
private fun BottomSheetNestedScrollConnection(
    anchoredDraggableState: AnchoredDraggableState<SheetValue>,
    orientation: Orientation,
    onFling: (velocity: Float) -> Unit,
): NestedScrollConnection = object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.toFloat()
        return if (delta < 0 && source == NestedScrollSource.Drag) {
            anchoredDraggableState.dispatchRawDelta(delta).toOffset()
        } else {
            Offset.Zero
        }
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource,
    ): Offset {
        return if (source == NestedScrollSource.Drag) {
            anchoredDraggableState.dispatchRawDelta(available.toFloat()).toOffset()
        } else {
            Offset.Zero
        }
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        val toFling = available.toFloat()
        val currentOffset = anchoredDraggableState.requireOffset()
        val minAnchor = anchoredDraggableState.anchors.minAnchor()
        return if (toFling < 0 && currentOffset > minAnchor) {
            onFling(toFling)
            // since we go to the anchor with tween settling, consume all for the best UX
            available
        } else {
            Velocity.Zero
        }
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        onFling(available.toFloat())
        return available
    }

    private fun Float.toOffset(): Offset = Offset(
        x = if (orientation == Orientation.Horizontal) this else 0f,
        y = if (orientation == Orientation.Vertical) this else 0f,
    )

    @JvmName("velocityToFloat")
    private fun Velocity.toFloat() = if (orientation == Orientation.Horizontal) x else y

    @JvmName("offsetToFloat")
    private fun Offset.toFloat(): Float = if (orientation == Orientation.Horizontal) x else y
}