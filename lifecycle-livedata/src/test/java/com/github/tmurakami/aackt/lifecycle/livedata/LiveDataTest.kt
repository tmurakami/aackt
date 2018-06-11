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
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import com.github.tmurakami.aackt.lifecycle.liveData
import com.github.tmurakami.aackt.lifecycle.observe
import com.github.tmurakami.aackt.lifecycle.observeChanges
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue

class LiveDataTest {

    @[Rule JvmField]
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun liveData() = assertSame(0, liveData(0).value)

    @Test
    fun observe() {
        val src = MutableLiveData<Int>()
        val results = ArrayList<Int>()
        val observation = src.observe { results += it }
        src.value = 0
        observation.dispose()
        src.value = 1
        assertSame(0, results.single())
    }

    @Test
    fun observeChanges() {
        val src = MutableLiveData<Int>().apply { value = -1 }
        val results = ArrayList<Int>()
        val observation = src.observeChanges { results += it }
        assertTrue(results.isEmpty())
        src.value = 0
        observation.dispose()
        src.value = 1
        assertSame(0, results.single())
    }

    @Test
    fun observeChanges_observe_MediatorLiveData() {
        val src = MediatorLiveData<Int>().apply { addSource(liveData(-1)) { value = it } }
        src.value = 0
        val results = ArrayList<Int>()
        src.observeChanges { results += it }
        assertSame(-1, results.single())
    }

    @Test
    fun observe_LifecycleOwner() {
        val owner = TestLifecycleOwner()
        val src = MutableLiveData<Int>()
        val results = ArrayList<Int>()
        src.observe(owner) { results += it }
        owner.lifecycle.markState(Lifecycle.State.RESUMED)
        src.value = 0
        owner.lifecycle.markState(Lifecycle.State.DESTROYED)
        src.value = 1
        assertSame(0, results.single())
    }

    @Test
    fun observeChanges_LifecycleOwner() {
        val owner = TestLifecycleOwner()
        val src = MutableLiveData<Int>().apply { value = -1 }
        val results = ArrayList<Int>()
        src.observeChanges(owner) { results += it }
        owner.lifecycle.markState(Lifecycle.State.RESUMED)
        src.value = 0
        owner.lifecycle.markState(Lifecycle.State.DESTROYED)
        src.value = 1
        assertSame(0, results.single())
    }

    @Test
    fun observeChanges_LifecycleOwner_observe_MediatorLiveData() {
        val owner = TestLifecycleOwner()
        val src = MediatorLiveData<Int>().apply { addSource(liveData(-1)) { value = it } }
        src.value = 0
        val results = ArrayList<Int>()
        src.observeChanges(owner) { results += it }
        owner.lifecycle.markState(Lifecycle.State.RESUMED)
        assertSame(-1, results.single())
    }
}
