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
import android.arch.lifecycle.Observer
import android.support.annotation.MainThread

/**
 * Binds the given [data] to this and returns the registered [Observer]. The [observer] will receive
 * values only while [this] is active. You can manually stop observing by calling
 * [LiveData.removeObserver] with the resulting [Observer].
 */
@MainThread
inline fun <T> LifecycleOwner.bindLiveData(
    data: LiveData<T>,
    crossinline observer: (T) -> Unit
): Observer<T> = bindLiveData(data, Observer {
    @Suppress("UNCHECKED_CAST")
    observer(it as T)
})

/**
 * Binds the given [data] to this and returns the [observer]. The [observer] will receive values
 * only while [this] is active. You can manually stop observing by calling [LiveData.removeObserver]
 * with the resulting [Observer].
 */
@MainThread
inline fun <T> LifecycleOwner.bindLiveData(data: LiveData<T>, observer: Observer<T>): Observer<T> =
    observer.also { data.observe(this, it) }

/**
 * Unbinds the given [data] from [this].
 */
@MainThread
inline fun <T> LifecycleOwner.unbindLiveData(data: LiveData<T>) = data.removeObservers(this)
