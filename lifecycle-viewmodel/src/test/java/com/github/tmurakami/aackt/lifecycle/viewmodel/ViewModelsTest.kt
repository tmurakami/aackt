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
import androidx.lifecycle.ViewModelStoreOwner
import com.github.tmurakami.aackt.lifecycle.ViewModels
import com.github.tmurakami.aackt.lifecycle.viewModel
import kotlin.test.Test
import kotlin.test.assertSame

class ViewModelsTest {
    @Test
    fun viewModel() {
        var createCount = 0
        val viewModels = ViewModels<ViewModelStoreOwner> {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    modelClass.cast(TestViewModel(this@ViewModels))!!.also { createCount++ }
            }
        }
        val owner1 = object :
            ViewModels<ViewModelStoreOwner> by viewModels,
            ViewModelStoreOwner by FakeViewModelStoreOwner() {}
        val owner2 = FakeViewModelStoreOwner()
        val owner1ViewModel: TestViewModel by owner1.viewModel()
        val owner2ViewModel: TestViewModel by owner1.viewModel { owner2 }
        assertSame(0, createCount)
        assertSame(owner1, owner1ViewModel.owner)
        assertSame(1, createCount)
        assertSame(owner2, owner2ViewModel.owner)
        assertSame(2, createCount)
    }

    class TestViewModel(val owner: ViewModelStoreOwner) : ViewModel()
}
