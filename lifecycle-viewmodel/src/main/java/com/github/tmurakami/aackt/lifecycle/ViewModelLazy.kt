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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Creates a [Lazy] that calls [provide] to instantiate [ViewModel].
 */
@MainThread
inline fun <T : ViewModel> viewModel(noinline provide: () -> T): Lazy<T> =
    lazy(LazyThreadSafetyMode.NONE, provide)

/**
 * Creates a [Lazy] from the given [provider].
 *
 * The resulting [Lazy] calls [ViewModelProvider.get] without a key in order to instantiate
 * [ViewModel]. If you want to instantiate [ViewModel] with some key, use [viewModel] instead.
 */
@MainThread
inline fun <reified T : ViewModel> viewModels(
    crossinline provider: () -> ViewModelProvider
): Lazy<T> = viewModel { provider().get(T::class.java) }
