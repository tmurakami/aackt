# AAC.kt

[![CircleCI](https://circleci.com/gh/tmurakami/aackt.svg?style=shield)](https://circleci.com/gh/tmurakami/aackt)
[![Release](https://jitpack.io/v/tmurakami/aackt.svg)](https://jitpack.io/#tmurakami/aackt)
![Kotlin](https://img.shields.io/badge/Kotlin-1.2.41%2B-blue.svg)
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
val dsFactory: DataSource.Factory<Int, MyData> = ...

// With LiveData
val livePagedList = dsFactory.livePagedListBuilder(10).build()

// With RxJava2
val rxPagedList = dsFactory.rxPagedListBuilder(10).buildObservable()
```

## WorkManager

```kotlin
class MyWorker : Worker() {
    ...
}

val workRequest = OneTimeWorkRequestBuilder<MyWorker>().build()
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
    
    // Paging with RxJava2
    implementation 'com.github.tmurakami.aackt:paging-rxjava2:x.y.z'

    // WorkManager
    implementation 'com.github.tmurakami.aackt:work-runtime:x.y.z'

}
```

[![Release](https://jitpack.io/v/tmurakami/aackt.svg)](https://jitpack.io/#tmurakami/aackt)

## API references

### Lifecycle

- [Extensions](https://jitpack.io/com/github/tmurakami/aackt/lifecycle-extensions/0.8.1/javadoc/lifecycle-extensions/)
- [LiveData](https://jitpack.io/com/github/tmurakami/aackt/lifecycle-livedata/0.8.1/javadoc/lifecycle-livedata/)
- [ViewModel](https://jitpack.io/com/github/tmurakami/aackt/lifecycle-viewmodel/0.8.1/javadoc/lifecycle-viewmodel/)
- [Reactive Streams](https://jitpack.io/com/github/tmurakami/aackt/lifecycle-reactivestreams/0.8.1/javadoc/lifecycle-reactivestreams/)

### Room

- [Runtime](https://jitpack.io/com/github/tmurakami/aackt/persistence-room-runtime/0.8.1/javadoc/persistence-room-runtime/)

### Paging

- [Runtime](https://jitpack.io/com/github/tmurakami/aackt/paging-runtime/0.8.1/javadoc/paging-runtime/)
- [RxJava2](https://jitpack.io/com/github/tmurakami/aackt/paging-rxjava2/0.8.1/javadoc/paging-rxjava2/)

### WorkManager

- [Runtime](https://jitpack.io/com/github/tmurakami/aackt/work-runtime/0.8.1/javadoc/work-runtime/)

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
