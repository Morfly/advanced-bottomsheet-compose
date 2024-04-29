@file:OptIn(ExperimentalMaterial3Api::class)

package io.morfly.bottomsheet.sample.bottomsheet

import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import io.morfly.bottomsheet.sample.bottomsheet.common.BottomSheetContent
import io.morfly.bottomsheet.sample.bottomsheet.common.MapScreenContent

@Composable
fun OfficialMaterial3DemoScreen() {
    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded
    )
    val scaffoldState = rememberBottomSheetScaffoldState(sheetState)

    var bottomPadding by remember { mutableStateOf(0.dp) }

    BottomSheetScaffold(
        sheetPeekHeight = 56.dp,
        scaffoldState = scaffoldState,
        sheetContent = {
            val density = LocalDensity.current

            BottomSheetContent(
                modifier = Modifier.onGloballyPositioned { coordinates ->
                    // TODO use offset from state
                    val sheetLayout = coordinates.parentLayoutCoordinates?.parentLayoutCoordinates
                    val parentLayout = sheetLayout?.parentLayoutCoordinates
                    if (sheetLayout != null && parentLayout != null) {
                        val offsetPx = sheetLayout.positionInParent()
                        val layoutHeightPx = parentLayout.size.height
                        val sheetHeightPx = layoutHeightPx - offsetPx.y
                        bottomPadding = with(density) { sheetHeightPx.toDp() }
                    }
                },
            )
        },
        content = {
            MapScreenContent(bottomPadding)
        },
    )
}