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

package com.github.tmurakami.aackt.lifecycle.livedata

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Observer
import android.support.annotation.MainThread

/**
 * Binds the given [source] to [this]. The given [observer] will receive values whenever the
 * [source] is changed.
 */
@MainThread
inline fun <T, S> MediatorLiveData<T>.bindSource(
    source: LiveData<S>,
    crossinline observer: (S) -> Unit
) = bindSource(source, Observer {
    @Suppress("UNCHECKED_CAST")
    observer(it as S)
})

/**
 * Binds the given [source] to [this]. The given [observer] will receive values whenever the
 * [source] is changed.
 */
@MainThread
inline fun <T, S> MediatorLiveData<T>.bindSource(source: LiveData<S>, observer: Observer<S>) =
    addSource(source, observer)

/**
 * Unbinds the given [source] from [this].
 */
@MainThread
inline fun <T> MediatorLiveData<T>.unbindSource(source: LiveData<*>) = removeSource(source)
