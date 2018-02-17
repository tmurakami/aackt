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

package com.github.tmurakami.aackt.lifecycle

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelStore
import org.junit.Before
import org.junit.Test
import kotlin.test.assertSame

class ViewModelTest {

    private lateinit var viewModels: ViewModelProvider

    @Before
    fun setUp() {
        viewModels = ViewModelProvider(ViewModelStore(), ViewModelProvider.NewInstanceFactory())
    }

    @Test
    fun viewModel() = assertSame(
        viewModel<TestViewModel> { viewModels }.value,
        viewModel<TestViewModel> { viewModels }.value
    )

    @Test
    fun viewModel_key() = assertSame(
        viewModel<TestViewModel>("test") { viewModels }.value,
        viewModel<TestViewModel>("test") { viewModels }.value
    )

    class TestViewModel : ViewModel()
}
