# AAC.kt

[![CircleCI](https://circleci.com/gh/tmurakami/aackt.svg?style=shield)](https://circleci.com/gh/tmurakami/aackt)
[![Release](https://jitpack.io/v/tmurakami/aackt.svg)](https://jitpack.io/#tmurakami/aackt)
![Kotlin](https://img.shields.io/badge/Kotlin-1.3.20%2B-blue.svg)
![Android](https://img.shields.io/badge/Android-4.0%2B-blue.svg)

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

// Create a LiveData with a value
val data = MutableLiveData(-1)

// The observer added via `subscribe` will receive the current value.
val values = ArrayList<Int>()
data.subscribe(owner) { values += it }

// The observer added via `subscribeChanges` won't receive the current value.
val updatedValues = ArrayList<Int>()
data.subscribeChanges(owner) { updatedValues += it }

owner.registry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

data.value = 0

assertEquals(listOf(-1, 0), values)
assertEquals(listOf(0), updatedValues) // `-1` won't be received
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
val db = context.databaseBuilder<MyRoomDatabase>("app.db")
    .addMigrations(MIGRATION_1_2)
    .build()
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

    def aacktVersion = '2.0.0-alpha04'

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

- [Extensions](https://jitpack.io/com/github/tmurakami/aackt/lifecycle-extensions/2.0.0-alpha04/javadoc/lifecycle-extensions/)
- [LiveData](https://jitpack.io/com/github/tmurakami/aackt/lifecycle-livedata/2.0.0-alpha04/javadoc/lifecycle-livedata/)
- [ViewModel](https://jitpack.io/com/github/tmurakami/aackt/lifecycle-viewmodel/2.0.0-alpha04/javadoc/lifecycle-viewmodel/)

### Room

- [Runtime](https://jitpack.io/com/github/tmurakami/aackt/room-runtime/2.0.0-alpha04/javadoc/room-runtime/)
- [RxJava2](https://jitpack.io/com/github/tmurakami/aackt/room-rxjava2/2.0.0-alpha04/javadoc/room-rxjava2/)

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
