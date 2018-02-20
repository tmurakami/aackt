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

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelStore
import org.junit.After
import org.junit.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class ViewModelProviderTest {

    private val store = ViewModelStore()
    private val provider = ViewModelProvider(store, ViewModelProvider.NewInstanceFactory())

    @After
    fun tearDown() {
        store.clear()
    }

    @Test
    fun getValue() {
        val holder = TestViewModelHolder(provider)
        assertSame(holder.viewModel1, holder.viewModel1)
        assertNotSame(holder.viewModel1, holder.viewModel2)
    }

    class TestViewModel : ViewModel()
    class TestViewModelHolder(viewModelProvider: ViewModelProvider) {
        val viewModel1: TestViewModel by viewModelProvider
        val viewModel2: TestViewModel by viewModelProvider
    }
}
