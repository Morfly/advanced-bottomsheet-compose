package io.morfly.compose.bottomsheet.material3

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Stable

@ExperimentalMaterial3Api
@Stable
val BottomSheetDefaults.PositionalThreshold: (totalDistance: Float) -> Float
    get() = { 0f }

@ExperimentalMaterial3Api
@Stable
val BottomSheetDefaults.VelocityThreshold: () -> Float
    get() = { 0f }

@ExperimentalMaterial3Api
@Stable
val BottomSheetDefaults.AnimationSpec: AnimationSpec<Float>
    get() = SpringSpec()
