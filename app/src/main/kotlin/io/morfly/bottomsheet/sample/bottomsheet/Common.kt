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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import io.morfly.bottomsheet.sample.bottomsheet.BottomSheetContentHeight.ExceedsScreen
import io.morfly.bottomsheet.sample.bottomsheet.BottomSheetContentHeight.FitsScreen
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

val SanFranciscoLocation = LatLng(37.773972, -122.431297)

@Composable
fun MapScreenContent(
    mapUiBottomPadding: Dp,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(SanFranciscoLocation, 13f)
    }

    AdjustedCameraPositionEffect(
        cameraPositionState = cameraPositionState,
        mapUiBottomPadding = mapUiBottomPadding
    )

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(position = SanFranciscoLocation),
            title = "San Francisco",
            snippet = "Marker in San Francisco"
        )

        if (isPortrait) {
            val density = LocalDensity.current
            val bottomPaddingPx = with(density) { mapUiBottomPadding.roundToPx() }
            val startPaddingPx = with(density) { 16.dp.roundToPx() }
            MapEffect(mapUiBottomPadding) { map ->
                map.setPadding(startPaddingPx, 0, 0, bottomPaddingPx)
            }
        }
    }
}

@OptIn(FlowPreview::class)
@Composable
private fun AdjustedCameraPositionEffect(
    cameraPositionState: CameraPositionState,
    mapUiBottomPadding: Dp,
) {
    val density = LocalDensity.current

    var lastCameraPosition by remember { mutableFloatStateOf(0f) }

    val cameraAnimationScope = rememberCoroutineScope()
    var cameraAnimationJob by remember { mutableStateOf<Job?>(null) }
    LaunchedEffect(mapUiBottomPadding) {
        snapshotFlow { mapUiBottomPadding }
            .debounce(timeoutMillis = 100)
            .filter { !cameraPositionState.isMoving }
            .collectLatest { bottomPadding ->
                val bottomPaddingPx = with(density) { bottomPadding.toPx() }

                val diffPx = bottomPaddingPx - lastCameraPosition
                if (diffPx == 0f) return@collectLatest

                val update = CameraUpdateFactory.scrollBy(0f, diffPx / 2)

                cameraAnimationJob?.cancel()
                cameraAnimationJob = cameraAnimationScope.launch {
                    cameraPositionState.animate(update)
                    lastCameraPosition = bottomPaddingPx
                }
            }
    }
}

enum class BottomSheetContentHeight { FitsScreen, ExceedsScreen, }

@Composable
fun BottomSheetContent(
    modifier: Modifier = Modifier,
    height: BottomSheetContentHeight = FitsScreen
) {
    val numberOfItems = when (height) {
        FitsScreen -> 27
        ExceedsScreen -> 100
    }
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        for (i in 0..numberOfItems) {
            item {
                Text(text = "Test_$i")
            }
        }
    }
}