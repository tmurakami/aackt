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

import android.app.Application
import android.os.Bundle
import androidx.annotation.MainThread
import androidx.lifecycle.AbstractSavedStateVMFactory
import androidx.lifecycle.SavedStateVMFactory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner

/**
 * Represents the factory responsible for instantiating [AbstractSavedStateVMFactory].
 */
typealias SavedStateVMFactoryMaker =
    SavedStateRegistryOwner.(defaultArgs: Bundle?) -> AbstractSavedStateVMFactory

/**
 * Creates a [ViewModelProvider] with the given [factory].
 *
 * This is equivalent to `createSavedStateVMProvider(null, factory)`
 */
@MainThread
inline fun <O> O.createSavedStateVMProvider(factory: SavedStateVMFactoryMaker): ViewModelProvider
    where O : ViewModelStoreOwner, O : SavedStateRegistryOwner =
    createSavedStateVMProvider(null, factory)

/**
 * Creates a [ViewModelProvider] with the given [factory].
 *
 * @param defaultArgs will be passed to [factory].
 */
@MainThread
inline fun <O> O.createSavedStateVMProvider(
    defaultArgs: Bundle? = null,
    factory: SavedStateVMFactoryMaker
): ViewModelProvider where O : ViewModelStoreOwner, O : SavedStateRegistryOwner =
    ViewModelProvider(this, factory(defaultArgs))

/**
 * Creates a [SavedStateVMFactoryMaker] that instantiates [SavedStateVMFactory].
 */
@Suppress("FunctionName")
fun AndroidSavedStateVMFactoryMaker(application: Application): SavedStateVMFactoryMaker =
    { SavedStateVMFactory(application, this, it) }
