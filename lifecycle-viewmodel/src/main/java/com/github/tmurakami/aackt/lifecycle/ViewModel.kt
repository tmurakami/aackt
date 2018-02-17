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
 * Creates a [Lazy] of the [ViewModel] returned from the given [provider].
 */
@MainThread
inline fun <reified T : ViewModel> viewModel(
    crossinline provider: () -> ViewModelProvider
): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) { provider().get<T>() }

/**
 * Creates a [Lazy] of the [ViewModel] returned from the given [provider] with the [key].
 */
@MainThread
inline fun <reified T : ViewModel> viewModel(
    key: String,
    crossinline provider: () -> ViewModelProvider
): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) { provider().get<T>(key) }
