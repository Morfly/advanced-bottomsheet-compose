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
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import io.morfly.bottomsheet.sample.R

private val SanFranciscoLocation = LatLng(37.773972, -122.431297)

@Composable
fun MapScreenContent(
    modifier: Modifier = Modifier,
    bottomPadding: Dp = 0.dp,
    bottomPaddingMoving: Boolean = false,
    layoutHeight: Dp = Dp.Unspecified,
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(SanFranciscoLocation, 13f)
    }

    val maxBottomPadding = remember(layoutHeight) { layoutHeight - 200.dp }
    val mapPadding = rememberMapPadding(bottomPadding, maxBottomPadding)

    AdjustedCameraPositionEffect(
        cameraPositionState = cameraPositionState,
        bottomPaddingMoving = bottomPaddingMoving,
        bottomPadding = mapPadding.calculateBottomPadding(),
    )

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = rememberMapUiSettings(),
        properties = rememberMapProperties(),
        contentPadding = mapPadding
    ) {
        Marker(state = MarkerState(position = SanFranciscoLocation))
    }
}

@Composable
private fun AdjustedCameraPositionEffect(
    cameraPositionState: CameraPositionState,
    bottomPaddingMoving: Boolean,
    bottomPadding: Dp,
) {
    LaunchedEffect(cameraPositionState.isMoving) {
        println("TTAGG state is Moving: ${cameraPositionState.isMoving}")
    }

    var firstCameraMove by remember { mutableStateOf(true) }

    val density = LocalDensity.current
    LaunchedEffect(bottomPaddingMoving) {
        if (bottomPaddingMoving) return@LaunchedEffect

        if (firstCameraMove) {
            firstCameraMove = false
            val verticalShiftPx = with(density) { bottomPadding.toPx() / 2 }
            val update = CameraUpdateFactory.scrollBy(0f, verticalShiftPx)
            cameraPositionState.animate(update)
        } else {
            val location = cameraPositionState.position.target
            val update = CameraUpdateFactory.newLatLng(location)
            cameraPositionState.animate(update)
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
            bottom = bottomPadding.takeIf { it < maxBottomPadding } ?: maxBottomPadding)
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
