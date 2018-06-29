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
import android.arch.lifecycle.version
import android.support.annotation.MainThread

/**
 * Creates a [LiveData] whose value is the given [value].
 */
@MainThread
inline fun <T> liveData(value: T): LiveData<T> {
    // To save methods count, we prefer MutableLiveData rather than `object : LiveData<T>() {}`.
    return mutableLiveData(value)
}

/**
 * Adds the given [observer] callback to the receiver. If the receiver already has a value, it will
 * first be notified to the callback.
 *
 * To stop observing the receiver, you will need to call [Observation.dispose] with the resulting
 * [Observation] of this extension.
 */
@MainThread
fun <T> LiveData<T>.observe(observer: (T) -> Unit): Observation =
    ObservationImpl(this, observer).also { observeForever(it) }

/**
 * Adds the given [onChanged] callback to the receiver. Unlike [observe] extension, the callback
 * will only receive updated values after calling this extension.
 *
 * To stop observing the receiver, you will need to call [Observation.dispose] with the resulting
 * [Observation] of this extension.
 */
@MainThread
fun <T> LiveData<T>.observeChanges(onChanged: (T) -> Unit): Observation {
    val startVersion = version
    return observe { if (version > startVersion) onChanged(it) }
}

/**
 * Adds the given [observer] callback to the receiver. If the receiver already has a value, it will
 * first be notified to the callback.
 *
 * The callback will receive values only while the given [owner] is active. You can manually stop
 * observing by calling [Observation.dispose] with the resulting [Observation] of this extension.
 */
@MainThread
fun <T> LiveData<T>.observe(owner: LifecycleOwner, observer: (T) -> Unit): Observation =
    ObservationImpl(this, observer).also { observe(owner, it) }

/**
 * Adds the given [onChanged] callback to the receiver. Unlike [observe] extension, the callback
 * will only receive updated values after calling this extension.
 *
 * The callback will receive values only while the given [owner] is active. You can manually stop
 * observing by calling [Observation.dispose] with the resulting [Observation] of this extension.
 */
@MainThread
fun <T> LiveData<T>.observeChanges(owner: LifecycleOwner, onChanged: (T) -> Unit): Observation {
    val startVersion = version
    return observe(owner) { if (version > startVersion) onChanged(it) }
}
