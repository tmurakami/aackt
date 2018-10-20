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

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlin.reflect.KProperty

/**
 * Returns an existing [ViewModel] with the given [key], or creates a new one.
 */
@MainThread
inline operator fun <reified T : ViewModel> ViewModelProvider.get(key: String): T =
    get(key, T::class.java)

/**
 * Returns an existing [ViewModel] with the given [property] name, or creates a new one.
 */
@MainThread
inline operator fun <reified T : ViewModel> ViewModelProvider.getValue(
    thisRef: Any?,
    property: KProperty<*>
): T = get(property.name)
