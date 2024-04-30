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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@Stable
class BottomSheetState<T : Any>(
    val draggableState: AnchoredDraggableState<T>,
    val snackbarHostState: SnackbarHostState,
    val defineValues: BottomSheetValuesConfig<T>.() -> Unit,
    val density: Density
) {
    internal val onValuesRequested = mutableSetOf<(sheetFullHeight: Int) -> Unit>()

    var layoutHeight: Int by mutableIntStateOf(Int.MAX_VALUE)
        internal set

    var sheetFullHeight: Int by mutableIntStateOf(Int.MAX_VALUE)
        internal set

    val sheetVisibleHeight: Float by derivedStateOf {
        layoutHeight - offset
    }

    val offset: Float get() = draggableState.offset

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

    fun redefineValues() {
        if (sheetFullHeight == Int.MAX_VALUE) return
        onValuesRequested.forEach { call -> call(sheetFullHeight) }
    }

    companion object {

        fun <T : Any> Saver(
            defineValues: BottomSheetValuesConfig<T>.() -> Unit,
            snackbarHostState: SnackbarHostState,
            density: Density
        ) = Saver<BottomSheetState<T>, AnchoredDraggableState<T>>(
            save = { it.draggableState },
            restore = { draggableState ->
                BottomSheetState(draggableState, snackbarHostState, defineValues, density)
            }
        )
    }
}

@ExperimentalFoundationApi
val <T : Any> BottomSheetState<T>.sheetVisibleHeightDp: Dp
    get() = with(density) { sheetVisibleHeight.toDp() }

@ExperimentalFoundationApi
fun <T : Any> BottomSheetState<T>.requireSheetVisibleHeightDp(): Dp {
    return with(density) { requireSheetVisibleHeight().toDp() }
}

@ExperimentalMaterial3Api
@ExperimentalFoundationApi
@Composable
fun <T : Any> rememberAnchoredDraggableState(
    initialValue: T,
    positionalThreshold: (totalDistance: Float) -> Float = BottomSheetDefaults.PositionalThreshold,
    velocityThreshold: () -> Float = BottomSheetDefaults.VelocityThreshold,
    animationSpec: AnimationSpec<Float> = BottomSheetDefaults.AnimationSpec,
    confirmValueChange: (newValue: T) -> Boolean = { true }
) = rememberSaveable(
    positionalThreshold, velocityThreshold, animationSpec, confirmValueChange,
    saver = AnchoredDraggableState.Saver(
        animationSpec = animationSpec,
        positionalThreshold = positionalThreshold,
        velocityThreshold = velocityThreshold,
        confirmValueChange = confirmValueChange
    )
) {
    AnchoredDraggableState(
        initialValue = initialValue,
        positionalThreshold = positionalThreshold,
        velocityThreshold = velocityThreshold,
        animationSpec = animationSpec,
        confirmValueChange = confirmValueChange
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T : Any> rememberBottomSheetState(
    draggableState: AnchoredDraggableState<T>,
    defineValues: BottomSheetValuesConfig<T>.() -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
): BottomSheetState<T> {
    val density = LocalDensity.current

    return remember(draggableState, defineValues) {
        BottomSheetState(draggableState, snackbarHostState, defineValues, density)
    }
}

@ExperimentalMaterial3Api
@ExperimentalFoundationApi
@Composable
fun <T : Any> rememberBottomSheetState(
    initialValue: T,
    defineValues: BottomSheetValuesConfig<T>.() -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    positionalThreshold: (totalDistance: Float) -> Float = BottomSheetDefaults.PositionalThreshold,
    velocityThreshold: () -> Float = BottomSheetDefaults.VelocityThreshold,
    animationSpec: AnimationSpec<Float> = BottomSheetDefaults.AnimationSpec,
    confirmValueChange: BottomSheetState<T>.(newValue: T) -> Boolean = { true }
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

    state = rememberBottomSheetState(draggableState, defineValues, snackbarHostState)
    return state
}
