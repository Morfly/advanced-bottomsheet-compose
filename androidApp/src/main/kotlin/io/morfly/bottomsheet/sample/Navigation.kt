package io.morfly.bottomsheet.sample

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.morfly.bottomsheet.sample.bottomsheet.CustomFinalizedDemoScreen
import io.morfly.bottomsheet.sample.bottomsheet.CustomDraggableDemoScreen
import io.morfly.bottomsheet.sample.bottomsheet.OfficialMaterial3DemoScreen
import io.morfly.bottomsheet.sample.bottomsheet.CustomDraggableSubcomposeDemoScreen

enum class Destination {
    Menu, Material3Demo, CustomDraggableDemo, CustomDraggableSubcomposeDemo, CustomFinalizedDemo;
}

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Destination.Menu.name) {
        composable(Destination.Menu.name) {
            MenuScreen(onClick = { destination -> navController.navigate(destination.name) })
        }
        composable(Destination.Material3Demo.name) {
            OfficialMaterial3DemoScreen()
        }
        composable(Destination.CustomDraggableDemo.name) {
            CustomDraggableDemoScreen()
        }
        composable(Destination.CustomDraggableSubcomposeDemo.name) {
            CustomDraggableSubcomposeDemoScreen()
        }
        composable(Destination.CustomFinalizedDemo.name) {
            CustomFinalizedDemoScreen()
        }
    }
}