@file:OptIn(ExperimentalFoundationApi::class)

package io.morfly.bottomsheet.sample.bottomsheet.draggable

import androidx.compose.animation.core.SpringSpec
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.morfly.bottomsheet.sample.bottomsheet.MapScreenContent

enum class SheetValue { Peek, PartiallyExpanded, Expanded }

@Composable
fun CustomDraggableScreen() {
    val density = LocalDensity.current
//    val screenHeightDp = LocalConfiguration.current.screenHeightDp.dp
//    val screenHeightPx = with(density) { screenHeightDp.toPx() }

    val anchors = remember {
        DraggableAnchors {
            with(density) {
                SheetValue.Peek at 700.dp.toPx()
                SheetValue.PartiallyExpanded at 400.dp.toPx()
                SheetValue.Expanded at 56.dp.toPx()
            }
        }
    }
    val state = remember {
        AnchoredDraggableState(
            initialValue = SheetValue.Expanded,
            anchors = anchors,
            positionalThreshold = { 0f },
            velocityThreshold = { 0f },
            animationSpec = SpringSpec(),
        )
    }

    Box(
        modifier = Modifier
            .background(Color.Transparent)
    ) {
        MapScreenContent(mapUiBottomPadding = 0.dp)

        Surface(
            shadowElevation = 16.dp,
            tonalElevation = 16.dp,
            color = MaterialTheme.colorScheme.surface,
            contentColor = contentColorFor(MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxSize()
                .offset {
                    IntOffset(
                        x = 0,
                        y = state
                            .requireOffset()
                            .toInt()
                    )
                }
                .anchoredDraggable(
                    state = state,
                    orientation = Orientation.Vertical,
                )
        ) {
            LazyColumn(
                userScrollEnabled = false,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                for (i in 0..2) {
                    item {
                        Text(text = "Test_$i")
                    }
                }
            }
        }
    }
}