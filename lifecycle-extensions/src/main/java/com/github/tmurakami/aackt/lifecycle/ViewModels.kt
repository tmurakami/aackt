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

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * Represents Fragment-scoped [ViewModels].
 *
 * This can be used as follows:
 * ```
 * class MyFragment(viewModels: FragmentViewModels = ViewModelProviders::of) :
 *     Fragment(),
 *     FragmentViewModels by viewModels {
 *
 *     private val viewModel: MyFragmentViewModel by viewModel()
 *
 *     ...
 * }
 * ```
 *
 * @see viewModel
 */
typealias FragmentViewModels = ViewModels<Fragment>

/**
 * Represents Activity-scoped [ViewModels].
 *
 * This can be used as follows:
 * ```
 * class MyActivity(viewModels: ActivityViewModels = ViewModelProviders::of) :
 *     FragmentActivity(),
 *     ActivityViewModels by viewModels {
 *
 *     private val viewModel: MyActivityViewModel by viewModel()
 *
 *     ...
 * }
 * ```
 *
 * @see viewModel
 */
typealias ActivityViewModels = ViewModels<FragmentActivity>
