
<h1 align="center">Configurable Bottom Sheet for Compose</h1>

<p align="center">
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="Apache 2.0 license" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://search.maven.org/search?q=g:%22io.morfly.airin%22%20AND%20a:%22airin-gradle-plugin%22"><img alt="Maven Central" src="https://img.shields.io/maven-central/v/io.morfly.airin/airin-gradle-plugin.svg?label=Maven%20Central"/></a>
</p><br>

![Bottom sheet demo](demos/demo_1.png)

## Installation
[![Maven Central](https://img.shields.io/maven-central/v/io.morfly.airin/airin-gradle-plugin.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.morfly.airin%22%20AND%20a:%22airin-gradle-plugin%22)

```kotlin
dependencies {
    implementation("io.morfly:compose-bottomsheet-material3:<version>")
}
```

## How to use
```kotlin
enum class SheetValue { Peek, PartiallyExpanded, Expanded }
```

```kotlin
val sheetState = rememberBottomSheetState(
    initialValue = SheetValue.PartiallyExpanded,
    defineValues = {
        // Bottom sheet height is 56 dp.
        SheetValue.Peek at height(56.dp)
        // Bottom sheet offset is 60%, meaning it takes 40% of the screen.
        SheetValue.PartiallyExpanded at offset(percent = 60)
        // Bottom sheet height is equal to its content height.
        SheetValue.Expanded at contentHeight
    }
)
```

```kotlin
val scaffoldState = rememberBottomSheetScaffoldState(sheetState)

BottomSheetScaffold(
    scaffoldState = scaffoldState,
    sheetContent = {
        // Bottom sheet content
    },
    content = {
        // Screen content
    }
)
```

### Comparing with Google's implementation
|Feature | Official Material 3 sheet | This sheet |
|-|-|-|
| Bottom sheet states | Has only 2 expanded states. Does not allow to have more. | Allows to configure as many states as you want. |
| Flexible configuration| | Provides a rich configuration API that allows setting bottom sheet dimensions as **offset** and **height** values specified in **pixels**, **dp** or **percent**. |
| Dynamic configuration | | Allows to dynamically **add**/**update**/**remove** bottom sheet states while using it.|

### Bottom sheet values
`offset(px = 200)` — 

`offset(dp = 56.dp)` —

`offset(percent = 60)` —

`height(px = 200)` —

`height(dp = 56.dp)` —

`height(percent = 40)` —

`contentHeight` — a dynamically calculated height of the bottom sheet so it wraps its content.

### Dynamically refreshing values

```kotlin
var isInitialState by remember { mutableStateOf(true) }

val sheetState = rememberBottomSheetState(
    initialValue = SheetValue.PartiallyExpanded,
    defineValues = {
        SheetValue.Peek at height(56.dp)
        if (isInitialState) {
            SheetValue.PartiallyExpanded at offset(percent = 60)
        }
        SheetValue.Expanded at contentHeight
    },
    confirmValueChange = {
        if (isInitialState) {
            isInitialState = false
            // Invokes defineValues lambda again.
            refreshValues()
        }
        true
    }
)
```

### Observing bottom sheet state

```kotlin
val sheetState = rememberBottomSheetState(...)
val scaffoldState = rememberBottomSheetScaffoldState(sheetState)

BottomSheetScaffold(
    scaffoldState = scaffoldState,
    sheetContent = {
        // Bottom sheet content
    },
    content = {
        val bottomPadding by remember {
            derivedStateOf { sheetState.requireSheetVisibleHeightDp() }
        }

        val cameraPositionState = rememberCameraPositionState()
        GoogleMap(
            cameraPositionState = cameraPositionState,
            contentPadding = remember(bottomPadding) { 
                PaddingValues(bottom = bottomPadding)
            }
        )
    },
)
```

`offset`, `offsetDp` — 

`layoutHeight`, `layoutHeightDp` —

`sheetFullHeight`, `sheetFullHeightDp` —

`sheetVisibleHeight`, `sheetVisibleHeightDp` —

For each of the properties above a function with `require...` prefix which is recommented to use. For instance `requireOffset()`, `requireOffsetDp()`.

## License
```
Copyright 2024 morfly (Pavlo Stavytskyi).

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
