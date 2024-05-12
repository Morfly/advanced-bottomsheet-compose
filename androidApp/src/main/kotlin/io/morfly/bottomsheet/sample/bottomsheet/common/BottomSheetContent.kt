package io.morfly.bottomsheet.sample.bottomsheet.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.morfly.bottomsheet.sample.bottomsheet.common.BottomSheetContentHeight.ExceedsScreen
import io.morfly.bottomsheet.sample.bottomsheet.common.BottomSheetContentHeight.FitsScreen

enum class BottomSheetContentHeight { FitsScreen, ExceedsScreen, }

@Composable
fun BottomSheetContent(
    modifier: Modifier = Modifier,
    userScrollEnabled: Boolean = true,
    height: BottomSheetContentHeight = FitsScreen
) {
    val numberOfItems = when (height) {
        FitsScreen -> 27
        ExceedsScreen -> 100
    }
    LazyColumn(
        userScrollEnabled = userScrollEnabled,
        modifier = modifier.fillMaxWidth()
    ) {
        for (i in 0..numberOfItems) {
            item {
                Text(text = "Test_$i")
            }
        }
    }
}