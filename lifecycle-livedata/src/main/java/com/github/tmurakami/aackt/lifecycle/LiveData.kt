/*
 * Copyright 2018 Tsuyoshi Murakami
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("NOTHING_TO_INLINE")

package com.github.tmurakami.aackt.lifecycle

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.Transformations
import android.support.annotation.MainThread
import java.util.LinkedList

/**
 * Returns a [LiveData] that emits [this] as a value.
 */
@MainThread
inline fun <T> T.toLiveData(): LiveData<T> {
    // To save method count, we prefer MutableLiveData rather than `object: LiveData() {}`.
    return MutableLiveData<T>().also { it.value = this }
}

/**
 * Adds the given [observer] to [this] and returns a registered [Observer]. The [observer] will
 * receive values until explicitly unregistered via the [LiveData.removeObserver].
 */
@MainThread
inline fun <T> LiveData<T>.addObserver(crossinline observer: (T) -> Unit): Observer<T> =
    addObserver(Observer {
        @Suppress("UNCHECKED_CAST")
        observer(it as T)
    })

/**
 * Adds the given [observer] to [this] and returns the [observer]. The [observer] will receive
 * values until explicitly unregistered via the [LiveData.removeObserver].
 */
@MainThread
inline fun <T> LiveData<T>.addObserver(observer: Observer<T>): Observer<T> =
    observer.also { observeForever(it) }

/**
 * Returns a [LiveData] that emits the results of applying the given [transform] function.
 */
@MainThread
inline fun <X, Y> LiveData<X>.map(crossinline transform: (X) -> Y): LiveData<Y> =
    Transformations.map(this) { transform(it) }

/**
 * Returns a [LiveData] whose source is switched by the given [transform] function.
 */
@MainThread
inline fun <X, Y> LiveData<X>.switchMap(crossinline transform: (X) -> LiveData<Y>?): LiveData<Y> =
    Transformations.switchMap(this) { transform(it) }

/**
 * Returns a [LiveData] with the given [onActive] function. The given function will be called when
 * the resulting [LiveData] becomes active.
 */
@MainThread
fun <T> LiveData<T>.doOnActive(onActive: () -> Unit): LiveData<T> =
    doOnLifecycle(onActive = onActive)

/**
 * Returns a [LiveData] with the given [onInactive] function. The given function will be called when
 * the resulting [LiveData] becomes inactive.
 */
@MainThread
fun <T> LiveData<T>.doOnInactive(onInactive: () -> Unit): LiveData<T> =
    doOnLifecycle(onInactive = onInactive)

private fun <T> LiveData<T>.doOnLifecycle(
    onActive: (() -> Unit)? = null,
    onInactive: (() -> Unit)? = null
): LiveData<T> {
    val result =
        if (this is LiveDataOnLifecycle) {
            this
        } else {
            val data = LiveDataOnLifecycle<T>()
            data.addSource(this) { data.value = it }
            data
        }
    onActive?.let { result.onActiveListeners += it }
    onInactive?.let { result.onInactiveListeners += it }
    return result
}

/**
 * Returns a [LiveData] with the given [onChanged] function. The given function will be called when
 * the resulting [LiveData] is changed.
 */
@MainThread
inline fun <T> LiveData<T>.doOnChanged(crossinline onChanged: (T) -> Unit): LiveData<T> {
    val result = MediatorLiveData<T>()
    result.addSource(this) {
        result.value = it
        @Suppress("UNCHECKED_CAST")
        onChanged(it as T)
    }
    return result
}

/**
 * Returns a [LiveData] that emits only values matching the given [predicate].
 */
@MainThread
inline fun <T> LiveData<T>.filter(crossinline predicate: (T) -> Boolean): LiveData<T> {
    val result = MediatorLiveData<T>()
    result.addSource(this) {
        @Suppress("UNCHECKED_CAST")
        if (predicate(it as T)) result.value = it
    }
    return result
}

/**
 * Returns a [LiveData] that emits only values not matching the given [predicate].
 */
@MainThread
inline fun <T> LiveData<T>.filterNot(crossinline predicate: (T) -> Boolean): LiveData<T> =
    filter { !predicate(it) }

/**
 * Returns a [LiveData] that emits only non-null values.
 */
@MainThread
inline fun <reified T : Any> LiveData<T?>.filterNotNull(): LiveData<T> = filterIsInstance()

/**
 * Returns a [LiveData] that emits only values of the given type [R]. If [R] is nullable type, null
 * will be notified, otherwise dropped.
 */
