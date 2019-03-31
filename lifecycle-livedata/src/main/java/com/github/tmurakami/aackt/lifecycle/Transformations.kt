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

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import java.util.LinkedList

@Deprecated("", ReplaceWith("map(transform)", "androidx.lifecycle.map"))
@MainThread
inline fun <T, R> LiveData<T>.map(crossinline transform: (T) -> R): LiveData<R> =
    Transformations.map(this) { transform(it) }

/**
 * Returns a [LiveData] that emits the non-null results of applying the given [transform] function.
 *
 * @sample com.github.tmurakami.aackt.lifecycle.livedata.TransformationsTest.testMapNotNull
 */
@MainThread
inline fun <T, R : Any> LiveData<T>.mapNotNull(crossinline transform: (T) -> R?): LiveData<R> {
    val result = MediatorLiveData<R>()
    result.addSource(this) { transform(it)?.let(result::setValue) }
    return result
}

@Deprecated("", ReplaceWith("switchMap(transform)", "androidx.lifecycle.switchMap"))
@MainThread
inline fun <T, R> LiveData<T>.switchMap(crossinline transform: (T) -> LiveData<R>?): LiveData<R> =
    Transformations.switchMap(this) { transform(it) }

/**
 * Returns a [LiveData] with the given [action] which will be called when the resulting [LiveData]
 * becomes active.
 *
 * @sample com.github.tmurakami.aackt.lifecycle.livedata.TransformationsTest.testDoOnActive
 */
@MainThread
inline fun <T> LiveData<T>.doOnActive(crossinline action: () -> Unit): LiveData<T> =
    doOnActive(Runnable { action() })

/**
 * Returns a [LiveData] with the given [action] which will be called when the resulting [LiveData]
 * becomes active.
 *
 * @sample com.github.tmurakami.aackt.lifecycle.livedata.TransformationsTest.testDoOnActive_Runnable
 */
@MainThread
fun <T> LiveData<T>.doOnActive(action: Runnable): LiveData<T> {
    val result = this as? LiveDataOnLifecycle ?: LiveDataOnLifecycle(this)
    result.onActiveActions += action
    return result
}

/**
 * Returns a [LiveData] with the given [action] which will be called when the resulting [LiveData]
 * becomes inactive.
 *
 * @sample com.github.tmurakami.aackt.lifecycle.livedata.TransformationsTest.testDoOnInactive
 */
@MainThread
inline fun <T> LiveData<T>.doOnInactive(crossinline action: () -> Unit): LiveData<T> =
    doOnInactive(Runnable { action() })

/**
 * Returns a [LiveData] with the given [action] which will be called when the resulting [LiveData]
 * becomes inactive.
 *
 * @sample com.github.tmurakami.aackt.lifecycle.livedata.TransformationsTest.testDoOnInactive_Runnable
 */
@MainThread
fun <T> LiveData<T>.doOnInactive(action: Runnable): LiveData<T> {
    val result = this as? LiveDataOnLifecycle ?: LiveDataOnLifecycle(this)
    result.onInactiveActions += action
    return result
}

/**
 * Returns a [LiveData] with the given [action] which will be called when the value of the resulting
 * [LiveData] is changed.
 *
 * @sample com.github.tmurakami.aackt.lifecycle.livedata.TransformationsTest.testDoOnChanged
 */
@MainThread
inline fun <T> LiveData<T>.doOnChanged(crossinline action: (T) -> Unit): LiveData<T> {
    val result = MediatorLiveData<T>()
    result.addSource(this) {
        result.value = it
        action(it)
    }
    return result
}

/**
 * Returns a [LiveData] that emits only values matching the given [predicate] function.
 *
 * @sample com.github.tmurakami.aackt.lifecycle.livedata.TransformationsTest.testFilter
 */
@MainThread
inline fun <T> LiveData<T>.filter(crossinline predicate: (T) -> Boolean): LiveData<T> {
    val result = MediatorLiveData<T>()
    result.addSource(this) { if (predicate(it)) result.value = it }
    return result
}

/**
 * Returns a [LiveData] that emits only values not matching the given [predicate] function.
 *
 * @sample com.github.tmurakami.aackt.lifecycle.livedata.TransformationsTest.testFilterNot
 */
@MainThread
inline fun <T> LiveData<T>.filterNot(crossinline predicate: (T) -> Boolean): LiveData<T> =
    filter { predicate(it).not() }

/**
 * Returns a [LiveData] that emits only non-null values.
 *
 * @sample com.github.tmurakami.aackt.lifecycle.livedata.TransformationsTest.testFilterNotNull
 */
