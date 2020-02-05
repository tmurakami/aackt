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

package com.github.tmurakami.aackt.lifecycle.livedata

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.github.tmurakami.aackt.lifecycle.LiveDataOnLifecycle
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class LiveDataOnLifecycleTest {
    @[Rule JvmField]
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun testOnActive() {
        val data = LiveDataOnLifecycle(MutableLiveData<Unit>())
        var count = 0
        repeat(3) { data.onActiveActions += Runnable { count++ } }
        assertEquals(0, count)
        data.observeForever { }
        assertEquals(3, count)
    }

    @Test
    fun testOnChanged() {
        val expected = listOf(0)
        val actual = mutableListOf<Int>()
        val data = MutableLiveData<Int>()
        LiveDataOnLifecycle(data).observeForever { actual += it }
        data.value = 0
        assertEquals(expected, actual)
    }

    @Test
    fun testOnInactive() {
        val data = LiveDataOnLifecycle(MutableLiveData<Unit>())
        var count = 0
        repeat(3) { data.onInactiveActions += Runnable { count++ } }
        val observer = Observer<Unit> { }.also { data.observeForever(it) }
        assertEquals(0, count)
        data.removeObserver(observer)
        assertEquals(3, count)
    }
}
