package io.morfly.androidx.compose.foundation.gestures

import androidx.compose.foundation.ExperimentalFoundationApi

@OptIn(ExperimentalFoundationApi::class)
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