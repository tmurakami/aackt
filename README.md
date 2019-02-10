# AAC.kt

![Android](https://img.shields.io/badge/Android-4.0%2B-blue.svg)
![Kotlin](https://img.shields.io/badge/Kotlin-1.3.21%2B-blue.svg)
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

val values = ArrayList<Int>()

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

val values = ArrayList<Int>()
data.subscribe(owner) { values += it }

val updatedValues = ArrayList<Int>()
// Observers added via `subscribeChanges` will only receive updated values.
data.subscribeChanges(owner) { updatedValues += it }

owner.registry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

data.value = 1

assertEquals(listOf(0, 1), values)
assertEquals(listOf(1), updatedValues)
```

## ViewModel

```kotlin
class FooActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModelProvider = createViewModelProvider()
        val fooViewModel: FooViewModel by viewModelProvider

        ...
    }
}
```

## Room

```kotlin
// Create a new Migration instance
val MIGRATION_1_2 = Migration(1, 2) { ... }

// Create a new RoomDatabase instance
val db = context.createRoomDatabase<MyRoomDatabase>("app.db") {
    addMigrations(MIGRATION_1_2)
}
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

    def aacktVersion = '2.0.0-alpha05'

    // LiveData and ViewModel
    implementation "com.github.tmurakami.aackt:lifecycle-extensions:$aacktVersion"

    // Alternatively, just LiveData
    implementation "com.github.tmurakami.aackt:lifecycle-livedata:$aacktVersion"

    // Alternatively, just ViewModel
    implementation "com.github.tmurakami.aackt:lifecycle-viewmodel:$aacktVersion"

    // Room
    implementation "com.github.tmurakami.aackt:room-runtime:$aacktVersion"

    // Room with RxJava2
    implementation "com.github.tmurakami.aackt:room-rxjava2:$aacktVersion"

}
```

## API references

### Lifecycle

- [Extensions](https://jitpack.io/com/github/tmurakami/aackt/lifecycle-extensions/2.0.0-alpha05/javadoc/lifecycle-extensions/)
- [LiveData](https://jitpack.io/com/github/tmurakami/aackt/lifecycle-livedata/2.0.0-alpha05/javadoc/lifecycle-livedata/)
- [ViewModel](https://jitpack.io/com/github/tmurakami/aackt/lifecycle-viewmodel/2.0.0-alpha05/javadoc/lifecycle-viewmodel/)

### Room

- [Runtime](https://jitpack.io/com/github/tmurakami/aackt/room-runtime/2.0.0-alpha05/javadoc/room-runtime/)
- [RxJava2](https://jitpack.io/com/github/tmurakami/aackt/room-rxjava2/2.0.0-alpha05/javadoc/room-rxjava2/)

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
