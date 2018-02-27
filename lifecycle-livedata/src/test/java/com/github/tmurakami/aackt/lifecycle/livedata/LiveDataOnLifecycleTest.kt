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

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import com.github.tmurakami.aackt.lifecycle.LiveDataOnLifecycle
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class LiveDataOnLifecycleTest {

    @Suppress("unused")
    @get:Rule
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun onActive() {
        val data = LiveDataOnLifecycle<Unit>(MutableLiveData())
        var count = 0
        for (i in 0..2) {
            data.onActiveListeners += { count++ }
        }
        assertEquals(0, count)
        data.observeForever { }
        assertEquals(3, count)
    }

    @Test
    fun onChanged() {
        val src = MutableLiveData<Int>()
        val data = LiveDataOnLifecycle<Int>(src)
        var received: Int? = null
        data.observeForever { received = it }
        src.value = 0
        assertEquals(0, received)
    }

    @Test
    fun onInactive() {
        val data = LiveDataOnLifecycle<Unit>(MutableLiveData())
        var count = 0
        for (i in 0..2) {
            data.onInactiveListeners += { count++ }
        }
        val observer = Observer<Unit> {}
        data.observeForever(observer)
        assertEquals(0, count)
        data.removeObserver(observer)
        assertEquals(3, count)
    }
}
