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

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.currentVersion
import android.support.annotation.MainThread
import java.util.LinkedList

/**
 * Creates a [LiveData] whose value is the given [value].
 */
@MainThread
inline fun <T> liveData(value: T): LiveData<T> {
    // To save method count, we prefer MutableLiveData rather than `object : LiveData<T>() {}`.
    return mutableLiveData(value)
}

/**
 * Adds the given [observer] callback to this [LiveData]. If this [LiveData] already has a value, it
 * will first be notified to the callback.
 *
 * To stop observing this [LiveData], you need to call [Observation.dispose] with the resulting
 * [Observation] of this extension.
 */
@MainThread
fun <T> LiveData<T>.observe(observer: (T) -> Unit): Observation =
    ObservationImpl(this, observer).also { observeForever(it) }

/**
 * Adds the given [onChanged] callback to this [LiveData]. Unlike [observe] extension, the callback
 * will only receive updated values after calling this extension.
 *
 * To stop observing this [LiveData], you need to call [Observation.dispose] with the resulting
 * [Observation] of this extension.
 */
@MainThread
fun <T> LiveData<T>.observeChanges(onChanged: (T) -> Unit): Observation {
    val startVersion = currentVersion
    return observe { if (currentVersion > startVersion) onChanged(it) }
}

/**
 * Adds the given [observer] callback to this [LiveData]. If this [LiveData] already has a value, it
 * will first be notified to the callback.
 *
 * The callback will receive values only while the given [owner] is active. You can manually stop
 * observing by calling [Observation.dispose] with the resulting [Observation] of this extension.
 */
@MainThread
fun <T> LiveData<T>.observe(owner: LifecycleOwner, observer: (T) -> Unit): Observation =
    ObservationImpl(this, observer).also { observe(owner, it) }

/**
 * Adds the given [onChanged] callback to this [LiveData]. Unlike [observe] extension, the callback
 * will only receive updated values after calling this extension.
 *
 * The callback will receive values only while the given [owner] is active. You can manually stop
 * observing by calling [Observation.dispose] with the resulting [Observation] of this extension.
 */
@MainThread
fun <T> LiveData<T>.observeChanges(
    owner: LifecycleOwner,
    onChanged: (T) -> Unit
): Observation {
    val startVersion = currentVersion
    return observe(owner) { if (currentVersion > startVersion) onChanged(it) }
}

/**
 * Returns a [LiveData] that emits the results of applying the given [transform] function.
 */
@MainThread
inline fun <T, R> LiveData<T>.map(crossinline transform: (T) -> R): LiveData<R> =
    Transformations.map(this) { transform(it) }

/**
 * Returns a [LiveData] that emits the non-null results of applying the given [transform] function.
 */
@MainThread
inline fun <T, R : Any> LiveData<T>.mapNotNull(crossinline transform: (T) -> R?): LiveData<R> {
    val result = MediatorLiveData<R>()
    result.addSource(this) {
        @Suppress("UNCHECKED_CAST")
        transform(it as T)?.let { result.value = it }
    }
    return result
}

/**
 * Returns a [LiveData] whose source is switched by the given [transform] function.
 */
@MainThread
inline fun <T, R> LiveData<T>.switchMap(crossinline transform: (T) -> LiveData<R>?): LiveData<R> =
    Transformations.switchMap(this) { transform(it) }

/**
 * Returns a [LiveData] with the given [onActive] function. The given function will be called when
 * the resulting [LiveData] becomes active.
 */
@MainThread
fun <T> LiveData<T>.doOnActive(onActive: () -> Unit): LiveData<T> {
    val result = this as? LiveDataOnLifecycle ?: LiveDataOnLifecycle(this)
    result.onActiveListeners += onActive
    return result
}

/**
 * Returns a [LiveData] with the given [onInactive] function. The given function will be called when
 * the resulting [LiveData] becomes inactive.
 */
@MainThread
fun <T> LiveData<T>.doOnInactive(onInactive: () -> Unit): LiveData<T> {
    val result = this as? LiveDataOnLifecycle ?: LiveDataOnLifecycle(this)
    result.onInactiveListeners += onInactive
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
 * Returns a [LiveData] that emits only values matching the given [predicate] function.
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
 * Returns a [LiveData] that emits only values not matching the given [predicate] function.
 */
@MainThread
inline fun <T> LiveData<T>.filterNot(crossinline predicate: (T) -> Boolean): LiveData<T> =
    filter { !predicate(it) }

/**
 * Returns a [LiveData] that emits only non-null values.
 */
@Suppress("UNCHECKED_CAST")
@MainThread
fun <T : Any> LiveData<T?>.filterNotNull(): LiveData<T> = filter { it != null } as LiveData<T>

/**
 * Returns a [LiveData] that emits only values of the given type [R]. If [R] is nullable type, null
 * will be notified, otherwise dropped.
 */
@Suppress("UNCHECKED_CAST")
@MainThread
inline fun <reified R> LiveData<*>.filterIsInstance(): LiveData<R> =
    filter { it is R } as LiveData<R>

/**
 * Returns a [LiveData] that emits only distinct values.
 */
@MainThread
fun <T> LiveData<T>.distinct(): LiveData<T> = distinctBy { it }

/**
 * Returns a [LiveData] that emits only distinct values according to the given [selector] function.
 */
@MainThread
inline fun <T, K> LiveData<T>.distinctBy(crossinline selector: (T) -> K): LiveData<T> {
    val set = HashSet<K>()
    return filter { set.add(selector(it)) }
}

/**
 * Returns a [LiveData] that emits only distinct contiguous values.
 */
@MainThread
fun <T> LiveData<T>.distinctUntilChanged(): LiveData<T> = distinctUntilChangedBy { it }

private val NOT_SET = Any()

/**
 * Returns a [LiveData] that emits only distinct contiguous values according to the given [selector]
 * function.
 */
@MainThread
fun <T, K> LiveData<T>.distinctUntilChangedBy(selector: (T) -> K): LiveData<T> {
    var previous: Any? = NOT_SET
    return filter { previous != selector(it).also { previous = it } }
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
 * Returns a [LiveData] that emits values except first items satisfying the given [predicate]
 * function.
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
 * Returns a [LiveData] that emits first values satisfying the given [predicate] function.
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
 * Returns a [LiveData] that emits pairs of each two adjacent values emitted by this [LiveData].
 */
@MainThread
fun <T> LiveData<T>.zipWithNext(): LiveData<Pair<T, T>> = zipWithNext { a, b -> a to b }

/**
 * Returns a [LiveData] that emits the results of applying the given [transform] function to each
 * pair of two adjacent values emitted by this [LiveData].
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
