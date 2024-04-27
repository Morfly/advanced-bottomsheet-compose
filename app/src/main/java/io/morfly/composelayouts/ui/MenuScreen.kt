package io.morfly.composelayouts.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.morfly.composelayouts.Destination

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
            onClick = { onClick(Destination.Material3Demo) }
        )
        Item(
            title = "Custom Simplified Bottom Sheet",
            description = "Simplified implementation of a custom bottom sheet.",
            onClick = { onClick(Destination.CustomSimplifiedDemo) }
        )
        Item(
            title = "Custom Bottom Sheet",
            description = "Custom implementation of a bottom sheet that extends the functionality of a Material3 variant.",
            onClick = { onClick(Destination.CustomDemo) }
        )
    }
}

@Composable
private fun Item(
    title: String,
    description: String,
    onClick: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = onClick) {
            Text(title)
        }
        Text(description)
    }
}