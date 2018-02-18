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
import com.github.tmurakami.aackt.lifecycle.ViewModels
import org.junit.Before
import org.junit.Test
import kotlin.test.assertSame

class ViewModelsTest {

    private lateinit var provider: ViewModelProvider

    @Before
    fun setUp() {
        provider = ViewModelProvider(ViewModelStore(), ViewModelProvider.NewInstanceFactory())
    }

    @Test
    fun viewModel() = assertSame(
        TestViewModelHolder(provider).testViewModel,
        TestViewModelHolder(provider).testViewModel
    )

    class TestViewModel : ViewModel()
    class TestViewModelHolder(provider: ViewModelProvider) {
        val testViewModel: TestViewModel by ViewModels { provider }
    }
}
