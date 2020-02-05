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

package com.github.tmurakami.aackt.lifecycle.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.tmurakami.aackt.lifecycle.TypedViewModelProvider
import com.github.tmurakami.aackt.lifecycle.viewModelLazy
import com.github.tmurakami.aackt.lifecycle.viewModelProvider
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ViewModelStoreOwnerTest {
    @Test
    fun testViewModelLazy() {
        val lazy = FakeViewModelStoreOwner().viewModelLazy {
            object : TypedViewModelProvider<TestViewModel> {
                override fun get(name: String?): TestViewModel = TestViewModel()
            }
        }
        assertFalse(lazy.isInitialized())
        val viewModel = lazy.value
        assertTrue(lazy.isInitialized())
        assertSame(viewModel, lazy.value)
    }

    @Test
    fun testViewModelProvider() {
        val provider = FakeViewModelStoreOwner().viewModelProvider<TestViewModel>()
        assertSame(provider.get(), provider.get())
    }

    @Test
    fun testViewModelProvider_factory() {
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T = TestViewModel() as T
        }
        val provider = FakeViewModelStoreOwner().viewModelProvider<TestViewModel>(factory)
        assertSame(provider.get(), provider.get())
    }

    @Test
    fun testViewModelProvider_factory_function() {
        val provider = FakeViewModelStoreOwner().viewModelProvider { TestViewModel() }
        assertSame(provider.get(), provider.get())
    }

    class TestViewModel : ViewModel()
}
