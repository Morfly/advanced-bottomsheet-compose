package io.morfly.composelayouts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import io.morfly.composelayouts.ui.theme.ComposeLayoutsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeLayoutsTheme {
//                BottomSheet1()

                BottomSheetScaffoldDemo()
//                MultiStateBottomSheetDemo()

//                UsingLibraryBottomSheet()
//                DynamicHeaderLayout()
//                FlowLayout {
//                    repeat(10) {
//                        Box(
//                            modifier = Modifier
//                                .width(Random.nextInt(50, 200).dp)
//                                .height(100.dp)
//                                .background(Color(Random.nextLong(0xFFFFFFFF)))
//                        )
//                    }
//                }
            }
        }
    }
}

@Composable
fun FlowLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = content,
        measurePolicy = { measurables, constraints ->
            val placeables = measurables.map { it.measure(constraints) }

            val groupedPlaceables = mutableListOf<List<Placeable>>()
            var currentGroup = mutableListOf<Placeable>()
            var currentGroupWidth = 0

            for (placeable in placeables) {
                if (currentGroupWidth + placeable.width <= constraints.maxWidth) {
                    currentGroup += placeable
                    currentGroupWidth += placeable.width
                } else {
                    groupedPlaceables += currentGroup
                    currentGroup = mutableListOf(placeable)
                    currentGroupWidth = placeable.width
                }
            }

            if (currentGroup.isNotEmpty()) {
                groupedPlaceables += currentGroup
            }

            layout(
                width = constraints.maxWidth,
                height = constraints.maxHeight,
            ) {
                var yPosition = 0
                for (group in groupedPlaceables) {
                    var xPosition = 0
                    for (placeable in group) {
                        placeable.place(
                            x = xPosition,
                            y = yPosition
                        )
                        xPosition += placeable.width
                    }
                    yPosition += group.maxOfOrNull { it.height } ?: 0
                }
            }
        }
    )
}