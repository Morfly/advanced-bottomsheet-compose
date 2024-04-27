package io.morfly.composelayouts.ui.bottomsheet

import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import io.morfly.composelayouts.ui.BottomSheetContent
import io.morfly.composelayouts.ui.BottomSheetScreenBody

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Material3BottomSheetDemo() {
    BottomSheetScaffold(
        sheetContent = {
            BottomSheetContent()
        },
        content = {
            BottomSheetScreenBody()
        }
    )
}