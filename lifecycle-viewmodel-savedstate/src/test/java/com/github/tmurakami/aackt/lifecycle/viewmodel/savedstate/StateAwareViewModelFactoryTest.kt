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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.github.tmurakami.aackt.lifecycle.StateAwareViewModelFactory
import kotlin.test.Test
import kotlin.test.assertSame

class StateAwareViewModelFactoryTest {
    @Test
    fun getViewModel() {
        class TestViewModel(val handle: SavedStateHandle) : ViewModel() {
            init {
                handle.set("test", 0)
            }
        }

        val creator = object : StateAwareViewModelFactory.Creator {
            override fun <T : ViewModel> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T = modelClass.cast(TestViewModel(handle))!!
        }
        val factory = StateAwareViewModelFactory(FakeSavedStateRegistryOwner(), creator)
        val provider = ViewModelProvider(ViewModelStore(), factory)
        val viewModel = provider.get("testViewModel", TestViewModel::class.java)
        assertSame(0, viewModel.handle.get<Int?>("test"))
    }
}
