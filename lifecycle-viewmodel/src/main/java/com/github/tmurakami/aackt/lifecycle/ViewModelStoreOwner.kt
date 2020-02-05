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
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

/**
 * Creates a [Lazy] that caches the [ViewModel] fetched from the given [provider].
 *
 * If the [name] isn't specified (or null), the [ViewModel] with the default name will be cached.
 */
@MainThread
inline fun <reified T : ViewModel> ViewModelStoreOwner.viewModelLazy(
    name: String? = null,
    crossinline provider: () -> TypedViewModelProvider<T> = { viewModelProvider() }
): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) { provider()[name] }

/**
 * Creates a [TypedViewModelProvider] with the default factory.
 *
 * The default factory is the result of
 * [HasDefaultViewModelProviderFactory.getDefaultViewModelProviderFactory] if the receiver
 * implements [HasDefaultViewModelProviderFactory]. Otherwise, it is a
 * [ViewModelProvider.NewInstanceFactory].
 */
inline fun <reified T : ViewModel> ViewModelStoreOwner.viewModelProvider():
    TypedViewModelProvider<T> = TypedViewModelProvider(ViewModelProvider(this))

/**
 * Creates a [TypedViewModelProvider] with the given [factory].
 */
inline fun <reified T : ViewModel> ViewModelStoreOwner.viewModelProvider(
    factory: ViewModelProvider.Factory
): TypedViewModelProvider<T> = TypedViewModelProvider(ViewModelProvider(viewModelStore, factory))

/**
 * Creates a [TypedViewModelProvider] with the given [factory].
 */
inline fun <reified T : ViewModel> ViewModelStoreOwner.viewModelProvider(
    crossinline factory: () -> T
): TypedViewModelProvider<T> = viewModelProvider(object : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = factory() as T
})
