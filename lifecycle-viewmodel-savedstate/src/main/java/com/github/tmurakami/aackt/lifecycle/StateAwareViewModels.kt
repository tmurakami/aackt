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
import androidx.lifecycle.SavedStateVMFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner

/**
 * A provider of [ViewModel]s that uses [SavedStateVMFactory] to instantiate new [ViewModel]s.
 *
 * This is equivalent to `{ f -> f.createViewModelProvider(SavedStateVMFactory(f, f.arguments)) }`.
 *
 * @see ViewModelStoreOwner.createViewModelProvider
 * @see SavedStateVMFactory
 */
@get:MainThread
val FragmentStateAwareViewModels: ViewModels<Fragment> =
    { it.createViewModelProvider(SavedStateVMFactory(it, it.arguments)) }

/**
 * A provider of [ViewModel]s that uses [SavedStateVMFactory] to instantiate new [ViewModel]s.
 *
 * This is equivalent to
 * `{ a -> a.createViewModelProvider(SavedStateVMFactory(a, a.intent.extras)) }`.
 *
 * @see ViewModelStoreOwner.createViewModelProvider
 * @see SavedStateVMFactory
 */
@get:MainThread
val ActivityStateAwareViewModels: ViewModels<FragmentActivity> =
    { it.createViewModelProvider(SavedStateVMFactory(it, it.intent.extras)) }
