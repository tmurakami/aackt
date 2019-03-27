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
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.github.tmurakami.aackt.lifecycle.viewModel
import com.github.tmurakami.aackt.lifecycle.viewModels
import kotlin.test.Test
import kotlin.test.assertSame

class ViewModelLazyTest {
    @Test
    fun testViewModel() {
        val viewModel: TestViewModel by viewModel { TestViewModel() }
        assertSame(viewModel, viewModel)
    }

    @Test
    fun testViewModels() {
        val owner = FakeViewModelStoreOwner()
        var providers = 0
        val viewModel by viewModels<TestViewModel> {
            ViewModelProvider(owner, NewInstanceFactory()).also { providers++ }
        }
        assertSame(0, providers)
        assertSame(viewModel, viewModel)
        assertSame(1, providers)
    }

    class TestViewModel : ViewModel()
}
