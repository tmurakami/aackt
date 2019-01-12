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

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

@Deprecated(
    "",
    ReplaceWith(
        "MediatorLiveData<T>().also { it.value = value }",
        "androidx.lifecycle.MediatorLiveData"
    )
)
@MainThread
inline fun <T> mediatorLiveData(value: T): MediatorLiveData<T> =
    MediatorLiveData<T>().also { it.value = value }

@Deprecated("", ReplaceWith("addSource(source, onChanged)"))
@MainThread
inline fun <S> MediatorLiveData<*>.observeSource(
    source: LiveData<S>,
    crossinline onChanged: (S) -> Unit
) = addSource(source) { onChanged(it) }
