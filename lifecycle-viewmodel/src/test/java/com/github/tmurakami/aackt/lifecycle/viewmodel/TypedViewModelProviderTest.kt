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
import androidx.lifecycle.keys
import com.github.tmurakami.aackt.lifecycle.TypedViewModelProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class TypedViewModelProviderTest {
    @Test
    fun testTypedViewModelProvider() {
        TypedViewModelProvider<FooViewModel>(ViewModelProvider(FakeViewModelStoreOwner()))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testTypedViewModelProvider_localClass() {
        class LocalViewModel : ViewModel()
        TypedViewModelProvider<LocalViewModel>(ViewModelProvider(FakeViewModelStoreOwner()))
    }

    @Test
    fun testGet() {
        val owner = FakeViewModelStoreOwner()
        val provider = TypedViewModelProvider<FooViewModel>(ViewModelProvider(owner))
        assertSame(provider.get(), provider.get())
    }

    @Test
    fun testGet_name() {
        val provider = ViewModelProvider(FakeViewModelStoreOwner())
        val fooProvider = TypedViewModelProvider<FooViewModel>(provider)
        val fooViewModel = fooProvider["a"]
        assertSame(fooViewModel, fooProvider["a"])
        assertNotSame(fooViewModel, fooProvider.get())
        assertNotSame(fooViewModel, fooProvider["b"])
        val barProvider = TypedViewModelProvider<BarViewModel>(provider)
        assertNotSame<ViewModel>(fooViewModel, barProvider["a"])
        assertSame(fooViewModel, fooProvider["a"])
    }

    @Test
    fun testKeyFormat() {
        val owner = FakeViewModelStoreOwner()
        val provider = TypedViewModelProvider<FooViewModel>(ViewModelProvider(owner))
        provider.get()
        provider["a"]
        provider["b"]
        val className = FooViewModel::class.java.canonicalName
        assertEquals(setOf(className, "$className:a", "$className:b"), owner.viewModelStore.keys())
    }

    class FooViewModel : ViewModel()
    class BarViewModel : ViewModel()
}