@MainThread
inline fun <reified R> LiveData<*>.filterIsInstance(): LiveData<R> {
    val result = MediatorLiveData<R>()
    result.addSource(this) { if (it is R) result.value = it }
    return result
}

/**
 * Returns a [LiveData] that emits only distinct values.
 */
@MainThread
inline fun <T> LiveData<T>.distinct(): LiveData<T> = distinctBy { it }

/**
 * Returns a [LiveData] that emits only values having distinct keys returned by the given
 * [selector].
 */
@MainThread
inline fun <T, K> LiveData<T>.distinctBy(crossinline selector: (T) -> K): LiveData<T> {
    val set = HashSet<K>()
    return filter { set.add(selector(it)) }
}

/**
 * Returns a [LiveData] that emits values except first [n] items.
 */
@MainThread
fun <T> LiveData<T>.drop(n: Int): LiveData<T> {
    var count = 0
    return dropWhile { count++ < n }
}

/**
 * Returns a [LiveData] that emits values except first items satisfying the given [predicate].
 */
@MainThread
inline fun <T> LiveData<T>.dropWhile(crossinline predicate: (T) -> Boolean): LiveData<T> {
    val result = MediatorLiveData<T>()
    var drop = true
    result.addSource(this) {
        @Suppress("UNCHECKED_CAST")
        if (drop) drop = predicate(it as T)
        if (!drop) result.value = it
    }
    return result
}

/**
 * Returns a [LiveData] that emits first [n] items.
 */
@MainThread
fun <T> LiveData<T>.take(n: Int): LiveData<T> {
    var count = 0
    return takeWhile { count++ < n }
}

/**
 * Returns a [LiveData] that emits first values satisfying the given [predicate].
 */
@MainThread
inline fun <T> LiveData<T>.takeWhile(crossinline predicate: (T) -> Boolean): LiveData<T> {
    val result = MediatorLiveData<T>()
    result.addSource(this) {
        @Suppress("UNCHECKED_CAST")
        if (predicate(it as T)) result.value = it else result.removeSource(this)
    }
    return result
}

/**
 * Returns a [LiveData] that emits values emitted by the sources.
 */
@MainThread
operator fun <T> LiveData<T>.plus(other: LiveData<out T>): LiveData<T> {
    val result = MediatorLiveData<T>()
    val observer = Observer<T> { result.value = it }
    result.addSource(this, observer)
    @Suppress("UNCHECKED_CAST")
    result.addSource(other as LiveData<T>, observer)
    return result
}

/**
 * Returns a [LiveData] that emits pairs of values emitted in sequence by the sources.
 */
@MainThread
fun <T, R> LiveData<T>.zip(other: LiveData<R>): LiveData<Pair<T, R>> = zip(other) { a, b -> a to b }

/**
 * Returns a [LiveData] that emits the results of applying the given [transform] function to values
 * emitted in sequence by the sources.
 */
@MainThread
inline fun <T, R, V> LiveData<T>.zip(
    other: LiveData<R>,
    crossinline transform: (a: T, b: R) -> V
): LiveData<V> {
    val result = MediatorLiveData<V>()
    val values = arrayOf<LinkedList<Any?>>(LinkedList(), LinkedList())
    val data = arrayOf(this, other)
    for (i in 0..1) {
        result.addSource(data[i]) {
            values[i].add(it)
            if (values.all { it.isNotEmpty() }) {
                @Suppress("UNCHECKED_CAST")
                result.value = transform(values[0].pop() as T, values[1].pop() as R)
            }
        }
    }
    return result
}

/**
 * Returns a [LiveData] that emits pairs of each two adjacent values emitted by [this].
 */
@MainThread
fun <T> LiveData<T>.zipWithNext(): LiveData<Pair<T, T>> = zipWithNext { a, b -> a to b }

/**
 * Returns a [LiveData] that emits the results of applying the given [transform] function to each
 * pair of two adjacent values emitted by [this].
 */
@MainThread
inline fun <T, R> LiveData<T>.zipWithNext(crossinline transform: (a: T, b: T) -> R): LiveData<R> {
    val result = MediatorLiveData<R>()
    var hasValue = false
    var previous: T? = null
    result.addSource(this) {
        if (hasValue) {
            @Suppress("UNCHECKED_CAST")
            result.value = transform(previous as T, it as T)
        } else {
            hasValue = true
        }
        previous = it
    }
    return result
}
