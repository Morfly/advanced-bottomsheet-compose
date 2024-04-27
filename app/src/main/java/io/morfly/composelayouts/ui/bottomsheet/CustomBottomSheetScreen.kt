@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package io.morfly.composelayouts.ui.bottomsheet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffold
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetState
import io.morfly.composelayouts.DragValue
import io.morfly.composelayouts.ui.BottomSheetContent
import io.morfly.composelayouts.ui.BottomSheetScreenBody

@Composable
fun CustomBottomSheetScreen() {
    var isInitialState by rememberSaveable { mutableStateOf(true) }

    val state = rememberBottomSheetState(
        initialValue = DragValue.Center,
        defineValues = {
            DragValue.Start at height(56.dp)
            if (isInitialState) {
                DragValue.Center at height(percent = 40)
            }
            DragValue.End at contentHeight
        },
        confirmValueChange = {
            if (isInitialState) {
                isInitialState = false
                redefineValues()
            }
            true
        }
    )

    var padding by remember { mutableStateOf(56.dp) }

    BottomSheetScaffold(
        sheetState = state,
        onSheetMoved = { sheetHeight ->
            padding = sheetHeight
        },
        sheetContent = {
            BottomSheetContent()
        },
        content = {
            BottomSheetScreenBody(padding)
        }
    )
}