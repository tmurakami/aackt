# AAC.kt

[![CircleCI](https://circleci.com/gh/tmurakami/aackt.svg?style=shield)](https://circleci.com/gh/tmurakami/aackt)
[![Release](https://jitpack.io/v/tmurakami/aackt.svg)](https://jitpack.io/#tmurakami/aackt)
![Kotlin](https://img.shields.io/badge/Kotlin-1.2.21%2B-blue.svg)
![Android](https://img.shields.io/badge/Android-4.0%2B-blue.svg)

A Kotlin library for [Android Architecture Components](https://developer.android.com/topic/libraries/architecture/).

## LiveData

```kotlin
val owner = object : LifecycleOwner {
    val registry = LifecycleRegistry(this)
    override fun getLifecycle(): Lifecycle = registry
}

val src = MutableLiveData<Any?>()

// Filter out non-int values
val filtered = src.filterIsInstance<Int>()

// Drop values less than 3
val dropped = filtered.dropWhile { it < 3 }

val received = ArrayList<Int>()

// Observe the LiveData
owner.bindLiveData(dropped) { received += it }

owner.registry.markState(Lifecycle.State.RESUMED)

src.value = 1
src.value = 2
src.value = null
src.value = 3
src.value = Unit
src.value = 4

assertEquals(listOf(3, 4), received)
```

## ViewModel

```kotlin
class FooActivity : FragmentActivity() {

    private lateinit var fooViewModel: FooViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fooViewModel = viewModelProvider().get()

        ...
    }
}
```

You can also use a ViewModelProvider as the delegate for a delegated
property.

```kotlin
class ViewModels(viewModelProvider: ViewModelProvider) {
    val fooViewModel: FooViewModel by viewModelProvider
}
```

## Room

```kotlin
val MIGRATION_1_2 = migration(1, 2) {
    it.execSQL(...)
}

val db =
    context.databaseBuilder<MyDatabase>("app.db")
        .addMigrations(MIGRATION_1_2)
        .build()
```

## Paging

```kotlin
val dataSourceFactory: DataSource.Factory<Int, MyData> = ...

val livePagedList = dataSourceFactory.livePagedListBuilder(10).build()
```

## Installation

Add the following to your `build.gradle`:

```groovy
repositories {
    google()
    jcenter()
    maven { url 'https://jitpack.io' }
}

dependencies {

    // LiveData and ViewModel
    implementation 'com.github.tmurakami.aackt:lifecycle-extensions:x.y.z'

    // Alternatively, just LiveData
    implementation 'com.github.tmurakami.aackt:lifecycle-livedata:x.y.z'

    // Alternatively, just ViewModel
    implementation 'com.github.tmurakami.aackt:lifecycle-viewmodel:x.y.z'

    // ReactiveStreams support for LiveData
    implementation 'com.github.tmurakami.aackt:lifecycle-reactivestreams:x.y.z'

    // Room
    implementation 'com.github.tmurakami.aackt:persistence-room-runtime:x.y.z'

    // Paging
    implementation 'com.github.tmurakami.aackt:paging-runtime:x.y.z'

}
```

[![Release](https://jitpack.io/v/tmurakami/aackt.svg)](https://jitpack.io/#tmurakami/aackt)

## API references

### Lifecycle

- [Extensions](https://jitpack.io/com/github/tmurakami/aackt/lifecycle-extensions/0.5.0/javadoc/lifecycle-extensions/)
- [LiveData](https://jitpack.io/com/github/tmurakami/aackt/lifecycle-livedata/0.5.0/javadoc/lifecycle-livedata/)
- [ViewModel](https://jitpack.io/com/github/tmurakami/aackt/lifecycle-viewmodel/0.5.0/javadoc/lifecycle-viewmodel/)
- [Reactive Streams](https://jitpack.io/com/github/tmurakami/aackt/lifecycle-reactivestreams/0.5.0/javadoc/lifecycle-reactivestreams/)

### Room

- [Runtime](https://jitpack.io/com/github/tmurakami/aackt/persistence-room-runtime/0.5.0/javadoc/persistence-room-runtime/)

### Paging

- [Runtime](https://jitpack.io/com/github/tmurakami/aackt/paging-runtime/0.5.0/javadoc/paging-runtime/)

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
