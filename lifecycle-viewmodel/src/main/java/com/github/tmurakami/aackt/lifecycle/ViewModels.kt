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
import androidx.lifecycle.ViewModelStoreOwner
import kotlin.reflect.KClass

/**
 * A factory that defers instantiation of [ViewModel]s.
 */
@MainThread
interface ViewModels<in O : ViewModelStoreOwner> {
    /**
     * Creates a [Lazy] which will instantiate a [ViewModel] of the owner returned by the given
     * [ownerProvider].
     */
    fun <T : ViewModel> viewModelLazy(modelClass: KClass<T>, ownerProvider: () -> O): Lazy<T>
}

/**
 * Creates a [Lazy] which will instantiate a [ViewModel] of this owner.
 *
 * You can call this by using interface delegation, as shown below:
 *
 * ```
 * class MyFragment(viewModels: ViewModels<Fragment> = ...) :
 *     Fragment(), ViewModels<Fragment> by viewModels {
 *     private val viewModel: MyViewModel by viewModelLazy()
 * }
 * ```
 */
@MainThread
inline fun <reified T : ViewModel, O> O.viewModelLazy(): Lazy<T>
    where O : ViewModelStoreOwner, O : ViewModels<O> = viewModelLazy { this }

/**
 * Creates a [Lazy] which will instantiate a [ViewModel] of the owner returned by the given
 * [ownerProvider].
 */
@MainThread
inline fun <reified T : ViewModel, O : ViewModelStoreOwner> ViewModels<O>.viewModelLazy(
    noinline ownerProvider: () -> O
): Lazy<T> = viewModelLazy(T::class, ownerProvider)

/**
 * Creates a [ViewModels].
 *
 * The given [factoryMaker] will be called for each instantiation of [ViewModel]s.
 */
@Suppress("FunctionName")
fun <O : ViewModelStoreOwner> ViewModels(
    factoryMaker: O.() -> ViewModelProvider.Factory
): ViewModels<O> = object : ViewModels<O> {
    override fun <T : ViewModel> viewModelLazy(
        modelClass: KClass<T>,
        ownerProvider: () -> O
    ): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
        ownerProvider().let { ViewModelProvider(it, factoryMaker(it)) }.get(modelClass.java)
    }
}
