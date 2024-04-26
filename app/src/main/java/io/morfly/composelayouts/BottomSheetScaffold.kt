package io.morfly.composelayouts

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffold
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetState


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetScaffoldDemo() {
    var isInitialState by rememberSaveable { mutableStateOf(true) }

    val state = rememberBottomSheetState(
        initialValue = DragValue.Center,
        defineValues = {
            DragValue.Start at height(200.dp)
            if (isInitialState)
                DragValue.Center at height(percent = 50)
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

    var padding by remember { mutableStateOf(200.dp) }

    BottomSheetScaffold(
        sheetState = state,
        onSheetMoved = { sheetHeight ->
            padding = if (sheetHeight <= 420.dp) {
                sheetHeight
            } else {
                420.dp
            }
        },
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, bottom = padding + 10.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = "Google",
                    style = TextStyle(fontSize = 16.sp)
                )
            }
        }
    )
}