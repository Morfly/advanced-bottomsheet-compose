package io.morfly.bottomsheet.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MenuScreen(
    onClick: (Destination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(top = 64.dp, bottom = 16.dp)
    ) {
        Text(text = "Bottom Sheet Samples", style = MaterialTheme.typography.headlineSmall)

        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Item(
                title = "Official Material3 Bottom Sheet",
                description = "Sample using original bottom sheet from material 3 library.",
                onClick = { onClick(Destination.Material3Demo) }
            )
            Item(
                title = "Anchored Draggable",
                description = "Custom basic bottom sheet implementation using anchored draggable.",
                onClick = { onClick(Destination.CustomDraggableDemo) }
            )
            Item(
                title = "Anchored Draggable + Subcompose Layout",
                description = "Custom bottom sheet implementation using anchored draggable and subcompose layout.",
                onClick = { onClick(Destination.CustomDraggableSubcomposeDemo) }
            )
            Item(
                title = "Finalized Custom Bottom Sheet",
                description = "Sample with the finalized bottom sheet implementation using anchored draggable and subcompose layout. Provides customizable API and is available as a library in this repository.",
                onClick = { onClick(Destination.CustomFinalizedDemo) }
            )
        }
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
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(16.dp)
    ) {
        OutlinedButton(onClick = onClick) {
            Text(title)
        }
        Spacer(Modifier.height(16.dp))
        Text(text = description, style = MaterialTheme.typography.bodyLarge)
    }
}