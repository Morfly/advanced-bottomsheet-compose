@file:OptIn(ExperimentalMaterial3Api::class)

package io.morfly.composelayouts.ui.bottomsheet

import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import io.morfly.composelayouts.ui.BottomSheetContent
import io.morfly.composelayouts.ui.BottomSheetScreenBody

@Composable
fun Material3BottomSheetDemo() {
    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded
    )
    val scaffoldState = rememberBottomSheetScaffoldState(sheetState)

    BottomSheetScaffold(
        sheetPeekHeight = 56.dp,
        scaffoldState = scaffoldState,
        sheetContent = { BottomSheetContent() },
        content = { BottomSheetScreenBody() },
    )
}