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
import com.github.tmurakami.aackt.lifecycle.LiveDataOnLifecycle
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class LiveDataOnLifecycleTest {

    @[Rule JvmField]
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun onActive() {
        val data = LiveDataOnLifecycle(MutableLiveData<Unit>())
        var count = 0
        (0..2).forEach { data.onActiveListeners += { count++ } }
        assertEquals(0, count)
        data.test()
        assertEquals(3, count)
    }

    @Test
    fun onChanged() {
        val src = MutableLiveData<Int>()
        val data = LiveDataOnLifecycle(src)
        val observer = data.test()
        src.value = 0
        observer.assertValues(0)
    }

    @Test
    fun onInactive() {
        val data = LiveDataOnLifecycle(MutableLiveData<Unit>())
        var count = 0
        (0..2).forEach { data.onInactiveListeners += { count++ } }
        val observer = data.test()
        assertEquals(0, count)
        data.removeObserver(observer)
        assertEquals(3, count)
    }
}
