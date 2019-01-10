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

@file:[Suppress("NOTHING_TO_INLINE")]

package com.github.tmurakami.aackt.lifecycle

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

/**
 * Creates a [ViewModelProvider] that uses [ViewModelProvider.AndroidViewModelFactory] to
 * instantiate new ViewModels.
 */
@MainThread
inline fun Fragment.createViewModelProvider(): ViewModelProvider = ViewModelProviders.of(this)

/**
 * Creates a [ViewModelProvider] that uses the given [factory] to instantiate new ViewModels.
 *
 * If the [factory] is null then [ViewModelProvider.AndroidViewModelFactory] will be used.
 */
@MainThread
inline fun Fragment.createViewModelProvider(
    factory: ViewModelProvider.Factory? = null
): ViewModelProvider = ViewModelProviders.of(this, factory)

/**
 * Creates a [ViewModelProvider] that uses [ViewModelProvider.AndroidViewModelFactory] to
 * instantiate new ViewModels.
 */
@MainThread
inline fun FragmentActivity.createViewModelProvider(): ViewModelProvider =
    ViewModelProviders.of(this)

/**
 * Creates a [ViewModelProvider] that uses the given [factory] to instantiate new ViewModels.
 *
 * If the [factory] is null then [ViewModelProvider.AndroidViewModelFactory] will be used.
 */
@MainThread
inline fun FragmentActivity.createViewModelProvider(
    factory: ViewModelProvider.Factory? = null
): ViewModelProvider = ViewModelProviders.of(this, factory)
