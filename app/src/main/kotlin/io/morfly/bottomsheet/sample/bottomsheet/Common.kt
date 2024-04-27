@file:OptIn(MapsComposeExperimentalApi::class)

package io.morfly.bottomsheet.sample.bottomsheet

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import io.morfly.bottomsheet.sample.bottomsheet.BottomSheetContentHeight.ExceedsScreen
import io.morfly.bottomsheet.sample.bottomsheet.BottomSheetContentHeight.FitsScreen

enum class BottomSheetContentHeight {
    FitsScreen, ExceedsScreen,
}

@Composable
fun BottomSheetScreenBody(mapUiBottomPadding: Dp) {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    val density = LocalDensity.current
    val bottomPaddingPx = with(density) {
        if (mapUiBottomPadding != Dp.Unspecified) {
            mapUiBottomPadding.toPx()
        } else 0f
    }
    val startPaddingPx = with(density) { 16.dp.roundToPx() }

    val singapore = LatLng(1.35, 103.87)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 10f)
    }

    LaunchedEffect(mapUiBottomPadding == Dp.Unspecified) {
        println("TTAGG padding")
        val update = CameraUpdateFactory.scrollBy(0f, bottomPaddingPx / 2)
        cameraPositionState.move(update)
    }
//
//
//    var scroll by remember { mutableFloatStateOf(bottomPaddingPx) }
//
//    LaunchedEffect(mapUiBottomPadding) {
//
//        if (!cameraPositionState.isMoving && mapUiBottomPadding != Dp.Unspecified) {
//            println("TTAGG move")
//            val diff = scroll - bottomPaddingPx
//            val update = CameraUpdateFactory.scrollBy(0f, diff)
//            cameraPositionState.animate(update)
//            scroll = bottomPaddingPx
//        }
//    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(position = singapore),
            title = "Singapore",
            snippet = "Marker in Singapore"
        )

        if (isPortrait) {
            MapEffect(mapUiBottomPadding) { map ->
                map.setPadding(startPaddingPx, 0, 0, bottomPaddingPx.toInt())

            }
        }
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