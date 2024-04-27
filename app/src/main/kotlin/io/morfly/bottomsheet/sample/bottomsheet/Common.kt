package io.morfly.bottomsheet.sample.bottomsheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.morfly.bottomsheet.sample.bottomsheet.BottomSheetContentHeight.ExceedsScreen
import io.morfly.bottomsheet.sample.bottomsheet.BottomSheetContentHeight.FitsScreen

enum class BottomSheetContentHeight {
    FitsScreen, ExceedsScreen,
}

@Composable
fun BottomSheetScreenBody(bottomPadding: Dp = 0.dp) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, bottom = 10.dp)
            .padding(bottom = bottomPadding),
        contentAlignment = Alignment.BottomStart
    ) {
        Text(
            text = "Google",
            style = TextStyle(fontSize = 16.sp)
        )
    }
}

@Composable
fun BottomSheetContent(height: BottomSheetContentHeight = FitsScreen) {
    val numberOfItems = when (height) {
        FitsScreen -> 27
        ExceedsScreen -> 100
    }
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        for (i in 0..numberOfItems) {
            item {
                Text(text = "Test_$i")
            }
        }
    }
}