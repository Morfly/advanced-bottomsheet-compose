
<h1 align="center">Advanced Bottom Sheet for Compose</h1>

<p align="center">
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="Apache 2.0 license" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://search.maven.org/search?q=g:%22io.morfly.compose%22%20AND%20a:%22advanced-bottomsheet-material3%22"><img alt="Maven Central" src="https://img.shields.io/maven-central/v/io.morfly.compose/advanced-bottomsheet-material3.svg?label=Maven%20Central"/></a>
</p><br>

<p align="center">
<b>Advanced Bottom Sheet</b> provides an implementation of a <a href="https://m3.material.io/components/bottom-sheets/overview">Material3 Standard Bottom Sheet</a> component for Compose with flexible configuration abilities. 
</p><br>


![Bottom sheet demo](demos/demo_cover.png)

The purpose of this repository is to lift the limitations of the original Material 3 bottom sheet by providing a more flexible API for configuring bottom sheet states. With **Advanced Bottom Sheet** you can implement more sophisticated use cases for your designs that rely on bottom sheets. 

## Installation
[![Maven Central](https://img.shields.io/maven-central/v/io.morfly.compose/advanced-bottomsheet-material3.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.morfly.compose%22%20AND%20a:%22advanced-bottomsheet-material3%22)

#### Gradle
Add the dependency below to the`build.gradle.kts` file of your module. The **Advanced Bottom Sheet** is compatible with [Compose Material3](https://developer.android.com/jetpack/androidx/releases/compose-material3).
```kotlin
dependencies {
    implementation("io.morfly.compose:advanced-bottomsheet-material3:<version>")
}
```

## How to use
Advanced Bottom Sheet follows the API of `BottomSheetScaffold` component from the official Material3 implementation as close as possible while adding advanced configuration abilities for bottom sheets.

Folow the following 3 steps to implement a bottom sheet in your app.

#### Step 1
Define an `enum class` that represents the values (states) of your bottom sheet.

```kotlin
enum class SheetValue { Collapsed, PartiallyExpanded, Expanded }
```

#### Step 2
Create an instance of a `BottomSheetState` using `rememberBottomSheetState` function.

```kotlin
val sheetState = rememberBottomSheetState(
    initialValue = SheetValue.PartiallyExpanded,
    defineValues = {
        // Bottom sheet height is 100 dp.
        SheetValue.Collapsed at height(100.dp)
        // Bottom sheet offset is 60%, meaning it takes 40% of the screen.
        SheetValue.PartiallyExpanded at offset(percent = 60)
        // Bottom sheet height is equal to its content height.
        SheetValue.Expanded at contentHeight
    }
)
```

Use `defineValues` lambda to configure bottom sheet values by mapping them to corresponding positions using `height`, `offset` or `contentHeight` and specify the `initialValue` of the bottom sheet.

Check [bottom sheet values](#bottom-sheet-values) section to learn more.

#### Step 3


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
This project mitigates 2 constraints of the original Material 3 bottom sheet implementation from Google.
- **More than 2 expanded states**. The original implementation allows the maximum of 2 expanded states. The **advanced bottom sheet** provides a convenient API for declaring as many states as you like.
- **Dynamic state changes**. It also enables the ability to dynamically change the number of states whyle the bottom sheet is being used including mid-animation cases when it's being dragged.

### Bottom sheet values
You can have as many bottom sheet values as you like and be able to easily configure the position of each of them. 

> State and value are used interchangeably in this context.

You can configure the bottom sheet values during the initialization of a `BottomSheetState` instance in `defineValues` lambda. There are a few available options to configure bottom sheet values.

```kotlin
val sheetState = rememberBottomSheetState(
    initialValue = SheetValue.PartiallyExpanded,
    defineValues = {
        SheetValue.Collapsed at height(...)
        SheetValue.PartiallyExpanded at offset(...)
        SheetValue.Expanded at contentHeight
    }
)
```

Define bottom sheet position using the offset from the top of the screen.

- `offset(px = 200f)` — bottom sheet offset in `Float` pixels.

- `offset(dp = 56.dp)` — bottom sheet offset in dp.

- `offset(percent = 60)` — bottom sheet offset as percentage of the screen height. (E.g. a 60% offset means the bottom sheet takes 40% of the screen height)

Define bottom sheet position using its height.

- `height(px = 200f)` — bottom sheet height in `Float` pixels.

- `height(dp = 56.dp)` — bottom sheet height in dp.

- `height(percent = 40)` — bottom sheet height as a percentage of the screen height. (It takes 40% of the screen height in this case)

Finally, use `contentHeight` if you want the bottom sheet to wrap it's content.

### Dynamically reconfigure values
In some cases, you might need to add, update or remove the bottom sheet values while you're using it mid-animation.

Imagine a use case when your bottom sheet has 3 values, `Collapsed`, `PartiallyExpanded` and `Expanded`. You need the mid value `PartiallyExpanded` to be present when you open the screen. However, once the user drags the bottom sheet you need to remove it so that only `Collapsed` and `PartiallyExpanded` values are present.

The `BottomSheetState` instance provides a `refreshValues` function that upon calling will invoke the `defineValues` lambda again.

```kotlin
var isInitialState by remember { mutableStateOf(true) }

val sheetState = rememberBottomSheetState(
    initialValue = SheetValue.PartiallyExpanded,
    defineValues = {
        SheetValue.Collapsed at height(100.dp)
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
As an example, the `confirmValueChange` lambda is invoked every time the bottom sheet value is about to be changed. This is a good place to update the variable that impacts the bottom sheet configuration.

### Observing bottom sheet state
You can easily observe in realtime the position and the dimensions of the bottom sheet.

A common use case is when your bottom sheet is displayed on top of a map. You might need to adjust the map UI controls or comply with the [Terms of Service](https://developers.google.com/maps/documentation/places/android-sdk/policies#logo) and display the Google logo while the bottom sheet is being dragged.

```kotlin
val sheetState = rememberBottomSheetState(...)
val scaffoldState = rememberBottomSheetScaffoldState(sheetState)

BottomSheetScaffold(
    scaffoldState = scaffoldState,
    sheetContent = { ... },
    content = {
        // Observe the height of the visible part of the bottom sheet 
        // while its being dragged.
        val bottomPadding by remember {
            derivedStateOf { sheetState.requireSheetVisibleHeightDp() }
        }

        val cameraPositionState = rememberCameraPositionState()
        GoogleMap(
            cameraPositionState = cameraPositionState,
            // Adjust the map content padding based on the current
            // bottom sheet height.
            contentPadding = remember(bottomPadding) { 
                PaddingValues(bottom = bottomPadding)
            }
        )
    },
)
```
By using `derivedStateOf` you will get realtime updates once the bottom sheet is being dragged. Here is the list of properties of `BottomSheetState` you could observe.

`offset`, `offsetDp` — bottom sheet offset from the top of the screen in pixels and dp.

`layoutHeight`, `layoutHeightDp` — height of the containing layout in pixels and dp.

`sheetFullHeight`, `sheetFullHeightDp` — full height of the bottom sheet including the offscreen part in pixels and dp.

`sheetVisibleHeight`, `sheetVisibleHeightDp` — visible height of the bottom sheet in pixels and dp.

For each of the properties above a function with `require...` prefix is available which is preferred way to retrieve these values. For instance `requireOffset()`, `requireOffsetDp()`, etc.

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
