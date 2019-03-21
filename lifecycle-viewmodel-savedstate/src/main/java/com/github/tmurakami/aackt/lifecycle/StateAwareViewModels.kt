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

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.SavedStateVMFactory
import androidx.lifecycle.ViewModel

/**
 * Represents Fragment-scoped [ViewModels].
 */
typealias FragmentViewModels = ViewModels<Fragment>

/**
 * Represents Activity-scoped [ViewModels].
 */
typealias ActivityViewModels = ViewModels<FragmentActivity>

/**
 * A [ViewModels] that uses [SavedStateVMFactory] to instantiate new [ViewModel]s.
 *
 * This is equivalent to `ViewModels { SavedStateVMFactory(this, arguments) }`.
 */
val FragmentStateAwareViewModels: FragmentViewModels =
    ViewModels { SavedStateVMFactory(this, arguments) }

/**
 * A [ViewModels] that uses [SavedStateVMFactory] to instantiate new [ViewModel]s.
 *
 * This is equivalent to `ViewModels { SavedStateVMFactory(this, intent.extras) }`.
 */
val ActivityStateAwareViewModels: ActivityViewModels =
    ViewModels { SavedStateVMFactory(this, intent.extras) }