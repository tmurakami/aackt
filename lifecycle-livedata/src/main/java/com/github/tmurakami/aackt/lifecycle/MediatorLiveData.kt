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
import android.arch.lifecycle.Observer
import android.support.annotation.MainThread

@Deprecated("", ReplaceWith("mediatorLiveData(value)"))
@Suppress("FunctionName")
@MainThread
inline fun <T> MediatorLiveData(value: T): MediatorLiveData<T> = mediatorLiveData(value)

/**
 * Creates a [MediatorLiveData] whose value is the given [value].
 */
@MainThread
inline fun <T> mediatorLiveData(value: T): MediatorLiveData<T> =
    MediatorLiveData<T>().also { it.value = value }

@Deprecated("", ReplaceWith("observeSource(source, onChanged)"))
@MainThread
inline fun <S> MediatorLiveData<*>.bindSource(
    source: LiveData<S>,
    crossinline onChanged: (S) -> Unit
) = observeSource(source, Observer {
    @Suppress("UNCHECKED_CAST")
    onChanged(it as S)
})

/**
 * Start observing the given [source]. The [onChanged] callback will receive values whenever the
 * [source] is changed. The callback will be called only when [this] is active.
 */
@MainThread
inline fun <S> MediatorLiveData<*>.observeSource(
    source: LiveData<S>,
    crossinline onChanged: (S) -> Unit
) = observeSource(source, Observer {
    @Suppress("UNCHECKED_CAST")
    onChanged(it as S)
})

@Deprecated("", ReplaceWith("observeSource(source, onChanged)"))
@MainThread
inline fun <S> MediatorLiveData<*>.bindSource(source: LiveData<S>, onChanged: Observer<S>) =
    addSource(source, onChanged)

/**
 * Start observing the given [source]. The [onChanged] callback will receive values whenever the
 * [source] is changed. The callback will be called only when [this] is active.
 */
@MainThread
inline fun <S> MediatorLiveData<*>.observeSource(source: LiveData<S>, onChanged: Observer<S>) =
    addSource(source, onChanged)

@Deprecated("", ReplaceWith("removeSource(source)"))
@MainThread
inline fun MediatorLiveData<*>.unbindSource(source: LiveData<*>) = removeSource(source)
