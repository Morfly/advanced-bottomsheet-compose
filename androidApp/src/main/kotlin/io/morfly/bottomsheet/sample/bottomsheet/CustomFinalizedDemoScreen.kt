@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package io.morfly.bottomsheet.sample.bottomsheet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.morfly.bottomsheet.sample.bottomsheet.common.BottomSheetContent
import io.morfly.bottomsheet.sample.bottomsheet.common.MapScreenContent
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffold
import io.morfly.compose.bottomsheet.material3.layoutHeightDp
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetScaffoldState
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetState
import io.morfly.compose.bottomsheet.material3.sheetVisibleHeightDp

@Composable
fun CustomFinalizedDemoScreen() {
    var isInitialState by rememberSaveable { mutableStateOf(true) }

    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        defineValues = {
            SheetValue.Peek at height(56.dp)
            if (isInitialState) {
                SheetValue.PartiallyExpanded at height(percent = 40)
            }
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
        sheetTonalElevation = 0.dp,
        sheetContent = {
            BottomSheetContent()
        },
        content = {
            val bottomPadding by remember {
                derivedStateOf { sheetState.sheetVisibleHeightDp }
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