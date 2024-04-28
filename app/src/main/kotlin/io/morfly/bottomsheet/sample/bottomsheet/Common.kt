@file:OptIn(MapsComposeExperimentalApi::class)

package io.morfly.bottomsheet.sample.bottomsheet

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
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
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import io.morfly.bottomsheet.sample.bottomsheet.BottomSheetContentHeight.ExceedsScreen
import io.morfly.bottomsheet.sample.bottomsheet.BottomSheetContentHeight.FitsScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

enum class BottomSheetContentHeight {
    FitsScreen, ExceedsScreen,
}

@Composable
fun BottomSheetScreenBody(mapUiBottomPadding: Dp) {
    if (mapUiBottomPadding == Dp.Unspecified) return

    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    val density = LocalDensity.current
    val bottomPaddingPx = with(density) { mapUiBottomPadding.toPx() }
    val startPaddingPx = with(density) { 16.dp.roundToPx() }

    val singapore = LatLng(1.35, 103.87)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 10f)
    }

    var cameraInitiallyPositioned by remember { mutableStateOf(false) }
    var canAnimateCamera by remember { mutableStateOf(true) }
    var lastCameraPosition by remember { mutableFloatStateOf(bottomPaddingPx) }

    var cameraKey by remember { mutableStateOf(mapUiBottomPadding) }
//    mapUiBottomPadding.useDebounce(100L) {
//        cameraKey = it
//    }


    LaunchedEffect(key1 = mapUiBottomPadding == Dp.Unspecified) {
        if (!cameraInitiallyPositioned && mapUiBottomPadding != Dp.Unspecified) {
            val update = CameraUpdateFactory.scrollBy(0f, bottomPaddingPx / 2)
            cameraPositionState.move(update)
            lastCameraPosition = bottomPaddingPx
            cameraInitiallyPositioned = true
        }
    }

    SideEffect {
//        scope.launch {
//            snapshotFlow { bottomPaddingPx - lastCameraPosition }
////                .debounce(100)
//                .filter { !cameraPositionState.isMoving }
//                .collect { diffPx ->
//                    println("TTAGG last: $lastCameraPosition, curr: $bottomPaddingPx, diff: $diffPx ${cameraPositionState.cameraMoveStartedReason}")
//                    val update = CameraUpdateFactory.scrollBy(0f, diffPx / 2)
//                    cameraPositionState.animate(update)
//                    println("TTAGG after")
//                    lastCameraPosition = bottomPaddingPx
//                }
//        }


//        if (!cameraInitiallyPositioned) {
//            val update = CameraUpdateFactory.scrollBy(0f, bottomPaddingPx / 2)
//            cameraPositionState.move(update)
//            lastCameraPosition = bottomPaddingPx
//            cameraInitiallyPositioned = true
//
//        } else if (!cameraPositionState.isMoving && canAnimateCamera) {
//            val diffPx = bottomPaddingPx - lastCameraPosition
//            println("TTAGG last: $lastCameraPosition, curr: $bottomPaddingPx, diff: $diffPx ${cameraPositionState.cameraMoveStartedReason}")
//            val update = CameraUpdateFactory.scrollBy(0f, diffPx / 2)
//            canAnimateCamera = false
//            cameraPositionState.animate(update)
//            canAnimateCamera = true
//            println("TTAGG after")
//            lastCameraPosition = bottomPaddingPx
//        }

    }


    val cameraAnimationScope = rememberCoroutineScope()
    var cameraAnimationJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(mapUiBottomPadding) {
        snapshotFlow { mapUiBottomPadding }
            .map { if (it > 420.dp) 420.dp else it }
            .debounce(100)
            .filter { !cameraPositionState.isMoving }
            .collectLatest { padding ->
                val bottomPaddingPx = with(density) { padding.toPx() }

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