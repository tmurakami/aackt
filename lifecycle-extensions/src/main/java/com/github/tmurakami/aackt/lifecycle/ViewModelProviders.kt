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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

@Deprecated("", ReplaceWith("ViewModelProviders.of(this)", "androidx.lifecycle.ViewModelProviders"))
@MainThread
inline fun Fragment.createViewModelProvider(): ViewModelProvider =
    ViewModelProviders.of(this)

@Deprecated(
    "",
    ReplaceWith("ViewModelProvider(this, factory)", "androidx.lifecycle.ViewModelProvider")
)
@MainThread
inline fun Fragment.createViewModelProvider(
    factory: ViewModelProvider.Factory? = null
): ViewModelProvider = ViewModelProviders.of(this, factory)

@Deprecated("", ReplaceWith("ViewModelProviders.of(this)", "androidx.lifecycle.ViewModelProviders"))
@MainThread
inline fun FragmentActivity.createViewModelProvider(): ViewModelProvider =
    ViewModelProviders.of(this)

@Deprecated(
    "",
    ReplaceWith("ViewModelProvider(this, factory)", "androidx.lifecycle.ViewModelProvider")
)
@MainThread
inline fun FragmentActivity.createViewModelProvider(
    factory: ViewModelProvider.Factory? = null
): ViewModelProvider = ViewModelProviders.of(this, factory)
