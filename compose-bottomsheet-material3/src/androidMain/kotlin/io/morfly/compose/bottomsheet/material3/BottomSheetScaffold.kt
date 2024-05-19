/*
 * Copyright 2024 Pavlo Stavytskyi
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.morfly.compose.bottomsheet.material3

import androidx.annotation.IntRange
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * State of the [BottomSheetScaffold] composable.
 *
 * @param sheetState the state of the persistent bottom sheet
 * @param snackbarHostState the [SnackbarHostState] used to show snackbars inside the scaffold
 */
@ExperimentalFoundationApi
@Stable
class BottomSheetScaffoldState<T : Any>(
    val sheetState: BottomSheetState<T>,
    val snackbarHostState: SnackbarHostState,
)

/**
 * Create and [remember] a [BottomSheetScaffoldState].
 *
 * @param sheetState the state of the standard bottom sheet. See [rememberBottomSheetState]
 * @param snackbarHostState the [SnackbarHostState] used to show snackbars inside the scaffold
 */
@ExperimentalFoundationApi
@Composable
fun <T : Any> rememberBottomSheetScaffoldState(
    sheetState: BottomSheetState<T>,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
): BottomSheetScaffoldState<T> {
    return remember(sheetState, snackbarHostState) {
        BottomSheetScaffoldState(
            sheetState = sheetState,
            snackbarHostState = snackbarHostState
        )
    }
}

/**
 * <a href="https://m3.material.io/components/bottom-sheets/overview" class="external" target="_blank">Material Design standard bottom sheet scaffold</a>.
 *
 * Standard bottom sheets co-exist with the screen’s main UI region and allow for simultaneously
 * viewing and interacting with both regions. They are commonly used to keep a feature or
 * secondary content visible on screen when content in main UI region is frequently scrolled or
 * panned.
 *
 * This component provides API to put together several material components to construct your
 * screen, by ensuring proper layout strategy for them and collecting necessary data so these
 * components will work together correctly.
 *
 * @param scaffoldState the state of the bottom sheet scaffold
 * @param sheetContent the content of the bottom sheet
 * @param modifier the [Modifier] to be applied to this scaffold
 * @param sheetMaxWidth [Dp] that defines what the maximum width the sheet will take.
 * Pass in [Dp.Unspecified] for a sheet that spans the entire screen width.
 * @param sheetShape the shape of the bottom sheet
 * @param sheetContainerColor the background color of the bottom sheet
 * @param sheetContentColor the preferred content color provided by the bottom sheet to its
 * children. Defaults to the matching content color for [sheetContainerColor], or if that is
 * not a color from the theme, this will keep the same content color set above the bottom sheet.
 * @param sheetTonalElevation the tonal elevation of the bottom sheet
 * @param sheetShadowElevation the shadow elevation of the bottom sheet
 * @param sheetDragHandle optional visual marker to pull the scaffold's bottom sheet
 * @param sheetSwipeEnabled whether the sheet swiping is enabled and should react to the user's
 * input
 * @param topBar top app bar of the screen, typically a [SmallTopAppBar]
 * @param snackbarHost component to host [Snackbar]s that are pushed to be shown via
 * [SnackbarHostState.showSnackbar], typically a [SnackbarHost]
 * @param containerColor the color used for the background of this scaffold. Use [Color.Transparent]
 * to have no color.
 * @param contentColor the preferred color for content inside this scaffold. Defaults to either the
 * matching content color for [containerColor], or to the current [LocalContentColor] if
 * [containerColor] is not a color from the theme.
 * @param content content of the screen. The lambda receives a [PaddingValues] that should be
 * applied to the content root via [Modifier.padding] and [Modifier.consumeWindowInsets] to
 * properly offset top and bottom bars. If using [Modifier.verticalScroll], apply this modifier to
 * the child of the scroll, and not on the scroll itself.
 */
