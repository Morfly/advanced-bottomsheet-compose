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
import androidx.compose.animation.core.SpringSpec
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api

/**
 * Default value for the [BottomSheetScaffold] positional threshold used by [AnchoredDraggableState]
 * internally.
 */
@ExperimentalMaterial3Api
val BottomSheetDefaults.PositionalThreshold: (totalDistance: Float) -> Float
    get() = { 0f }

/**
 * Default value for the [BottomSheetScaffold] velocity threshold used by [AnchoredDraggableState]
 * internally.
 */
@ExperimentalMaterial3Api
val BottomSheetDefaults.VelocityThreshold: () -> Float
    get() = { 0f }

/**
 * Default value for the [BottomSheetScaffold] animation spec used by [AnchoredDraggableState]
 * internally.
 */
@ExperimentalMaterial3Api
val BottomSheetDefaults.AnimationSpec: AnimationSpec<Float>
    get() = SpringSpec()
