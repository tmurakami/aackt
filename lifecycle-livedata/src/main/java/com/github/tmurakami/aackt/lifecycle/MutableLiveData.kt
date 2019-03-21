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
import androidx.lifecycle.MutableLiveData
import kotlin.reflect.KProperty

/**
 * Sets the [value].
 */
@MainThread
inline operator fun <T> MutableLiveData<T>.setValue(
    thisRef: Any?,
    property: KProperty<*>,
    value: T
) = setValue(value)

@Deprecated("", ReplaceWith("MutableLiveData(value)", "androidx.lifecycle.MutableLiveData"))
@MainThread
inline fun <T> mutableLiveData(value: T): MutableLiveData<T> = MutableLiveData(value)
