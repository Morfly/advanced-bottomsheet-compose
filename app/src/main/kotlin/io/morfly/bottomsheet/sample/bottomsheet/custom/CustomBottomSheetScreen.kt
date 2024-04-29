@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package io.morfly.bottomsheet.sample.bottomsheet.custom

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import io.morfly.bottomsheet.sample.bottomsheet.BottomSheetContent
import io.morfly.bottomsheet.sample.bottomsheet.MapScreenContent
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffold
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetState

enum class SheetValue { Peek, PartiallyExpanded, Expanded }

@Composable
fun CustomBottomSheetScreen() {
    var isInitialState by rememberSaveable { mutableStateOf(true) }

    val state = rememberBottomSheetState(
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
                redefineValues()
            }
            true
        }
    )

    var bottomPadding by remember { mutableStateOf(0.dp) }

    BottomSheetScaffold(
        sheetState = state,
        onSheetMoved = { sheetHeight ->
            bottomPadding = sheetHeight
        },
        sheetContent = {
            BottomSheetContent()
        },
        content = {
            MapScreenContent(bottomPadding)
        }
    )
}