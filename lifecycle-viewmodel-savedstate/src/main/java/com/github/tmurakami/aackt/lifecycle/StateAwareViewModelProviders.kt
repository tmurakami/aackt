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
import androidx.lifecycle.AbstractSavedStateVMFactory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner

/**
 * Represents a factory responsible for instantiating a new [ViewModelProvider].
 */
interface StateAwareViewModelProviders {
    /**
     * Creates a [ViewModelProvider] of the given [owner].
     */
    @MainThread
    fun <O> of(owner: O): ViewModelProvider
        where O : ViewModelStoreOwner, O : SavedStateRegistryOwner
}

/**
 * A template of [StateAwareViewModelProviders].
 */
abstract class AbstractStateAwareViewModelProviders protected constructor() :
    StateAwareViewModelProviders {
    /**
     * Creates a [ViewModelProvider] of the given [owner].
     */
    final override fun <O> of(owner: O): ViewModelProvider
        where O : ViewModelStoreOwner, O : SavedStateRegistryOwner =
        ViewModelProvider(owner, owner.createFactory())

    /**
     * Creates an instance of an [AbstractSavedStateVMFactory] subclass.
     */
    protected abstract fun SavedStateRegistryOwner.createFactory(): AbstractSavedStateVMFactory
}
