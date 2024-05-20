
<h1 align="center">Configurable Bottom Sheet for Compose</h1></br>

![Bottom sheet demo](demos/demo_1.png)

## Installation
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.morfly.airin/airin-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.morfly.airin/airin-core)

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
        // Bottom sheet offset is 60%, so its height is 40% of the screen.
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
    },
)
```

### Comparing with Google's implementation

### Bottom sheet values

### Refreshing values

### Observing bottom sheet dimensions

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
