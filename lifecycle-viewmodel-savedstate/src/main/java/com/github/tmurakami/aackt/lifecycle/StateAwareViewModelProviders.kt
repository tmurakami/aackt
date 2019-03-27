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

@file:Suppress("FunctionName")

package com.github.tmurakami.aackt.lifecycle

import android.app.Application
import android.os.Bundle
import androidx.annotation.MainThread
import androidx.lifecycle.AbstractSavedStateVMFactory
import androidx.lifecycle.SavedStateVMFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner

/**
 * Represents the factory responsible for instantiating [ViewModelProvider].
 */
interface StateAwareViewModelProviders {
    /**
     * Creates a [ViewModelProvider] of the given [owner].
     *
     * @param defaultArgs will be used to instantiate [AbstractSavedStateVMFactory].
     */
    @MainThread
    fun <O> of(owner: O, defaultArgs: Bundle? = null): ViewModelProvider
        where O : ViewModelStoreOwner, O : SavedStateRegistryOwner
}

/**
 * Represents the factory responsible for instantiating [AbstractSavedStateVMFactory].
 */
typealias SavedStateVMFactoryMaker =
    SavedStateRegistryOwner.(defaultArgs: Bundle?) -> AbstractSavedStateVMFactory

/**
 * Creates a [StateAwareViewModelProviders] that calls the given [factory] function to instantiate
 * [AbstractSavedStateVMFactory].
 */
inline fun StateAwareViewModelProviders(
    crossinline factory: SavedStateVMFactoryMaker
): StateAwareViewModelProviders = object : StateAwareViewModelProviders {
    override fun <O> of(owner: O, defaultArgs: Bundle?): ViewModelProvider
        where O : ViewModelStoreOwner, O : SavedStateRegistryOwner =
        ViewModelProvider(owner, owner.factory(defaultArgs))
}

/**
 * Creates a [StateAwareViewModelProviders] that uses [SavedStateVMFactory] to instantiate
 * [ViewModel].
 */
fun StateAwareAndroidViewModelProviders(application: Application): StateAwareViewModelProviders =
    StateAwareViewModelProviders { SavedStateVMFactory(application, this, it) }
