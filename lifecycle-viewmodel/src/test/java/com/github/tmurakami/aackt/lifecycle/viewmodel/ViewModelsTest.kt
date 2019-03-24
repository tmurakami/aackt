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
import com.github.tmurakami.aackt.lifecycle.createViewModelProvider
import com.github.tmurakami.aackt.lifecycle.of
import kotlin.test.Test
import kotlin.test.assertSame

class ViewModelsTest {
    @Test
    fun of() {
        val viewModels: ViewModels<ViewModelStoreOwner> = {
            it.createViewModelProvider(object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(c: Class<T>) = c.cast(TestViewModel(it))!!
            })
        }
        val one = FakeViewModelStoreOwner()
        val another = FakeViewModelStoreOwner()
        val oneViewModel: TestViewModel by viewModels of { one }
        assertSame(one, oneViewModel.owner)
        val anotherViewModel: TestViewModel by viewModels of { another }
        assertSame(another, anotherViewModel.owner)
    }

    class TestViewModel(val owner: ViewModelStoreOwner) : ViewModel()
}
