@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package io.morfly.composelayouts

import androidx.compose.animation.core.AnimationSpec
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Stable
class BottomSheetState<T : Any>(
    val draggableState: AnchoredDraggableState<T>,
) {
    var layoutSize: IntSize? = null
        internal set

    internal val onStatesRequested = mutableSetOf<(layoutSize: IntSize) -> Unit>()

    fun redefineStates() {
        val size = layoutSize ?: return
        onStatesRequested.forEach { call -> call(size) }
    }
}

@Composable
fun <T : Any> rememberBottomSheetState(
    draggableState: AnchoredDraggableState<T>,
) = remember(draggableState) {
    BottomSheetState(draggableState)
}

@Composable
fun <T : Any> rememberAnchoredDraggableState(
    initialValue: T,
    positionalThreshold: (totalDistance: Float) -> Float = { 0f },
    velocityThreshold: () -> Float = { 0f },
    animationSpec: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium,
    ),
    confirmValueChange: (newValue: T) -> Boolean = { true }
) = remember(positionalThreshold, velocityThreshold, animationSpec, confirmValueChange) {
    AnchoredDraggableState(
        initialValue = initialValue,
        positionalThreshold = positionalThreshold,
        velocityThreshold = velocityThreshold,
        animationSpec = animationSpec,
        confirmValueChange = confirmValueChange
    )
}

@Composable
fun BottomSheetScaffoldDemo() {
    var isInitialState by remember { mutableStateOf(true) }
    var counter by remember { mutableIntStateOf(0) }

    var state: BottomSheetState<DragValue>? = null
    val draggableState = rememberAnchoredDraggableState(
        initialValue = DragValue.Center,
        confirmValueChange = {
            println("TTAGG dragValue: $it")
            counter++
            if (counter == 2) {
                isInitialState = false
                state?.redefineStates()
                true
            } else true
        }
    )
    state = rememberBottomSheetState(draggableState = draggableState)

    BottomSheetScaffold(
        sheetState = state,
        defineStates = {
            DragValue.Start at height(200.dp)
            if (isInitialState) {
                DragValue.Center at height(percent = 0.5f)
            }
            DragValue.End at contentHeight
        },
        sheetContent = {
            LazyColumn(
                userScrollEnabled = true,
                modifier = Modifier.fillMaxWidth(),
            ) {
                for (i in 0..25) {
                    item {
                        Text(text = "Test_$i")
                    }
                }
            }
        },
        content = {

        }
    )
}

@Composable
fun <T : Any> BottomSheetScaffold(
    sheetState: BottomSheetState<T>,
    defineStates: BottomSheetStateConfig<T>.() -> Unit,
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
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(containerColor),
    content: @Composable (PaddingValues) -> Unit
) {
    val density = LocalDensity.current

    BottomSheetScaffoldLayout(
        modifier = modifier,
        body = content,
        sheetOffset = { sheetState.draggableState.requireOffset() },
        containerColor = containerColor,
        contentColor = contentColor,
        bottomSheet = { layoutHeight ->
            BottomSheet(
                state = sheetState.draggableState,
                sheetState = sheetState,
                sheetMaxWidth = sheetMaxWidth,
                sheetSwipeEnabled = sheetSwipeEnabled,
                calculateAnchors = { sheetSize ->
                    val config = BottomSheetStateConfig<T>(
                        layoutHeight = layoutHeight,
                        sheetHeight = sheetSize.height,
                        density = density,
                    )
                    config.defineStates()
                    require(config.states.isNotEmpty()) { "No bottom sheet states provided!" }

                    DraggableAnchors {
                        for ((state, value) in config.states) {
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
    body: @Composable (innerPadding: PaddingValues) -> Unit,
    bottomSheet: @Composable (layoutHeight: Int) -> Unit,
    sheetOffset: () -> Float,
    containerColor: Color,
    contentColor: Color,
) {
    SubcomposeLayout { constraints ->
        val layoutWidth = constraints.maxWidth
        val layoutHeight = constraints.maxHeight

        val sheetPlaceable = subcompose(BottomSheetScaffoldLayoutSlot.Sheet) {
            bottomSheet(layoutHeight)
        }[0].measure(constraints)

        val bodyPlaceable = subcompose(BottomSheetScaffoldLayoutSlot.Body) {
            Surface(
                modifier = modifier,
                color = containerColor,
                contentColor = contentColor,
            ) { body(PaddingValues()) }
        }[0].measure(constraints)

        layout(width = layoutWidth, height = layoutHeight) {
            val sheetOffsetY = sheetOffset().roundToInt()
            val sheetOffsetX = Integer.max(0, (layoutWidth - sheetPlaceable.width) / 2)

            bodyPlaceable.placeRelative(x = 0, y = 0)
            sheetPlaceable.placeRelative(sheetOffsetX, sheetOffsetY)

            println("TTAGG layout")
        }
    }
}

private enum class BottomSheetScaffoldLayoutSlot { Body, Sheet }

@Composable
internal fun <T : Any> BottomSheet(
    state: AnchoredDraggableState<T>,
    sheetState: BottomSheetState<T>,
    calculateAnchors: (sheetSize: IntSize) -> DraggableAnchors<T>,
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

    fun updateAnchors(layoutSize: IntSize) {
        val newAnchors = calculateAnchors(layoutSize)
        state.updateAnchors(newAnchors, state.targetValue)
    }

    DisposableEffect(sheetState) {
        val onStatesRequested = ::updateAnchors
        sheetState.onStatesRequested += onStatesRequested
        onDispose {
            sheetState.onStatesRequested -= onStatesRequested
        }
    }

    Surface(
        modifier = Modifier
            .widthIn(max = sheetMaxWidth)
            .fillMaxWidth()
            .nestedScroll(
                remember(state) {
                    BottomSheetNestedScrollConnection(state, orientation) {
                        scope.launch { state.settle(it) }
                    }
                },
            )
            .anchoredDraggable(
                state = state,
                orientation = orientation,
                enabled = sheetSwipeEnabled,
            )
            .onSizeChanged { layoutSize ->
                println("TTAGG onSizeChanged")
                sheetState.layoutSize = layoutSize

                val newAnchors = calculateAnchors(layoutSize)
                state.updateAnchors(newAnchors, state.targetValue)
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

@Suppress("FunctionName")
internal fun <T> BottomSheetNestedScrollConnection(
    anchoredDraggableState: AnchoredDraggableState<T>,
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

class BottomSheetStateConfig<T : Any>(
    val layoutHeight: Int,
    val sheetHeight: Int,
    val density: Density,
) {
    val contentHeight: Float = (layoutHeight - sheetHeight).toFloat()

    val states = mutableMapOf<T, Float>()

    infix fun T.at(offsetPx: Float) {
        states[this] = maxOf(offsetPx, 0f)
    }

    fun offset(px: Int): Float {
        return px.toFloat()
    }

    fun offset(value: Dp): Float {
        return with(density) { value.toPx() }
    }

    fun offset(percent: Float): Float {
        return layoutHeight * percent
    }

    fun height(px: Int): Float {
        return layoutHeight - offset(px)
    }

    fun height(value: Dp): Float {
        return layoutHeight - offset(value)
    }

    fun height(percent: Float): Float {
        return layoutHeight - offset(percent)
    }
}
