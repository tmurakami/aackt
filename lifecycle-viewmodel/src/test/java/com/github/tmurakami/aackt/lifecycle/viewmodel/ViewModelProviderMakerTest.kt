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
import com.github.tmurakami.aackt.lifecycle.ViewModelProviderMaker
import com.github.tmurakami.aackt.lifecycle.createViewModelProvider
import com.github.tmurakami.aackt.lifecycle.viewModel
import kotlin.test.Test
import kotlin.test.assertSame

class ViewModelProviderMakerTest {
    @Test
    fun viewModel() {
        val providerMaker: ViewModelProviderMaker<ViewModelStoreOwner> = {
            it.createViewModelProvider(object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(c: Class<T>) = c.cast(TestViewModel(it))!!
            })
        }
        val owner1 = object :
            ViewModelProviderMaker<ViewModelStoreOwner> by providerMaker,
            ViewModelStoreOwner by FakeViewModelStoreOwner() {}
        val owner2 = FakeViewModelStoreOwner()
        val owner1ViewModel: TestViewModel by owner1.viewModel()
        assertSame(owner1, owner1ViewModel.owner)
        val owner2ViewModel: TestViewModel by owner1.viewModel { owner2 }
        assertSame(owner2, owner2ViewModel.owner)
    }

    class TestViewModel(val owner: ViewModelStoreOwner) : ViewModel()
}
