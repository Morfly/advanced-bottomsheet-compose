package io.morfly.compose.bottomsheet.material3

import androidx.compose.foundation.ExperimentalFoundationApi

@ExperimentalFoundationApi
suspend fun <T> AnchoredDraggableState<T>.updateAnchorsAnimated(
    newAnchors: DraggableAnchors<T>,
    newTarget: T = if (!offset.isNaN()) {
        newAnchors.closestAnchor(offset) ?: targetValue
    } else targetValue
) {
    if (anchors != newAnchors) {
        anchors = newAnchors

        animateTo(newTarget)
    }
}