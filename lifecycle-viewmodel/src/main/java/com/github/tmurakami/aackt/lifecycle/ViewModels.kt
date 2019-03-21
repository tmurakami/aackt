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
import androidx.lifecycle.get

/**
 * Represents [ViewModel]s.
 */
interface ViewModels<in O : ViewModelStoreOwner> {
    /**
     * Creates this owner's [ViewModelProvider].
     */
    fun O.createViewModelProvider(): ViewModelProvider
}

/**
 * Creates a [Lazy] which will instantiate this owner's [ViewModel].
 *
 * You can call this by using interface delegation, as shown below:
 *
 * ```
 * class MyFragment(viewModels: ViewModels<Fragment> = ViewModels { ... }) :
 *     Fragment(), ViewModels<Fragment> by viewModels {
 *     private val viewModel: MyViewModel by viewModel()
 * }
 * ```
 */
@MainThread
inline fun <reified T : ViewModel, O> O.viewModel(): Lazy<T>
    where O : ViewModelStoreOwner, O : ViewModels<O> = viewModel { this }

/**
 * Creates a [Lazy] which will instantiate the given [owner]'s [ViewModel].
 */
@MainThread
inline fun <reified T : ViewModel, O : ViewModelStoreOwner> ViewModels<O>.viewModel(
    crossinline owner: () -> O
): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) { owner().createViewModelProvider().get<T>() }

/**
 * Creates a [ViewModels].
 *
 * The given [factory] function will be called for each instantiation of [ViewModel]s.
 */
@Suppress("FunctionName")
inline fun <O : ViewModelStoreOwner> ViewModels(
    crossinline factory: O.() -> ViewModelProvider.Factory
): ViewModels<O> = object : ViewModels<O> {
    override fun O.createViewModelProvider(): ViewModelProvider = createViewModelProvider(factory())
}
