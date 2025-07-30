@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package io.morfly.bottomsheet.sample.bottomsheet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.morfly.bottomsheet.sample.bottomsheet.common.BottomSheetContent
import io.morfly.bottomsheet.sample.bottomsheet.common.MapScreenContent
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffold
import io.morfly.compose.bottomsheet.material3.layoutHeightDp
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetScaffoldState
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetState
import io.morfly.compose.bottomsheet.material3.requireSheetVisibleHeightDp

@Composable
fun CustomFinalizedDemoScreen(
    sheetAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    sheetPaddingHorizontal: Dp = 0.dp,
) {
    var isInitialState by rememberSaveable { mutableStateOf(true) }

    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        defineValues = {
            // Bottom sheet height is 100 dp.
            SheetValue.Collapsed at height(100.dp)
            if (isInitialState) {
                // Offset is 60% which means the bottom sheet takes 40% of the screen.
                SheetValue.PartiallyExpanded at offset(percent = 60)
            }
            // Bottom sheet height is equal to the height of its content.
            SheetValue.Expanded at contentHeight
        },
        confirmValueChange = {
            if (isInitialState) {
                isInitialState = false
                refreshValues()
            }
            true
        }
    )
    val scaffoldState = rememberBottomSheetScaffoldState(sheetState)

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            BottomSheetContent()
        },
        sheetAlignment = sheetAlignment,
        sheetPaddingHorizontal = sheetPaddingHorizontal,
        content = {
            val bottomPadding by remember {
                derivedStateOf { sheetState.requireSheetVisibleHeightDp() }
            }
            val isBottomSheetMoving by remember {
                derivedStateOf { sheetState.currentValue != sheetState.targetValue }
            }
            MapScreenContent(
                bottomPadding = bottomPadding,
                isBottomSheetMoving = isBottomSheetMoving,
                layoutHeight = sheetState.layoutHeightDp
            )
        },
        modifier = Modifier.fillMaxSize(),
    )
}

@Preview(name = "Portrait Mode", showBackground = true, device = Devices.PIXEL_7)
@Preview(
    name = "Landscape Mode",
    showBackground = true,
    heightDp = 360,
    widthDp = 800,
    device = "spec:width=360dp,height=800dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape",
)
@Composable
private fun Preview_AlignmentCenter() {
    MaterialTheme {
        CustomFinalizedDemoScreen()
    }
}

@Preview(
    name = "Landscape Mode",
    showBackground = true,
    heightDp = 360,
    widthDp = 800,
    device = "spec:width=360dp,height=800dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape",
)
@Composable
private fun Preview_AlignmentStart() {
    MaterialTheme {
        CustomFinalizedDemoScreen(
            sheetAlignment = Alignment.Start,
            sheetPaddingHorizontal = 16.dp,
        )
    }
}

@Preview(
    name = "Landscape Mode",
    showBackground = true,
    heightDp = 360,
    widthDp = 800,
    device = "spec:width=360dp,height=800dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape",
)
@Composable
private fun Preview_AlignmentEnd() {
    MaterialTheme {
        CustomFinalizedDemoScreen(
            sheetAlignment = Alignment.End,
            sheetPaddingHorizontal = 16.dp,
        )
    }
}