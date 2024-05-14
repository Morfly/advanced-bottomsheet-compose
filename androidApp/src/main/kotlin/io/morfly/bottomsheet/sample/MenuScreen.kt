package io.morfly.bottomsheet.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun MenuScreen(onClick: (Destination) -> Unit) {
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize(),
    ) {
        Text("Title")
        Item(
            title = "Material3 Bottom Sheet",
            description = "Bottom Sheet provided by the official Material3 library.",
            modifier = Modifier.background(Color.LightGray),
            onClick = { onClick(Destination.Material3Demo) }
        )
        Item(
            title = "Anchored Draggable",
            description = "Draggable.",
            onClick = { onClick(Destination.CustomDraggableDemo) }
        )
        Item(
            title = "Anchored Draggable + Subcompose Layout",
            description = "Simplified implementation of a custom bottom sheet.",
            onClick = { onClick(Destination.CustomDraggableSubcomposeDemo) }
        )
        Item(
            title = "Custom Bottom Sheet Finalized",
            description = "Custom implementation of a bottom sheet that extends the functionality of a Material3 variant.",
            onClick = { onClick(Destination.CustomFinalizedDemo) }
        )
    }
}

@Composable
private fun Item(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Button(onClick = onClick) {
            Text(title)
        }
        Text(description)
    }
}