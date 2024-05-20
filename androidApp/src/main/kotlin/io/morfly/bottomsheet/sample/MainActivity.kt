package io.morfly.bottomsheet.sample

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import io.morfly.bottomsheet.sample.theme.MultiStateBottomSheetSampleTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdgeWithUiMode()
        super.onCreate(savedInstanceState)
        setContent {
            MultiStateBottomSheetSampleTheme {
                Surface {
                    Navigation()
                }
            }
        }
    }
}

private fun ComponentActivity.enableEdgeToEdgeWithUiMode() {
    val uiMode = resources.configuration.uiMode
    val isDark = (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

    val statusBarStyle = if (isDark) {
        SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
    } else {
        SystemBarStyle.light(
            android.graphics.Color.TRANSPARENT,
            android.graphics.Color.TRANSPARENT
        )
    }
    enableEdgeToEdge(statusBarStyle = statusBarStyle)
}
