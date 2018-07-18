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

package com.github.tmurakami.aackt.lifecycle

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.Transformations
import android.support.annotation.MainThread
import java.util.LinkedList

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
 * Returns a [LiveData] that emits only values of the given type [R].
 *
 * If [R] is a nullable type then null will be notified otherwise null will be dropped.
 */
@Suppress("UNCHECKED_CAST")
@MainThread
inline fun <reified R> LiveData<*>.filterIsInstance(): LiveData<R> =
    filter { it is R } as LiveData<R>

/**
 * Returns a [LiveData] that emits only distinct values.
 *
 * Note that structurally equivalent values are regarded as identical. You can use [distinctBy] to
 * treat structurally equivalent values as different.
 */
@MainThread
fun <T> LiveData<T>.distinct(): LiveData<T> = distinctBy { it }

/**
 * Returns a [LiveData] that emits only distinct values according to the given [selector] function.
 *
 * Note that structurally equivalent keys are regarded as identical. To treat structurally
 * equivalent values emitted by the receiver [LiveData] as different, you will need to give a
 * [selector] that calls [System.identityHashCode], for instance
 * `distinctBy { System.identityHashCode(it) }`.
 */
@MainThread
inline fun <T, K> LiveData<T>.distinctBy(crossinline selector: (T) -> K): LiveData<T> {
    val set = HashSet<K>()
    return filter { set.add(selector(it)) }
}

/**
 * Returns a [LiveData] that emits only distinct contiguous values.
 *
 * Note that structurally equivalent values are regarded as identical. You can use
 * [distinctUntilChangedBy] to treat structurally equivalent values as different.
 */
@MainThread
fun <T> LiveData<T>.distinctUntilChanged(): LiveData<T> = distinctUntilChangedBy { it }

/**
 * Returns a [LiveData] that emits only distinct contiguous values according to the given [selector]
 * function.
 *
 * Note that structurally equivalent keys are regarded as identical. To treat structurally
 * equivalent values emitted by the receiver [LiveData] as different, you will need to give a
 * [selector] that calls [System.identityHashCode], for instance
 * `distinctUntilChangedBy { System.identityHashCode(it) }`.
 */
@MainThread
inline fun <T, K> LiveData<T>.distinctUntilChangedBy(crossinline selector: (T) -> K): LiveData<T> {
    val result = MediatorLiveData<T>()
    result.addSource(this, object : Observer<T> {
        private var lastKey: Any? = this // Not set
        override fun onChanged(t: T?) {
            @Suppress("UNCHECKED_CAST")
            val key = selector(t as T)
            if (key != lastKey) result.value = t
            lastKey = key
        }
    })
    return result
}

/**
 * Returns a [LiveData] that emits values except first [n] items.
 */
@MainThread
fun <T> LiveData<T>.drop(n: Int): LiveData<T> {
    val result = MediatorLiveData<T>()
    result.addSource(this, object : Observer<T> {
        private var count = 0
        override fun onChanged(t: T?) {
            if (++count > n) result.value = t
        }
    })
    return result
}

/**
 * Returns a [LiveData] that emits values except first items satisfying the given [predicate]
 * function.
 */
@MainThread
inline fun <T> LiveData<T>.dropWhile(crossinline predicate: (T) -> Boolean): LiveData<T> {
    val result = MediatorLiveData<T>()
    result.addSource(this, object : Observer<T> {
        private var drop = true
        override fun onChanged(t: T?) {
            var drop = drop
            @Suppress("UNCHECKED_CAST")
            if (drop) drop = predicate(t as T).also { this.drop = it }
            if (!drop) result.value = t
        }
    })
    return result
}

/**
 * Returns a [LiveData] that emits first [n] items.
 */
