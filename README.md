# AAC.kt

![Android](https://img.shields.io/badge/Android-4.0%2B-blue.svg)
![Kotlin](https://img.shields.io/badge/Kotlin-1.3.50%2B-blue.svg)
[![CircleCI](https://circleci.com/gh/tmurakami/aackt.svg?style=shield)](https://circleci.com/gh/tmurakami/aackt)
[![Release](https://jitpack.io/v/tmurakami/aackt.svg)](https://jitpack.io/#tmurakami/aackt)

A Kotlin library for
[Android Architecture Components](https://developer.android.com/topic/libraries/architecture/).

## LiveData

```kotlin
val owner = object : LifecycleOwner {
    val registry = LifecycleRegistry(this)
    override fun getLifecycle(): Lifecycle = registry
}

val data = MutableLiveData<Any?>()

val values = mutableListOf<Int>()

data.filterIsInstance<Int>() // Filter out non-int values
    .dropWhile { it < 3 } // Drop values less than 3
    .subscribe(owner) { values += it }

owner.registry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

data.value = 0
data.value = 1
data.value = 2
data.value = null
data.value = 3
data.value = Unit
data.value = 4

assertEquals(listOf(3, 4), values)
```

If a LiveData already has a value, the observer added via `subscribe`
will receive that value first. If you would not like to receive it, you
can use `subscribeChanges` to receive only updated values.

```kotlin
val owner = object : LifecycleOwner {
    val registry = LifecycleRegistry(this)
    override fun getLifecycle(): Lifecycle = registry
}

val data = MutableLiveData(0)

val values = mutableListOf<Int>()
data.subscribe(owner) { values += it }

val updatedValues = mutableListOf<Int>()
// Observers added via `subscribeChanges` will only receive updated values.
data.subscribeChanges(owner) { updatedValues += it }

owner.registry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

data.value = 1

assertEquals(listOf(0, 1), values)
assertEquals(listOf(1), updatedValues)
```

## ViewModel

Since `ViewModelProvider` doesn't manage keys for each ViewModel
class, for example, the following test code will fail.

```kotlin
// Create a ViewModelProvider.
val provider = ViewModelProvider(fragment)

// Fetch a FooViewModel with the key `a`.
val fooViewModel = provider.get("a", FooViewModel::class.java)

// Fetch a BarViewModel with the same key.
provider.get("a", BarViewModel::class.java)

// This test will fail because the above code overwrites that key.
assertSame(fooViewModel, provider.get("a", FooViewModel::class.java))
```

To avoid the above problem, you can use `TypedViewModelProvider`, which
manages ViewModel instances by both class and name.

```kotlin
// Create TypedViewModelProviders for each ViewModel.
val fooProvider = fragment.viewModelProvider<FooViewModel>()
val barProvider = fragment.viewModelProvider<BarViewModel>()

// Fetch a FooViewModel with the name `a`.
val fooViewModel = fooProvider["a"]

// Fetch a BarViewModel with the same name.
barProvider["a"]

// This test will pass.
assertSame(fooViewModel, fooProvider["a"])
```

In addition, you can use `ViewModelStoreOwner.viewModelLazy` function to
retrieve ViewModels lazily in a type-safe manner.

```kotlin
class MyFragment : Fragment() {
    private val viewModel by viewModelLazy {
        viewModelProvider { MyViewModel(/* ... */) }
    }
}
```

You can also use it as follows:

```kotlin
// Use the default factory
viewModelLazy<MyViewModel>()
viewModelLazy<MyViewModel> { viewModelProvider() }
```

```kotlin
val factory = object : ViewModelProvider.Factory { /* ... */ }

// Use a ViewModelProvider.Factory instead of the default factory
viewModelLazy<MyViewModel> { viewModelProvider(factory) }
```

```kotlin
// Cache an activity-scoped ViewModel created by the default factory
viewModelLazy<MyViewModel> { requireActivity().viewModelProvider() }
```

## ViewModel-SavedState

You can use the `SavedStateHandleProvider` utility to provide
SavedStateHandle instances as shown below:

```kotlin
val provider = SavedStateHandleProvider(fragment, fragment.arguments)
val handle = provider["myHandleKey"]

var intValue: Int by handle
val stringValue: LiveData<String> by handle.liveData()
```

## Room

```kotlin
// Create a new Migration instance
val MIGRATION_1_2 = Migration(1, 2) { /* ... */ }

// Create a new RoomDatabase instance
val db = context.createRoomDatabase<MyRoomDatabase>("app.db") {
    addMigrations(MIGRATION_1_2)
}
```

## WorkManager

```kotlin
val data = workDataOf("intValue" to 0)
val intValue: Int by data
```

## Installation

Add the following to your `build.gradle`:

```groovy
repositories {
    google()
    maven { url 'https://jitpack.io' }
    jcenter()
}

dependencies {

    def aacktVersion = '2.0.0'

    // LiveData
    implementation "com.github.tmurakami.aackt:lifecycle-livedata:$aacktVersion"

    // ViewModel
    implementation "com.github.tmurakami.aackt:lifecycle-viewmodel:$aacktVersion"

    // ViewModel-SavedState
    implementation "com.github.tmurakami.aackt:lifecycle-viewmodel-savedstate:$aacktVersion"

    // Room
    implementation "com.github.tmurakami.aackt:room-runtime:$aacktVersion"

    // Room with RxJava2
    implementation "com.github.tmurakami.aackt:room-rxjava2:$aacktVersion"

    // WorkManager
    implementation "com.github.tmurakami.aackt:work-runtime:$aacktVersion"

}
```

## License

```
Copyright 2018 Tsuyoshi Murakami

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
