package io.morfly.bottomsheet.sample.bottomsheet.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.morfly.bottomsheet.sample.bottomsheet.common.BottomSheetContentSize.Large
import io.morfly.bottomsheet.sample.bottomsheet.common.BottomSheetContentSize.Medium
import io.morfly.bottomsheet.sample.bottomsheet.common.BottomSheetContentSize.Small

enum class BottomSheetContentSize { Small, Medium, Large }

@Composable
fun BottomSheetContent(
    modifier: Modifier = Modifier,
    userScrollEnabled: Boolean = true,
    size: BottomSheetContentSize = Medium
) {
    val itemCount = remember(size) {
        when (size) {
            Small -> 0
            Medium -> 2
            Large -> pointsOfInterest.size
        }
    }
    LazyColumn(
        userScrollEnabled = userScrollEnabled,
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
    ) {
        item {
            Header(Modifier.padding(bottom = 16.dp))
        }

        items(itemCount) { i ->
            PointOfInterestItem(
                pointOfInterest = pointsOfInterest[i],
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun Header(modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(text = "San Francisco, California", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        Text(text = "Iconic places", style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun PointOfInterestItem(
    pointOfInterest: PointOfInterest,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 1.dp,
        modifier = modifier
    ) {
        Column {
            AsyncImage(
                model = pointOfInterest.photoUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.height(190.dp)
            )
            Column(Modifier.padding(16.dp)) {
                Text(text = pointOfInterest.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = pointOfInterest.license,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private data class PointOfInterest(val name: String, val photoUrl: String, val license: String)

private val pointsOfInterest = listOf(
    PointOfInterest(
        name = "Golden Gate",
        photoUrl = "https://images.unsplash.com/photo-1610312278520-bcc893a3ff1d?q=80&w=3494&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        // Photo by Varun Yadav on Unsplash. https://unsplash.com/photos/golden-gate-bridge-san-francisco-california-QhYTCG3CTeI
        license = "Photo by Varun Yadav on Unsplash"
    ),
    PointOfInterest(
        name = "The Painted Ladies",
        photoUrl = "https://images.unsplash.com/photo-1522735555435-a8fe18da2089?q=80&w=3540&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        // Photo by Aaron Kato on Unsplash. https://unsplash.com/photos/white-and-brown-2-storey-houses-with-vehicles-in-front-during-daytime-zcoDYal9GkQ
        license = "Photo by Aaron Kato on Unsplash"
    ),
    PointOfInterest(
        name = "Salesforce Tower",
        photoUrl = "https://images.unsplash.com/photo-1558623869-d6f8763a24f9?q=80&w=3522&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        // Photo by Denys Nevozhai on Unsplash. https://unsplash.com/photos/aerial-view-of-city-during-nighttime-wgtJfd2Jhnk
        license = "Photo by Denys Nevozhai on Unsplash"
    ),
    PointOfInterest(
        name = "Lombard Street",
        photoUrl = "https://plus.unsplash.com/premium_photo-1673483585905-439b19e0d30a?q=80&w=3348&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        // Photo by Casey Horner on Unsplash+. https://unsplash.com/photos/an-aerial-view-of-a-city-with-a-river-running-through-it-tQicpDWhIzk
        license = "Photo by Casey Horner on Unsplash+"
    ),
)