@ExperimentalMaterial3Api
@ExperimentalFoundationApi
@Composable
fun <T : Any> BottomSheetScaffold(
    scaffoldState: BottomSheetScaffoldState<T>,
    sheetContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    sheetMaxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,
    sheetShape: Shape = BottomSheetDefaults.ExpandedShape,
    sheetContainerColor: Color = BottomSheetDefaults.ContainerColor,
    sheetContentColor: Color = contentColorFor(sheetContainerColor),
    sheetTonalElevation: Dp = BottomSheetDefaults.Elevation,
    sheetShadowElevation: Dp = BottomSheetDefaults.Elevation,
    sheetDragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    sheetSwipeEnabled: Boolean = true,
    topBar: @Composable (() -> Unit)? = null,
    snackbarHost: @Composable (SnackbarHostState) -> Unit = { SnackbarHost(it) },
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(containerColor),
    content: @Composable (PaddingValues) -> Unit
) {
    val density = LocalDensity.current

    BottomSheetScaffoldLayout(
        modifier = modifier,
        topBar = topBar,
        body = content,
        snackbarHost = {
            snackbarHost(scaffoldState.snackbarHostState)
        },
        sheetOffset = { scaffoldState.sheetState.draggableState.requireOffset() },
        containerColor = containerColor,
        contentColor = contentColor,
        bottomSheet = { layoutHeight ->
            SideEffect {
                scaffoldState.sheetState.layoutHeight = layoutHeight
            }
            BottomSheet(
                state = scaffoldState.sheetState,
                sheetMaxWidth = sheetMaxWidth,
                sheetSwipeEnabled = sheetSwipeEnabled,
                calculateAnchors = { sheetFullHeight ->
                    val config = BottomSheetValuesConfig<T>(
                        layoutHeight = layoutHeight,
                        sheetFullHeight = sheetFullHeight,
                        density = density,
                    )
                    scaffoldState.sheetState.defineValues(config)
                    require(config.values.isNotEmpty()) { "No bottom sheet values provided!" }

                    DraggableAnchors {
                        for ((state, value) in config.values) {
                            state at value
                        }
                    }
                },
                shape = sheetShape,
                containerColor = sheetContainerColor,
                contentColor = sheetContentColor,
                tonalElevation = sheetTonalElevation,
                shadowElevation = sheetShadowElevation,
                dragHandle = sheetDragHandle,
                content = sheetContent
            )
        },
    )
}

@Composable
internal fun BottomSheetScaffoldLayout(
    modifier: Modifier,
    topBar: @Composable (() -> Unit)?,
    body: @Composable (innerPadding: PaddingValues) -> Unit,
    bottomSheet: @Composable (layoutHeight: Int) -> Unit,
    snackbarHost: @Composable () -> Unit,
    sheetOffset: () -> Float,
    containerColor: Color,
    contentColor: Color,
) {
    val density = LocalDensity.current
    SubcomposeLayout { constraints ->
        val layoutWidth = constraints.maxWidth
        val layoutHeight = constraints.maxHeight
        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

        val sheetPlaceable = subcompose(BottomSheetScaffoldLayoutSlot.Sheet) {
            bottomSheet(layoutHeight)
        }[0].measure(looseConstraints)

        val topBarPlaceable = topBar?.let {
            subcompose(BottomSheetScaffoldLayoutSlot.TopBar, topBar)[0]
                .measure(looseConstraints)
        }
        val topBarHeight = topBarPlaceable?.height ?: 0

        val bodyConstraints = looseConstraints.copy(maxHeight = layoutHeight)
        val bodyPlaceable = subcompose(BottomSheetScaffoldLayoutSlot.Body) {
            Surface(
                modifier = modifier,
                color = containerColor,
                contentColor = contentColor,
            ) { body(PaddingValues(top = with(density) { topBarHeight.toDp() })) }
        }[0].measure(bodyConstraints)

        val snackbarPlaceable = subcompose(BottomSheetScaffoldLayoutSlot.Snackbar, snackbarHost)[0]
            .measure(looseConstraints)

        layout(width = layoutWidth, height = layoutHeight) {
            val sheetOffsetY = sheetOffset().roundToInt()
            val sheetOffsetX = Integer.max(0, (layoutWidth - sheetPlaceable.width) / 2)

            val snackbarOffsetX = (layoutWidth - snackbarPlaceable.width) / 2

            val snackbarThreshold = minOf(snackbarPlaceable.height * 2f, layoutHeight * 0.3f)
            val snackbarOffsetY = if (layoutHeight - sheetOffsetY < snackbarThreshold) {
                sheetOffsetY - snackbarPlaceable.height
            } else {
                layoutHeight - snackbarPlaceable.height
            }

            // Placement order is important for elevation
            bodyPlaceable.placeRelative(0, 0)
            topBarPlaceable?.placeRelative(0, 0)
            sheetPlaceable.placeRelative(sheetOffsetX, sheetOffsetY)
            snackbarPlaceable.placeRelative(snackbarOffsetX, snackbarOffsetY)
        }
    }
}

private enum class BottomSheetScaffoldLayoutSlot { TopBar, Body, Sheet, Snackbar }

