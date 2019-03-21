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
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SavedStateHandleTest {
    @[Rule JvmField]
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun liveData() {
        val handle = SavedStateHandle()
        val d by handle.liveData<String>()
        assertNull(d.value)
        handle["d"] = "test"
        assertEquals("test", d.value)
    }

    @Test
    fun getValue() {
        val handle = SavedStateHandle()
        handle["s"] = "test"
        val s: String by handle
        assertEquals("test", s)
    }

    @Test
    fun setValue() {
        val handle = SavedStateHandle()
        var s: String? by handle
        assertNull(s)
        s = "test"
        assertEquals("test", s)
        assertEquals<String?>("test", handle["s"])
    }
}
