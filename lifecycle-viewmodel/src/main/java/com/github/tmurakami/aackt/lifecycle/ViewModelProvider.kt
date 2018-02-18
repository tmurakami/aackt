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

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.support.annotation.MainThread

/**
 * Returns an existing [ViewModel] or creates a new one.
 */
@Deprecated(message = "", replaceWith = ReplaceWith("get(T::class.java)"))
@MainThread
inline fun <reified T : ViewModel> ViewModelProvider.get(): T = get(T::class.java)

/**
 * Returns an existing [ViewModel] with the given [key], or creates a new one.
 */
@Deprecated(message = "", replaceWith = ReplaceWith("get(key, T::class.java)"))
@MainThread
inline operator fun <reified T : ViewModel> ViewModelProvider.get(key: String): T =
    get(key, T::class.java)