@ExperimentalFoundationApi
@Composable
internal fun <T : Any> BottomSheet(
    state: BottomSheetState<T>,
    calculateAnchors: (sheetFullHeight: Int) -> DraggableAnchors<T>,
    sheetMaxWidth: Dp,
    sheetSwipeEnabled: Boolean,
    shape: Shape,
    containerColor: Color,
    contentColor: Color,
    tonalElevation: Dp,
    shadowElevation: Dp,
    dragHandle: @Composable (() -> Unit)?,
    content: @Composable ColumnScope.() -> Unit
) {
    val scope = rememberCoroutineScope()
    val orientation = Orientation.Vertical

    DisposableEffect(state) {
        val onRefreshValues = fun(sheetFullHeight: Int, targetValue: T, animate: Boolean) {
            val newAnchors = calculateAnchors(sheetFullHeight)
            if (animate) {
                state.draggableState.updateAnchorsAnimated(scope, newAnchors, targetValue)
            } else {
                state.draggableState.updateAnchors(newAnchors, targetValue)
            }
        }

        state.onRefreshValues += onRefreshValues
        onDispose {
            state.onRefreshValues -= onRefreshValues
        }
    }

    Surface(
        modifier = Modifier
            .widthIn(max = sheetMaxWidth)
            .fillMaxWidth()
            .nestedScroll(
                remember(state) {
                    BottomSheetNestedScrollConnection(
                        draggableState = state.draggableState,
                        orientation = orientation,
                        onFling = { velocity ->
                            scope.launch { state.draggableState.settle(velocity) }
                        }
                    )
                },
            )
            .anchoredDraggable(
                state = state.draggableState,
                orientation = orientation,
                enabled = sheetSwipeEnabled,
            )
            .onSizeChanged { sheetFullSize ->
                state.sheetFullHeight = sheetFullSize.height
                val newAnchors = calculateAnchors(sheetFullSize.height)
                state.draggableState.updateAnchors(newAnchors, state.targetValue)
            },
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
    ) {
        Column(Modifier.fillMaxWidth()) {
            if (dragHandle != null) {
                Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    dragHandle()
                }
            }
            content()
        }
    }
}

@ExperimentalFoundationApi
@Suppress("FunctionName")
internal fun <T> BottomSheetNestedScrollConnection(
    draggableState: AnchoredDraggableState<T>,
    orientation: Orientation,
    onFling: (velocity: Float) -> Unit,
): NestedScrollConnection = object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.toFloat()
        return if (delta < 0 && source == NestedScrollSource.Drag) {
            draggableState.dispatchRawDelta(delta).toOffset()
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
            draggableState.dispatchRawDelta(available.toFloat()).toOffset()
        } else {
            Offset.Zero
        }
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        val toFling = available.toFloat()
        val currentOffset = draggableState.requireOffset()
        val minAnchor = draggableState.anchors.minAnchor()
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

/**
 * [BottomSheetValuesConfig] stores mutable configuration values for the bottom sheet comprised of
 * values of [T] and corresponding [Float] positions.
 *
 * @param layoutHeight the height of the scaffold containing the sheet
 * @param sheetFullHeight the full height of the content of the bottom sheet including an offscreen
 * part
 */
class BottomSheetValuesConfig<T : Any>(
    val layoutHeight: Int,
    val sheetFullHeight: Int,
    val density: Density,
) {
    /**
     * The height of the bottom sheet content.
     */
    val contentHeight = Value((layoutHeight - sheetFullHeight).toFloat())

    /**
     * Collection of bottom sheet values and corresponding offsets in pixels.
     */
    val values = mutableMapOf<T, Float>()

    /**
     * Set the bottom sheet value.
     * @param value the bottom sheet value.
     */
    infix fun T.at(value: Value) {
        values[this] = maxOf(value.offsetPx, contentHeight.offsetPx)
    }

    /**
     * Define bottom sheet value using its offset from the top in pixels.
     * @param px the bottom sheet offset in pixels
     */
    fun offset(px: Float): Value {
        return Value(px)
    }

    /**
     * Define bottom sheet value using its offset from the top in dp.
     * @param dp the bottom sheet offset in pixels
     */
    fun offset(dp: Dp): Value {
        return Value(with(density) { dp.toPx() })
    }

    /**
     * Define bottom sheet value using its offset from the top as a percentage of the layout's
     * height.
     * @param percent the bottom sheet height in percent
     */
    fun offset(@IntRange(from = 0, to = 100) percent: Int): Value {
        return Value(layoutHeight * percent / 100f)
    }

    /**
     * Define bottom sheet value using its height in pixels.
     * @param px the bottom sheet height in pixels
     */
    fun height(px: Float): Value {
        return Value(layoutHeight - offset(px).offsetPx)
    }

    /**
     * Define bottom sheet value using its height in dp.
     * @param dp the bottom sheet height in dp
     */
    fun height(dp: Dp): Value {
        return Value(layoutHeight - offset(dp).offsetPx)
    }

    /**
     * Define bottom sheet value using its height as a percentage of the layout's height.
     * @param percent the bottom sheet height in percent
     */
    fun height(@IntRange(from = 0, to = 100) percent: Int): Value {
        return Value(layoutHeight - offset(percent).offsetPx)
    }

    /**
     * Holder of bottom sheet values. Both [offset] and [height] are represented as offset in
     * pixels.
     * @param offsetPx the bottom sheet offset in pixels
     */
    @JvmInline
    value class Value internal constructor(val offsetPx: Float)
}
