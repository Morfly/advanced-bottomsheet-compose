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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable

@ExperimentalFoundationApi
suspend fun <T> AnchoredDraggableState<T>.updateAnchorsAnimated(
    newAnchors: DraggableAnchors<T>,
    newTarget: T = if (!offset.isNaN()) {
        newAnchors.closestAnchor(offset) ?: targetValue
    } else targetValue
) {
    if (anchors != newAnchors) {
        anchors = newAnchors

        animateTo(newTarget)
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
    confirmValueChange: (T) -> Boolean = { true }
) = rememberSaveable(
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