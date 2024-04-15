package io.morfly.composelayouts.simplified

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MultiStateBottomSheetDemo() {
    MultiStateBottomSheet(
        sheetContent = {
            LazyColumn(
                userScrollEnabled = true,
                modifier = Modifier.fillMaxWidth(),
            ) {
                for (i in 0..25) {
                    item {
                        Text(text = "Test_$i")
                    }
                }
            }
        },
        content = {

        }
    )
}