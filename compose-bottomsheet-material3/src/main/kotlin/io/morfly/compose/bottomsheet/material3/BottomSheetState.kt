package io.morfly.compose.bottomsheet.material3

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize

@ExperimentalFoundationApi
@Stable
class BottomSheetState<T : Any>(
    val draggableState: AnchoredDraggableState<T>,
    val defineValues: BottomSheetValuesConfig<T>.() -> Unit,
) {
    internal val onValuesRequested = mutableSetOf<(layoutSize: IntSize) -> Unit>()

    var layoutSize: IntSize? = null
        internal set
    var sheetOffset: Offset? = null
        internal set

    fun requireLayoutSize() = layoutSize!!

    fun requireSheetOffse() = sheetOffset!!

    fun redefineValues() {
        val size = layoutSize ?: return
        onValuesRequested.forEach { call -> call(size) }
    }

    companion object {

        fun <T : Any> Saver(
            defineValues: BottomSheetValuesConfig<T>.() -> Unit,
        ) = Saver<BottomSheetState<T>, AnchoredDraggableState<T>>(
            save = { it.draggableState },
            restore = { draggableState ->
                BottomSheetState(draggableState, defineValues)
            }
        )
    }
}
