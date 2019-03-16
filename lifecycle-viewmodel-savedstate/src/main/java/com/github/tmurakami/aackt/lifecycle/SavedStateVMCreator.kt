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
import androidx.lifecycle.AbstractSavedStateVMFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner

/**
 * An interface that instantiates [ViewModel]s.
 */
interface SavedStateVMCreator {
    /**
     * Creates a new [ViewModel] of the given [modelClass]. You can write and read values to and
     * from the saved state using the given [handle].
     */
    fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T
}

/**
 * Creates a new [ViewModelProvider.Factory] with the given [owner] and [defaultArgs].
 */
fun SavedStateVMCreator.toFactory(
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
): ViewModelProvider.Factory = object : AbstractSavedStateVMFactory(owner, defaultArgs) {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T = this@toFactory.create(key, modelClass, handle)
}
