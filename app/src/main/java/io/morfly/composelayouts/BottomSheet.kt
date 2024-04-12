package io.morfly.composelayouts

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class DragValue {
    End,
    Center,
    Start,
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomSheet() {
    val density = LocalDensity.current
    val screenHeightDp = LocalConfiguration.current.screenHeightDp.dp
    val screenHeightPx = with(density) { screenHeightDp.toPx() }

    val anchors = remember {
        DraggableAnchors {
            DragValue.Start at screenHeightPx - 400f
            DragValue.Center at screenHeightPx - 1000f
            DragValue.End at 100f
        }
    }
    val state = remember {
        AnchoredDraggableState(
            initialValue = DragValue.End,
            positionalThreshold = { 0f },
            velocityThreshold = { 0f },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium,
            ),
        ).apply {
            updateAnchors(anchors)
        }
    }

    SubcomposeLayout { constraints ->
        layout(
            width = constraints.maxWidth,
            height = constraints.maxHeight
        ) {

        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.LightGray),
    ) {

        BottomSheetView(state)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomSheetView(
    state: AnchoredDraggableState<DragValue>
) {
    val screenSizeDp = LocalConfiguration.current.screenWidthDp.dp
    val scope = rememberCoroutineScope()

    Box(
        Modifier
            .offset {
                IntOffset(
                    x = 0,
                    y = state
                        .requireOffset()
                        .roundToInt(),
                )
            }
            .width(screenSizeDp)
            .background(Color.DarkGray),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(
                    remember(state) {
                        CustomNestedScroll(state, Orientation.Vertical) {
                            scope.launch { state.settle(it) }
                        }
                    },
                )
                .anchoredDraggable(state, Orientation.Vertical),
        ) {
            LazyColumn(
                userScrollEnabled = true,
                modifier = Modifier.fillMaxWidth(),
            ) {
                for (i in 0..17) {
                    item {
                        Text(text = "Test_$i")
                    }
                }
            }
        }
    }
}