@Suppress("UNCHECKED_CAST")
@MainThread
fun <T : Any> LiveData<T?>.filterNotNull(): LiveData<T> = filter { it != null } as LiveData<T>

/**
 * Returns a [LiveData] that emits only values of the given type [R].
 *
 * If [R] is a nullable type then null will be emitted otherwise null will be dropped.
 *
 * @sample com.github.tmurakami.aackt.lifecycle.livedata.TransformationsTest.testFilterIsInstance
 */
@Suppress("UNCHECKED_CAST")
@MainThread
inline fun <reified R> LiveData<*>.filterIsInstance(): LiveData<R> =
    filter { it is R } as LiveData<R>

/**
 * Returns a [LiveData] that emits only distinct values according to the given [selector] function.
 *
 * Note that structurally equivalent keys are regarded as identical. To treat structurally
 * equivalent values emitted by the receiver [LiveData] as different, you will need to give a
 * [selector] that calls [System.identityHashCode], for instance
 * `distinct(System::identityHashCode)`.
 *
 * @sample com.github.tmurakami.aackt.lifecycle.livedata.TransformationsTest.testDistinct
 */
@MainThread
inline fun <T> LiveData<T>.distinct(crossinline selector: (T) -> Any? = { it }): LiveData<T> {
    val set = HashSet<Any?>()
    return filter { set.add(selector(it)) }
}

@Deprecated("", ReplaceWith("distinct(selector)"))
@MainThread
inline fun <T, K> LiveData<T>.distinctBy(crossinline selector: (T) -> K): LiveData<T> =
    distinct(selector)

@Deprecated("", ReplaceWith("distinctUntilChanged()", "androidx.lifecycle.distinctUntilChanged"))
@MainThread
fun <T> LiveData<T>.distinctUntilChanged(): LiveData<T> = distinctUntilChanged { it }

/**
 * Returns a [LiveData] that emits only distinct contiguous values according to the given [selector]
 * function.
 *
 * Note that structurally equivalent keys are regarded as identical. To treat structurally
 * equivalent values emitted by the receiver [LiveData] as different, you will need to give a
 * [selector] that calls [System.identityHashCode], for instance
 * `distinctUntilChanged(System::identityHashCode)`.
 *
 * @sample com.github.tmurakami.aackt.lifecycle.livedata.TransformationsTest.testDistinctUntilChanged
 */
@MainThread
inline fun <T> LiveData<T>.distinctUntilChanged(crossinline selector: (T) -> Any?): LiveData<T> {
    val result = MediatorLiveData<T>()
    result.addSource(this, object : Observer<T> {
        private var lastKey: Any? = /* NOT_SET */ this
        override fun onChanged(t: T) {
            val key = selector(t)
            if (key != lastKey) result.value = t
            lastKey = key
        }
    })
    return result
}

@Deprecated("", ReplaceWith("distinctUntilChanged(selector)"))
@MainThread
inline fun <T, K> LiveData<T>.distinctUntilChangedBy(crossinline selector: (T) -> K): LiveData<T> =
    distinctUntilChanged(selector)

/**
 * Returns a [LiveData] that emits values except first [n] items.
 *
 * @sample com.github.tmurakami.aackt.lifecycle.livedata.TransformationsTest.testDrop
 */
@MainThread
fun <T> LiveData<T>.drop(n: Int): LiveData<T> {
    val result = MediatorLiveData<T>()
    result.addSource(this, object : Observer<T> {
        private var count = 0
        override fun onChanged(t: T) {
            if (++count > n) result.value = t
        }
    })
    return result
}

/**
 * Returns a [LiveData] that emits values except first items satisfying the given [predicate]
 * function.
 *
 * @sample com.github.tmurakami.aackt.lifecycle.livedata.TransformationsTest.testDropWhile
 */
@MainThread
inline fun <T> LiveData<T>.dropWhile(crossinline predicate: (T) -> Boolean): LiveData<T> {
    val result = MediatorLiveData<T>()
    result.addSource(this, object : Observer<T> {
        private var drop = true
        override fun onChanged(t: T) {
            var drop = drop
            if (drop) drop = predicate(t).also { this.drop = it }
            if (drop.not()) result.value = t
        }
    })
    return result
}

/**
 * Returns a [LiveData] that emits first [n] items.
 *
 * @sample com.github.tmurakami.aackt.lifecycle.livedata.TransformationsTest.testTake
 */
