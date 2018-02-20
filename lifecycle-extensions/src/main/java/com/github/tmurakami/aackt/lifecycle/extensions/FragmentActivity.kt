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

package com.github.tmurakami.aackt.lifecycle.extensions

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.annotation.MainThread
import android.support.v4.app.FragmentActivity

/**
 * Creates a [ViewModelProvider] that uses the default factory to instantiate new view models.
 */
@MainThread
inline fun FragmentActivity.viewModelProvider(): ViewModelProvider = ViewModelProviders.of(this)

/**
 * Creates a [ViewModelProvider] that uses the given [factory] to instantiate new view models.
 */
@MainThread
inline fun FragmentActivity.viewModelProvider(
    factory: ViewModelProvider.Factory
): ViewModelProvider = ViewModelProviders.of(this, factory)
