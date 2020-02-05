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

/**
 * A provider that provides [ViewModels][ViewModel] of [T].
 */
interface TypedViewModelProvider<T : ViewModel> {
    /**
     * Returns the [ViewModel] associated with the given [name].
     *
     * If the [name] isn't specified (or null), the [ViewModel] with the default name will be
     * returned.
     */
    @MainThread
    operator fun get(name: String? = null): T
}

/**
 * Creates a [TypedViewModelProvider] from the given [provider].
 */
@Suppress("FunctionName")
inline fun <reified T : ViewModel> TypedViewModelProvider(
    provider: ViewModelProvider
): TypedViewModelProvider<T> = TypedViewModelProvider(provider, T::class.java)

/**
 * Creates a [TypedViewModelProvider] from the given [provider].
 */
@Suppress("FunctionName")
fun <T : ViewModel> TypedViewModelProvider(
    provider: ViewModelProvider,
    modelClass: Class<T>
): TypedViewModelProvider<T> {
    val className = requireNotNull(modelClass.canonicalName) {
        "Local and anonymous classes cannot be ViewModels: ${modelClass.name}"
    }
    return object : TypedViewModelProvider<T> {
        override fun get(name: String?): T =
            provider.get(if (name == null) className else "$className:$name", modelClass)
    }
}
