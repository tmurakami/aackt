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

import android.os.Bundle
import androidx.lifecycle.SavedStateVMFactory
import androidx.lifecycle.ViewModelProvider

/**
 * A factory responsible for instantiation of [ViewModelProvider.Factory].
 *
 * This is useful for selecting one of the [SavedStateVMFactory] constructors, as shown below:
 *
 * ```
 * val createViewModelFactory: ViewModelFactoryWithBundleMaker<Fragment> = ::SavedStateVMFactory
 * val viewModelFactory = createViewModelFactory(arguments)
 * ```
 *
 * For example, if you want to replace the [SavedStateVMFactory] instance with a mock
 * [ViewModelProvider.Factory] for testing, you could replace the [ViewModelFactoryWithBundleMaker]
 * by doing the following:
 *
 * ```
 * class MyFragment @VisibleForTesting constructor(
 *     private val createViewModelFactory: ViewModelFactoryWithBundleMaker<Fragment> = ::SavedStateVMFactory
 * ) : Fragment() { ... }
 *
 * @Test
 * fun test() {
 *     // Create a mock object of `ViewModelProvider.Factory`
 *     val mockViewModelFactory: ViewModelProvider.Factory = ...
 *
 *     // Launch the fragment with the mock object above
 *     val scenario = launchFragmentInContainer { MyFragment { mockViewModelFactory } }
 *
 *     ...
 * }
 * ```
 */
typealias ViewModelFactoryWithBundleMaker<T> = ViewModelFactoryWithArgsMaker<T, Bundle?>