@MainThread
fun <T> LiveData<T>.take(n: Int): LiveData<T> {
    val result = MediatorLiveData<T>()
    result.addSource(this, object : Observer<T> {
        private var count = 0
        override fun onChanged(t: T) {
            if (count++ < n) result.value = t else result.removeSource(this@take)
        }
    })
    return result
}

/**
 * Returns a [LiveData] that emits first values satisfying the given [predicate] function.
 *
 * @sample com.github.tmurakami.aackt.lifecycle.livedata.TransformationsTest.testTakeWhile
 */
@MainThread
inline fun <T> LiveData<T>.takeWhile(crossinline predicate: (T) -> Boolean): LiveData<T> {
    val result = MediatorLiveData<T>()
    result.addSource(this) { if (predicate(it)) result.value = it else result.removeSource(this) }
    return result
}

/**
 * Returns a [LiveData] that emits values emitted by the sources.
 *
 * @sample com.github.tmurakami.aackt.lifecycle.livedata.TransformationsTest.testPlus
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
 *
 * @sample com.github.tmurakami.aackt.lifecycle.livedata.TransformationsTest.testCombineLatest
 */
@MainThread
fun <T, R> LiveData<T>.combineLatest(other: LiveData<R>): LiveData<Pair<T, R>> =
    combineLatest(other, ::Pair)

/**
 * Returns a [LiveData] that emits the results of applying the given [transform] function to values
 * emitted by each [LiveData] when either of them emits a value.
 *
 * Note that the resulting [LiveData] will not emit an initial value until both [LiveData] emit at
 * least one value.
 *
 * @sample com.github.tmurakami.aackt.lifecycle.livedata.TransformationsTest.testCombineLatest_transform
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
 *
 * @sample com.github.tmurakami.aackt.lifecycle.livedata.TransformationsTest.testWithLatestFrom
 */
@MainThread
fun <T, R> LiveData<T>.withLatestFrom(other: LiveData<R>): LiveData<Pair<T, R>> =
    withLatestFrom(other, ::Pair)

/**
 * Returns a [LiveData] that emits the results of applying the given [transform] function to values
 * emitted by each [LiveData] when the receiver [LiveData] emits a value.
 *
 * Note that the resulting [LiveData] will not emit an initial value until both [LiveData] emit at
 * least one value.
 *
 * @sample com.github.tmurakami.aackt.lifecycle.livedata.TransformationsTest.testWithLatestFrom_transform
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
        if (emitted) result.value = transform(it, other.value as R)
    }
    result.addSource(other) {
        emitted = true
        result.removeSource(other)
    }
    return result
}

/**
 * Returns a [LiveData] that emits pairs of values emitted in sequence by each [LiveData].
 *
 * Note that the resulting [LiveData] will not emit an initial value until both [LiveData] emit at
 * least one value.
 *
 * @sample com.github.tmurakami.aackt.lifecycle.livedata.TransformationsTest.testZip
 */
@MainThread
fun <T, R> LiveData<T>.zip(other: LiveData<R>): LiveData<Pair<T, R>> = zip(other, ::Pair)

/**
 * Returns a [LiveData] that emits the results of applying the given [transform] function to values
 * emitted in sequence by each [LiveData].
 *
 * Note that the resulting [LiveData] will not emit an initial value until both [LiveData] emit at
 * least one value.
 *
 * @sample com.github.tmurakami.aackt.lifecycle.livedata.TransformationsTest.testZip_transform
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
 *
 * @sample com.github.tmurakami.aackt.lifecycle.livedata.TransformationsTest.testZipWithNext
 */
@MainThread
fun <T> LiveData<T>.zipWithNext(): LiveData<Pair<T, T>> = zipWithNext(::Pair)

/**
 * Returns a [LiveData] that emits the results of applying the given [transform] function to each
 * pair of two adjacent values emitted by the receiver [LiveData].
 *
 * Note that the resulting [LiveData] will not emit an initial value until the receiver [LiveData]
 * emits at least two values.
 *
 * @sample com.github.tmurakami.aackt.lifecycle.livedata.TransformationsTest.testZipWithNext_transform
 */
@MainThread
inline fun <T, R> LiveData<T>.zipWithNext(crossinline transform: (a: T, b: T) -> R): LiveData<R> {
    val result = MediatorLiveData<R>()
    result.addSource(this, object : Observer<T> {
        private var lastValue: Any? = /* NOT_SET */ this
        override fun onChanged(t: T) {
            val lastValue = lastValue.apply { lastValue = t }
            @Suppress("UNCHECKED_CAST")
            if (lastValue !== /* NOT_SET */ this) result.value = transform(lastValue as T, t)
        }
    })
    return result
}
