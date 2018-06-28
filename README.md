# AAC.kt

[![CircleCI](https://circleci.com/gh/tmurakami/aackt.svg?style=shield)](https://circleci.com/gh/tmurakami/aackt)
[![Release](https://jitpack.io/v/tmurakami/aackt.svg)](https://jitpack.io/#tmurakami/aackt)
![Kotlin](https://img.shields.io/badge/Kotlin-1.2.50%2B-blue.svg)
![Android](https://img.shields.io/badge/Android-4.0%2B-blue.svg)

A Kotlin library for [Android Architecture Components](https://developer.android.com/topic/libraries/architecture/).

## LiveData

```kotlin
val owner = object : LifecycleOwner {
    val registry = LifecycleRegistry(this)
    override fun getLifecycle(): Lifecycle = registry
}

val data = MutableLiveData<Any?>()

val received = ArrayList<Int>()

data.filterIsInstance<Int>() // Filter out non-int values
    .dropWhile { it < 3 } // Drop values less than 3
    .observe(owner) { received += it }

owner.registry.markState(Lifecycle.State.RESUMED)

data.value = 1
data.value = 2
data.value = null
data.value = 3
data.value = Unit
data.value = 4

assertEquals(listOf(3, 4), received)
```

If a LiveData already has a value, the observer added via `observe` will
receive that value first. If you would not like to receive it, you can
use `observeChanges` to receive only updated values.

```kotlin
val owner = object : LifecycleOwner {
    val registry = LifecycleRegistry(this)
    override fun getLifecycle(): Lifecycle = registry
}

// Create a LiveData with a value
val data = MutableLiveData<Int>().apply { value = -1 }

val received = ArrayList<Int>()

// Add an observer to receive only updated values
data.observeChanges(owner) { received += it }
data.value = 0

owner.registry.markState(Lifecycle.State.RESUMED)

data.value = 1

assertEquals(listOf(0, 1), received)
```

## ViewModel

```kotlin
class FooActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fooViewModel = viewModelProvider().get<FooViewModel>()

        ...
    }
}
```

You can also use a ViewModelProvider as the delegate for a delegated
property.

```kotlin
val fooViewModel: FooViewModel by viewModelProvider
```

## Room

```kotlin
// Create a new Migration instance
val MIGRATION_1_2 = migration(1, 2) { ... }

// Create a new RoomDatabase instance
val db = context.databaseBuilder<MyRoomDatabase>("app.db")
    .addMigrations(MIGRATION_1_2)
    .build()
```

## Paging

```kotlin
val dsFactory: DataSource.Factory<Int, MyData> = ...

// Create a new LiveData<PagedList> instance
val liveData = dsFactory.livePagedListBuilder(10).build()

// Create a new Observable<PagedList> instance
val observable = dsFactory.rxPagedListBuilder(10).buildObservable()
```

## WorkManager

```kotlin
class MyWorker : Worker() {
    ...
}

val workRequest = OneTimeWorkRequestBuilder<MyWorker>().build()

// `workManager` is a top-level property that returns the singleton
// instance of `WorkManager`.
workManager.enqueue(workRequest)
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

    def aacktVersion = '1.0.1'

    // LiveData and ViewModel
    implementation "com.github.tmurakami.aackt:lifecycle-extensions:$aacktVersion"

    // Alternatively, just LiveData
    implementation "com.github.tmurakami.aackt:lifecycle-livedata:$aacktVersion"

    // Alternatively, just ViewModel
    implementation "com.github.tmurakami.aackt:lifecycle-viewmodel:$aacktVersion"

    // ReactiveStreams support for LiveData
    implementation "com.github.tmurakami.aackt:lifecycle-reactivestreams:$aacktVersion"

    // Room
    implementation "com.github.tmurakami.aackt:persistence-room-runtime:$aacktVersion"

    // Paging
    implementation "com.github.tmurakami.aackt:paging-runtime:$aacktVersion"
    
    // Paging with RxJava2
    implementation "com.github.tmurakami.aackt:paging-rxjava2:$aacktVersion"

    // WorkManager
    implementation "com.github.tmurakami.aackt:work-runtime:$aacktVersion"

}
```

## API references

### Lifecycle

- [Extensions](https://jitpack.io/com/github/tmurakami/aackt/lifecycle-extensions/1.0.1/javadoc/lifecycle-extensions/)
- [LiveData](https://jitpack.io/com/github/tmurakami/aackt/lifecycle-livedata/1.0.1/javadoc/lifecycle-livedata/)
- [ViewModel](https://jitpack.io/com/github/tmurakami/aackt/lifecycle-viewmodel/1.0.1/javadoc/lifecycle-viewmodel/)
- [Reactive Streams](https://jitpack.io/com/github/tmurakami/aackt/lifecycle-reactivestreams/1.0.1/javadoc/lifecycle-reactivestreams/)

### Room

- [Runtime](https://jitpack.io/com/github/tmurakami/aackt/persistence-room-runtime/1.0.1/javadoc/persistence-room-runtime/)

### Paging

- [Runtime](https://jitpack.io/com/github/tmurakami/aackt/paging-runtime/1.0.1/javadoc/paging-runtime/)
- [RxJava2](https://jitpack.io/com/github/tmurakami/aackt/paging-rxjava2/1.0.1/javadoc/paging-rxjava2/)

### WorkManager

- [Runtime](https://jitpack.io/com/github/tmurakami/aackt/work-runtime/1.0.1/javadoc/work-runtime/)

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
