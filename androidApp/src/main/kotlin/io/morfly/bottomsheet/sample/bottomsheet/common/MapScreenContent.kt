package io.morfly.bottomsheet.sample.bottomsheet.common

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import io.morfly.bottomsheet.sample.R

private const val DefaultMapZoom = 13f
private val SanFranciscoLocation = LatLng(37.77446, -122.42064)
private val MapUiOffsetLimit = 100.dp

@Composable
fun MapScreenContent(
    modifier: Modifier = Modifier,
    initialLocation: LatLng = SanFranciscoLocation,
    bottomPadding: Dp = 0.dp,
    isBottomSheetMoving: Boolean = false,
    layoutHeight: Dp = Dp.Unspecified,
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, DefaultMapZoom)
    }

    val maxBottomPadding = remember(layoutHeight) { layoutHeight - MapUiOffsetLimit }
    val mapPadding = rememberMapPadding(bottomPadding, maxBottomPadding)

    AdjustedCameraPositionEffect(
        camera = cameraPositionState,
        isBottomSheetMoving = isBottomSheetMoving,
        bottomPadding = mapPadding.calculateBottomPadding(),
    )

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = rememberMapUiSettings(),
        properties = rememberMapProperties(),
        contentPadding = mapPadding
    )
}

@Composable
private fun AdjustedCameraPositionEffect(
    camera: CameraPositionState,
    isBottomSheetMoving: Boolean,
    bottomPadding: Dp,
) {
    var cameraLocation by remember { mutableStateOf(camera.position.target) }
    LaunchedEffect(camera.isMoving, camera.cameraMoveStartedReason) {
        if (!camera.isMoving && camera.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE) {
            cameraLocation = camera.position.target
        }
    }

    var isCameraInitialized by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    LaunchedEffect(isBottomSheetMoving) {
        if (isBottomSheetMoving) return@LaunchedEffect

        // The map does not respect the initial bottom padding value. The CameraPositionState in
        // this case returns the camera location as if the padding was not set. Therefore, the
        // camera must be manually shifted according to the initial padding value.
        if (!isCameraInitialized) {
            isCameraInitialized = true
            val verticalShiftPx = with(density) { bottomPadding.toPx() / 2 }
            val update = CameraUpdateFactory.scrollBy(0f, verticalShiftPx)
            camera.animate(update)
        } else {
            val update = CameraUpdateFactory.newLatLng(cameraLocation)
            camera.animate(update)
        }
    }
}

@Composable
private fun rememberMapPadding(bottomPadding: Dp, maxBottomPadding: Dp): PaddingValues {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    return if (isPortrait) {
        rememberPortraitMapPadding(bottomPadding, maxBottomPadding)
    } else {
        remember { PaddingValues() }
    }
}

@Composable
private fun rememberPortraitMapPadding(bottomPadding: Dp, maxBottomPadding: Dp): PaddingValues {
    return remember(bottomPadding, maxBottomPadding) {
        PaddingValues(
            start = 16.dp,
            end = 16.dp,
            bottom = bottomPadding.takeIf { it < maxBottomPadding } ?: maxBottomPadding
        )
    }
}

@Composable
private fun rememberMapUiSettings(): MapUiSettings {
    return remember {
        MapUiSettings(
            compassEnabled = true,
            indoorLevelPickerEnabled = true,
            mapToolbarEnabled = false,
            myLocationButtonEnabled = false,
            rotationGesturesEnabled = false,
            scrollGesturesEnabled = true,
            scrollGesturesEnabledDuringRotateOrZoom = false,
            tiltGesturesEnabled = false,
            zoomControlsEnabled = false,
            zoomGesturesEnabled = true,
        )
    }
}

@Composable
private fun rememberMapProperties(): MapProperties {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()
    return remember(isDarkTheme) {
        val mapStyleOptions = if (isDarkTheme) {
            mapStyleDarkOptions(context)
        } else {
            mapStyleOptions(context)
        }
        MapProperties(mapStyleOptions = mapStyleOptions)
    }
}

fun mapStyleOptions(context: Context): MapStyleOptions {
    return MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
}

fun mapStyleDarkOptions(context: Context): MapStyleOptions {
    return MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark)
}
