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
import androidx.annotation.MainThread
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner

/**
 * A provider that provides [SavedStateHandles][SavedStateHandle].
 */
interface SavedStateHandleProvider {
    /**
     * Returns the [SavedStateHandle] identified by the given [key].
     */
    @MainThread
    operator fun get(key: String): SavedStateHandle
}

private const val KEY_PREFIX = "com.github.tmurakami.aackt.lifecycle.SavedStateHandle:"

/**
 * Creates a [SavedStateHandleProvider].
 *
 * @param defaultArgs will be used as default by a [SavedStateHandle] with no saved state.
 */
@Suppress("FunctionName")
fun <O> SavedStateHandleProvider(
    owner: O,
    defaultArgs: Bundle? = null
): SavedStateHandleProvider
    where O : ViewModelStoreOwner,
          O : SavedStateRegistryOwner {
    val factory = object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T = SavedStateHandleHolder(handle) as T
    }
    val provider = ViewModelProvider(owner.viewModelStore, factory)
    return object : SavedStateHandleProvider {
        override fun get(key: String): SavedStateHandle =
            provider.get("$KEY_PREFIX$key", SavedStateHandleHolder::class.java).handle
    }
}

private class SavedStateHandleHolder(@JvmField val handle: SavedStateHandle) : ViewModel()
