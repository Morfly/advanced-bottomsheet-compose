package io.morfly.composelayouts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp

@Composable
fun DynamicHeaderLayout() {
    var headerContent by remember { mutableStateOf("Header") }

    SubcomposeLayout { constraints ->
        val header = subcompose("header") {
            Text(headerContent, Modifier.padding(16.dp))
        }[0].measure(constraints.copy(maxHeight = Constraints.Infinity))

        val mainContentConstraints = constraints.copy(
            minHeight = 0,
            maxHeight = constraints.maxHeight - header.height
        )
        val mainContent = subcompose("mainContent") {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
            ) {
                Text("Main Content", Modifier.align(Alignment.Center))
            }
        }[0].measure(mainContentConstraints)

        layout(constraints.maxWidth, constraints.maxHeight) {
            header.placeRelative(0, 0)

            mainContent.placeRelative(0, header.height)
        }
    }
}