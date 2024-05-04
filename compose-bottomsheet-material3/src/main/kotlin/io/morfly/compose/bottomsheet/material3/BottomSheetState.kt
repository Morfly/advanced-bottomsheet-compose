/*
 * Copyright 2024 Pavlo Stavytskyi
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

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@Stable
class BottomSheetState<T : Any>(
    val draggableState: AnchoredDraggableState<T>,
    internal val defineValues: BottomSheetValuesConfig<T>.() -> Unit,
    internal val density: Density
) {
    internal val onRefreshValues = mutableSetOf<(sheetFullHeight: Int, targetValue: T) -> Unit>()

    val values: DraggableAnchors<T> get() = draggableState.anchors

    var layoutHeight: Int by mutableIntStateOf(Int.MAX_VALUE)
        internal set

    var sheetFullHeight: Int by mutableIntStateOf(Int.MAX_VALUE)
        internal set

    val sheetVisibleHeight: Float by derivedStateOf {
        layoutHeight - offset
    }

    val offset: Float get() = draggableState.offset

    val currentValue: T get() = draggableState.currentValue

    val targetValue: T get() = draggableState.targetValue

    fun requireLayoutHeight(): Int {
        check(layoutHeight != Int.MAX_VALUE) {
            "The layoutHeight was read before being initialized. Did you access the " +
                    "layoutHeight in a phase before layout, like effects or composition?"
        }
        return layoutHeight
    }

    fun requireSheetFullHeight(): Int {
        check(sheetFullHeight != Int.MAX_VALUE) {
            "The sheetFullHeight was read before being initialized. Did you access the " +
                    "sheetFullHeight in a phase before layout, like effects or composition?"
        }
        return sheetFullHeight
    }

    fun requireSheetVisibleHeight(): Float {
        check(!sheetVisibleHeight.isNaN()) {
            "The sheetVisibleHeight was read before being initialized. Did you access the " +
                    "sheetVisibleHeight in a phase before layout, like effects or composition?"
        }
        return sheetVisibleHeight
    }

    fun requireOffset() = draggableState.requireOffset()

    fun refreshValues(targetValue: T = this.targetValue) {
        if (sheetFullHeight != Int.MAX_VALUE) {
            onRefreshValues.forEach { call -> call(sheetFullHeight, targetValue) }
        }
    }

    suspend fun animateTo(
        targetValue: T,
        velocity: Float = draggableState.lastVelocity,
    ) = draggableState.animateTo(targetValue, velocity)

    suspend fun snapTo(
        targetValue: T
    ) = draggableState.snapTo(targetValue)

    companion object {

        fun <T : Any> Saver(
            defineValues: BottomSheetValuesConfig<T>.() -> Unit,
            density: Density
        ) = Saver<BottomSheetState<T>, AnchoredDraggableState<T>>(
            save = { it.draggableState },
            restore = { draggableState ->
                BottomSheetState(draggableState, defineValues, density)
            }
        )
    }
}

@ExperimentalFoundationApi
val <T : Any> BottomSheetState<T>.layoutHeightDp: Dp
    get() = with(density) { layoutHeight.toDp() }

@ExperimentalFoundationApi
val <T : Any> BottomSheetState<T>.sheetFullHeightDp: Dp
    get() = with(density) { sheetFullHeight.toDp() }

@ExperimentalFoundationApi
val <T : Any> BottomSheetState<T>.sheetVisibleHeightDp: Dp
    get() = with(density) { sheetVisibleHeight.toDp() }

@ExperimentalFoundationApi
val <T : Any> BottomSheetState<T>.offsetDp: Dp
    get() = with(density) { offset.toDp() }

@ExperimentalFoundationApi
fun <T : Any> BottomSheetState<T>.requireLayoutHeightDp(): Dp {
    return with(density) { requireLayoutHeight().toDp() }
}

@ExperimentalFoundationApi
fun <T : Any> BottomSheetState<T>.requireSheetFullHeightDp(): Dp {
    return with(density) { requireSheetFullHeight().toDp() }
}

@ExperimentalFoundationApi
fun <T : Any> BottomSheetState<T>.requireSheetVisibleHeightDp(): Dp {
    return with(density) { requireSheetVisibleHeight().toDp() }
}

@ExperimentalFoundationApi
fun <T : Any> BottomSheetState<T>.requireOffsetDp(): Dp {
    return with(density) { requireOffset().toDp() }
}

@ExperimentalMaterial3Api
@ExperimentalFoundationApi
@Composable
fun <T : Any> rememberBottomSheetState(
    initialValue: T,
    defineValues: BottomSheetValuesConfig<T>.() -> Unit,
    positionalThreshold: (totalDistance: Float) -> Float = BottomSheetDefaults.PositionalThreshold,
    velocityThreshold: () -> Float = BottomSheetDefaults.VelocityThreshold,
    animationSpec: AnimationSpec<Float> = BottomSheetDefaults.AnimationSpec,
    confirmValueChange: BottomSheetState<T>.(T) -> Boolean = { true }
): BottomSheetState<T> {
    lateinit var state: BottomSheetState<T>
    lateinit var draggableState: AnchoredDraggableState<T>

    val scope = rememberCoroutineScope()
    var prevOffset by remember { mutableFloatStateOf(0f) }

    draggableState = rememberAnchoredDraggableState(
        initialValue = initialValue,
        positionalThreshold = positionalThreshold,
        velocityThreshold = velocityThreshold,
        animationSpec = animationSpec,
        confirmValueChange = { value ->
            with(draggableState) {
                val currentOffset = requireOffset()
                val searchUpwards =
                    if (prevOffset == currentOffset) null
                    else prevOffset < currentOffset

                prevOffset = currentOffset
                if (!anchors.hasAnchorFor(value)) {
                    val closest = if (searchUpwards != null) {
                        anchors.closestAnchor(currentOffset, searchUpwards)
                    } else {
                        anchors.closestAnchor(currentOffset)
                    }
                    if (closest != null) {
                        scope.launch { animateTo(closest) }
                    }
                    false
                } else {
                    state.confirmValueChange(value)
                }
            }
        }
    )

    state = rememberBottomSheetState(draggableState, defineValues)
    return state
}

@ExperimentalFoundationApi
@Composable
internal fun <T : Any> rememberBottomSheetState(
    draggableState: AnchoredDraggableState<T>,
    defineValues: BottomSheetValuesConfig<T>.() -> Unit,
): BottomSheetState<T> {
    val density = LocalDensity.current

    return remember(draggableState) {
        BottomSheetState(draggableState, defineValues, density)
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
fun rememberStandardBottomSheetState(
    initialValue: SheetValue = SheetValue.PartiallyExpanded,
    skipPartiallyExpandedState: Boolean = false,
    peekHeight: Dp = BottomSheetDefaults.SheetPeekHeight,
    skipHiddenState: Boolean = true,
    confirmValueChange: BottomSheetState<SheetValue>.(SheetValue) -> Boolean = { true },
): BottomSheetState<SheetValue> {
    return rememberBottomSheetState(
        initialValue = initialValue,
        defineValues = {
            if (skipPartiallyExpandedState) {
                require(initialValue != SheetValue.PartiallyExpanded) {
                    "The initial value must not be set to PartiallyExpanded if " +
                            "skipPartiallyExpanded is set to true."
                }
            }
            if (skipHiddenState) {
                require(initialValue != SheetValue.Hidden) {
                    "The initial value must not be set to Hidden if skipHiddenState is set to true."
                }
            }

            if (!skipPartiallyExpandedState) {
                SheetValue.PartiallyExpanded at height(peekHeight)
            }
            if (!skipHiddenState) {
                SheetValue.Hidden at height(0.dp)
            }
            SheetValue.Expanded at contentHeight
        },
        confirmValueChange = confirmValueChange
    )
}
