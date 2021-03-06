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

package com.github.tmurakami.aackt.lifecycle.viewmodel.savedstate

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.github.tmurakami.aackt.lifecycle.getValue
import com.github.tmurakami.aackt.lifecycle.liveData
import com.github.tmurakami.aackt.lifecycle.setValue
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertSame

class SavedStateHandleTest {
    @[Rule JvmField]
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun testLiveData() {
        val handle = SavedStateHandle()
        val data by handle.liveData<Int>()
        assertNull(handle["data"])
        data.value = 0
        assertSame(0, handle.get<Int?>("data"))
    }

    @Test
    fun testGetValue() {
        val handle = SavedStateHandle()
        handle["value"] = 0
        val value: Int by handle
        assertSame(0, value)
    }

    @Test(NoSuchElementException::class)
    fun testGetValue_noSuchElement() {
        val handle = SavedStateHandle()
        val value: Int by handle
        value.toString()
    }

    @Test
    fun testSetValue() {
        val handle = SavedStateHandle()
        handle["value"] = 0
        var value: Int by handle
        assertSame(0, value)
        value = 1
        assertSame(1, value)
        assertSame<Int?>(1, handle["value"])
    }
}
