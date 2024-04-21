package io.morfly.composelayouts

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.animateTo

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