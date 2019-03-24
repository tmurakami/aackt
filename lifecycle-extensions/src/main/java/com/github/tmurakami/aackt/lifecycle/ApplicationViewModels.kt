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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Represents Fragment-scoped [ViewModel]s.
 *
 * This can be used as follows:
 * ```
 * class MyFragment(viewModels: FragmentViewModels = FragmentApplicationViewModels) :
 *     Fragment() {
 *
 *     private val viewModel: MyFragmentViewModel by viewModels of { this }
 *
 *     ...
 * }
 * ```
 */
typealias FragmentViewModels = ViewModels<Fragment>

/**
 * Represents Activity-scoped [ViewModel]s.
 *
 * This can be used as follows:
 * ```
 * class MyActivity(viewModels: ActivityViewModels = ActivityApplicationViewModels) :
 *     FragmentActivity() {
 *
 *     private val viewModel: MyActivityViewModel by viewModels of { this }
 *
 *     ...
 * }
 * ```
 */
typealias ActivityViewModels = ViewModels<FragmentActivity>

/**
 * A provider of [ViewModel]s that uses [ViewModelProvider.AndroidViewModelFactory] to instantiate
 * new [ViewModel]s.
 *
 * This is equivalent to `Fragment::createViewModelProvider`.
 *
 * @see Fragment.createViewModelProvider
 */
@get:MainThread
val FragmentApplicationViewModels: FragmentViewModels = Fragment::createViewModelProvider

/**
 * A provider of [ViewModel]s that uses [ViewModelProvider.AndroidViewModelFactory] to instantiate
 * new [ViewModel]s.
 *
 * This is equivalent to `FragmentActivity::createViewModelProvider`.
 *
 * @see FragmentActivity.createViewModelProvider
 */
@get:MainThread
val ActivityApplicationViewModels: ActivityViewModels = FragmentActivity::createViewModelProvider
