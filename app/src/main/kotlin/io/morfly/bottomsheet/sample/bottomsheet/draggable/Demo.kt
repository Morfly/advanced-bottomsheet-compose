package io.morfly.bottomsheet.sample.bottomsheet.draggable

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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
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

    val screenSizeDp = LocalConfiguration.current.screenWidthDp.dp

    val scope = rememberCoroutineScope()

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

//    Box(
//        Modifier
//            .fillMaxSize()
//            .background(Color.LightGray),
//    ) {

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
                    .anchoredDraggable(state, Orientation.Vertical),
            ) {
                LazyColumn(
                    userScrollEnabled = true,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    for (i in 0..2) {
                        item {
                            Text(text = "Test_$i")
                        }
                    }
                }
            }
//        }
    }
}