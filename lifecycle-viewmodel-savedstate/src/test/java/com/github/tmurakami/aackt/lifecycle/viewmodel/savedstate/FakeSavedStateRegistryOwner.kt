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

package com.github.tmurakami.aackt.lifecycle.viewmodel.savedstate

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner

class FakeSavedStateRegistryOwner(savedState: Bundle? = null) :
    ViewModelStoreOwner,
    SavedStateRegistryOwner {
    private val store = ViewModelStore()
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val registryController = SavedStateRegistryController.create(this)

    init {
        registryController.performRestore(savedState)
    }

    override fun getViewModelStore(): ViewModelStore = store
    override fun getLifecycle(): Lifecycle = lifecycleRegistry
    override fun getSavedStateRegistry(): SavedStateRegistry = registryController.savedStateRegistry
    fun performSave(outBundle: Bundle) = registryController.performSave(outBundle)
}
