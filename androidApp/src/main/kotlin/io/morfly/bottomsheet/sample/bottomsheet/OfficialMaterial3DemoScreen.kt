@file:OptIn(ExperimentalMaterial3Api::class)

package io.morfly.bottomsheet.sample.bottomsheet

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import io.morfly.bottomsheet.sample.bottomsheet.common.BottomSheetContent
import io.morfly.bottomsheet.sample.bottomsheet.common.MapScreenContent
import kotlin.math.roundToInt

@Composable
fun OfficialMaterial3DemoScreen() {
    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded
    )
    val scaffoldState = rememberBottomSheetScaffoldState(sheetState)

    BoxWithConstraints {
        val layoutHeightPx = constraints.maxHeight

        val density = LocalDensity.current
        val bottomPadding by remember(layoutHeightPx) {
            derivedStateOf {
                val sheetOffsetPx = sheetState.requireOffset()
                val sheetVisibleHeightPx = layoutHeightPx - sheetOffsetPx
                with(density) { sheetVisibleHeightPx.roundToInt().toDp() }
            }
        }

        BottomSheetScaffold(
            sheetPeekHeight = 56.dp,
            scaffoldState = scaffoldState,
            sheetTonalElevation = 0.dp,
            sheetContent = {
                BottomSheetContent()
            },
            content = {
                val isMoving by remember {
                    derivedStateOf { sheetState.currentValue != sheetState.targetValue }
                }
                MapScreenContent(isBottomSheetMoving = isMoving, mapUiBottomPadding = bottomPadding)
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}