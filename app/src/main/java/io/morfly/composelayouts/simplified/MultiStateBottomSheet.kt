@file:OptIn(ExperimentalFoundationApi::class)

package io.morfly.composelayouts.simplified

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

enum class BottomSheetValue {
    Expanded,
    PartiallyExpanded,
    Collapsed,
}

@Composable
fun MultiStateBottomSheet(
    sheetContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    val state = remember {
        AnchoredDraggableState(
            initialValue = BottomSheetValue.PartiallyExpanded,
            positionalThreshold = { 0f },
            velocityThreshold = { 0f },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium,
            ),
        )
    }

    SubcomposeLayout { constraints ->
        val layoutWidth = constraints.maxWidth
        val layoutHeight = constraints.maxHeight

        val sheetPlaceable = subcompose("sheet") {
            MultiStateBottomSheetContent(
                layoutHeight = layoutHeight,
                state = state,
                sheetContent = sheetContent,
            )
        }[0].measure(constraints)

        val bodyPlaceable = subcompose("body") {
            Surface(modifier = modifier) {
                content(PaddingValues())
            }
        }[0].measure(constraints)

        layout(width = layoutWidth, height = layoutHeight) {
            val sheetOffsetY = state.requireOffset().roundToInt()
            val sheetOffsetX = Integer.max(0, (layoutWidth - sheetPlaceable.width) / 2)

            bodyPlaceable.placeRelative(x = 0, y = 0)
            sheetPlaceable.placeRelative(sheetOffsetX, sheetOffsetY)
        }
    }
}

@Composable
internal fun MultiStateBottomSheetContent(
    layoutHeight: Int,
    state: AnchoredDraggableState<BottomSheetValue>,
    sheetContent: @Composable ColumnScope.() -> Unit,
) {
    val scope = rememberCoroutineScope()

    val zeroRadius = CornerSize(0)
    Surface(
        shape = RoundedCornerShape(
            topStart = MaterialTheme.shapes.extraLarge.topStart,
            topEnd = MaterialTheme.shapes.extraLarge.topEnd,
            bottomStart = zeroRadius,
            bottomEnd = zeroRadius
        ),
        modifier = Modifier
            .widthIn(max = 640.dp)
            .fillMaxWidth()
//            .nestedScroll(
//                remember(state) {
//                    BottomSheetNestedScrollConnection(state, Orientation.Vertical) {
//                        scope.launch { state.settle(it) }
//                    }
//                },
//            )
            .anchoredDraggable(state, Orientation.Vertical)
            .onSizeChanged { layoutSize ->
                val sheetHeight = layoutSize.height
                val newAnchors = DraggableAnchors {
                    BottomSheetValue.Collapsed at (layoutHeight * 0.8f)
                    BottomSheetValue.PartiallyExpanded at maxOf(layoutHeight * 0.6f, 0f)
                    BottomSheetValue.Expanded at maxOf(layoutHeight - sheetHeight, 0).toFloat()
                }
                state.updateAnchors(newAnchors, state.targetValue)
            },
    ) {
        Column(Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                DragHandle()
            }
            sheetContent()
        }
    }
}

@Composable
fun DragHandle() {
    Surface(
        modifier = Modifier.padding(vertical = 22.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Box(modifier = Modifier.size(width = 32.dp, height = 4.dp))
    }
}