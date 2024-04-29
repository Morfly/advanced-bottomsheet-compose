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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@Stable
class BottomSheetState<T : Any>(
    val draggableState: AnchoredDraggableState<T>,
    val snackbarHostState: SnackbarHostState,
    val defineValues: BottomSheetValuesConfig<T>.() -> Unit,
) {
    internal val onValuesRequested = mutableSetOf<(sheetSize: IntSize) -> Unit>()

    var sheetSize: IntSize? = null // TODO make mutableState
        internal set
    var sheetOffset: Offset? = null
        internal set

    fun requireSheetSize() = sheetSize!!

    fun requireSheetOffset() = sheetOffset!!

    fun redefineValues() {
        val size = sheetSize ?: return
        onValuesRequested.forEach { call -> call(size) }
    }

    companion object {

        fun <T : Any> Saver(
            defineValues: BottomSheetValuesConfig<T>.() -> Unit,
            snackbarHostState: SnackbarHostState,
        ) = Saver<BottomSheetState<T>, AnchoredDraggableState<T>>(
            save = { it.draggableState },
            restore = { draggableState ->
                BottomSheetState(draggableState, snackbarHostState, defineValues)
            }
        )
    }
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
) = remember(draggableState, defineValues) {
    BottomSheetState(draggableState, snackbarHostState, defineValues)
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