@MainThread
fun <T> LiveData<T>.take(n: Int): LiveData<T> {
    val result = MediatorLiveData<T>()
    result.addSource(this, object : Observer<T> {
        private var count = 0
        override fun onChanged(t: T?) {
            if (count++ < n) result.value = t else result.removeSource(this@take)
        }
    })
    return result
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
 * Returns a [LiveData] that emits pairs of values emitted by each [LiveData] when either of them
 * emits a value.
 *
 * Note that the resulting [LiveData] will not emit an initial value until both [LiveData] emit at
 * least one value.
 */
@MainThread
fun <T, R> LiveData<T>.combineLatest(other: LiveData<R>): LiveData<Pair<T, R>> =
    combineLatest(other) { a, b -> a to b }

/**
 * Returns a [LiveData] that emits the results of applying the given [transform] function to values
 * emitted by each [LiveData] when either of them emits a value.
 *
 * Note that the resulting [LiveData] will not emit an initial value until both [LiveData] emit at
 * least one value.
 */
@MainThread
inline fun <T, R, V> LiveData<T>.combineLatest(
    other: LiveData<R>,
    crossinline transform: (a: T, b: R) -> V
): LiveData<V> {
    val result = MediatorLiveData<V>()
    val sources = arrayOf(this, other)
    var emitted = 0
    for (i in 0..1) {
        result.addSource(sources[i]) {
            emitted = emitted or (i + 1)
            if (emitted == 0b11) {
                @Suppress("UNCHECKED_CAST")
                result.value = transform(value as T, other.value as R)
            }
        }
    }
    return result
}

/**
 * Returns a [LiveData] that emits pairs of values emitted by each [LiveData] when the receiver
 * [LiveData] emits a value.
 *
 * Note that the resulting [LiveData] will not emit an initial value until both [LiveData] emit at
 * least one value.
 */
@MainThread
fun <T, R> LiveData<T>.withLatestFrom(other: LiveData<R>): LiveData<Pair<T, R>> =
    withLatestFrom(other) { a, b -> a to b }

/**
 * Returns a [LiveData] that emits the results of applying the given [transform] function to values
 * emitted by each [LiveData] when the receiver [LiveData] emits a value.
 *
 * Note that the resulting [LiveData] will not emit an initial value until both [LiveData] emit at
 * least one value.
 */
@MainThread
inline fun <T, R, V> LiveData<T>.withLatestFrom(
    other: LiveData<R>,
    crossinline transform: (a: T, b: R) -> V
): LiveData<V> {
    val result = MediatorLiveData<V>()
    var emitted = false
    result.addSource(this) {
        @Suppress("UNCHECKED_CAST")
        if (emitted) result.value = transform(value as T, other.value as R)
    }
    result.addSource(other) { emitted = true }
    return result
}

/**
 * Returns a [LiveData] that emits pairs of values emitted in sequence by each [LiveData].
 *
 * Note that the resulting [LiveData] will not emit an initial value until both [LiveData] emit at
 * least one value.
 */
@MainThread
fun <T, R> LiveData<T>.zip(other: LiveData<R>): LiveData<Pair<T, R>> = zip(other) { a, b -> a to b }

/**
 * Returns a [LiveData] that emits the results of applying the given [transform] function to values
 * emitted in sequence by each [LiveData].
 *
 * Note that the resulting [LiveData] will not emit an initial value until both [LiveData] emit at
 * least one value.
 */
@MainThread
inline fun <T, R, V> LiveData<T>.zip(
    other: LiveData<R>,
    crossinline transform: (a: T, b: R) -> V
): LiveData<V> {
    val result = MediatorLiveData<V>()
    val sources = arrayOf(this, other)
    val values = arrayOf<LinkedList<Any?>>(LinkedList(), LinkedList())
    for (i in 0..1) {
        result.addSource(sources[i]) {
            values[i].plusAssign(it)
            if (values[i xor 1].isNotEmpty()) {
                @Suppress("UNCHECKED_CAST")
                result.value = transform(values[0].pop() as T, values[1].pop() as R)
            }
        }
    }
    return result
}

/**
 * Returns a [LiveData] that emits pairs of each two adjacent values emitted by the receiver
 * [LiveData].
 *
 * Note that the resulting [LiveData] will not emit an initial value until the receiver [LiveData]
 * emits at least two values.
 */
@MainThread
fun <T> LiveData<T>.zipWithNext(): LiveData<Pair<T, T>> = zipWithNext { a, b -> a to b }

/**
 * Returns a [LiveData] that emits the results of applying the given [transform] function to each
 * pair of two adjacent values emitted by the receiver [LiveData].
 *
 * Note that the resulting [LiveData] will not emit an initial value until the receiver [LiveData]
 * emits at least two values.
 */
@MainThread
inline fun <T, R> LiveData<T>.zipWithNext(crossinline transform: (a: T, b: T) -> R): LiveData<R> {
    val result = MediatorLiveData<R>()
    result.addSource(this, object : Observer<T> {
        private var lastValue: Any? = this // Not set
        override fun onChanged(t: T?) {
            val lastValue = lastValue.apply { lastValue = t }
            val notSet: Any = this // Avoid unnecessary CHECKCAST
            @Suppress("UNCHECKED_CAST")
            if (lastValue !== notSet) result.value = transform(lastValue as T, t as T)
        }
    })
    return result
}
