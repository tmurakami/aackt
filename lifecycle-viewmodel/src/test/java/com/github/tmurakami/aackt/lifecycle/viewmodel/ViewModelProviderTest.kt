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
import androidx.lifecycle.ViewModelStore
import com.github.tmurakami.aackt.lifecycle.getValue
import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class ViewModelProviderTest {
    @Test
    fun getValue() {
        class TestViewModel : ViewModel()

        val provider = ViewModelProvider(ViewModelStore(), ViewModelProvider.NewInstanceFactory())
        val vm1: TestViewModel by provider
        assertSame(vm1, vm1)
        val vm2: TestViewModel by provider
        assertNotSame(vm1, vm2)
    }
}
