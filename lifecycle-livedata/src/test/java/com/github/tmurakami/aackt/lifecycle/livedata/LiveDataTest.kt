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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.github.tmurakami.aackt.lifecycle.liveData
import com.github.tmurakami.aackt.lifecycle.observe
import com.github.tmurakami.aackt.lifecycle.observeChanges
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class LiveDataTest {

    @[Rule JvmField]
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun liveData() = assertSame(0, liveData(0).value)

    @Test
    fun observe() {
        val data = MutableLiveData<Int>().apply { value = -1 }
        val results = ArrayList<Int>()
        val observation = data.observe { results += it }
        assertTrue(results.isNotEmpty())
        data.value = 0
        observation.dispose()
        data.value = 1
        assertEquals(listOf(-1, 0), results)
    }

    @Test
    fun observe_dispose() {
        val data = MutableLiveData<Unit>()
        val observation = data.observe {}
        with(data) {
            assertTrue(hasObservers())
            assertTrue(hasActiveObservers())
        }
        observation.dispose()
        with(data) {
            assertFalse(hasObservers())
            assertFalse(hasActiveObservers())
        }
    }

    @Test
    fun observeChanges() {
        val data = MutableLiveData<Int>().apply { value = -1 }
        val results = ArrayList<Int>()
        val observation = data.observeChanges { results += it }
        assertTrue(results.isEmpty())
        data.value = 0
        observation.dispose()
        data.value = 1
        assertEquals(listOf(0), results)
    }

    @Test
    fun observeChanges_dispose() {
        val data = MutableLiveData<Unit>()
        val observation = data.observeChanges { }
        with(data) {
            assertTrue(hasObservers())
            assertTrue(hasActiveObservers())
        }
        observation.dispose()
        with(data) {
            assertFalse(hasObservers())
            assertFalse(hasActiveObservers())
        }
    }

    @Test
    fun observeChanges_observe_MediatorLiveData() {
        val data = MediatorLiveData<Int>().apply { addSource(liveData(-1)) { value = it } }
        data.value = 0
        val results = ArrayList<Int>()
        data.observeChanges { results += it }
        assertSame(-1, results.single())
    }

    @Test
    fun observe_LifecycleOwner() {
        val owner = TestLifecycleOwner()
        val data = MutableLiveData<Int>().apply { value = -1 }
        val results = ArrayList<Int>()
        data.observe(owner) { results += it }
        owner.lifecycle.markState(Lifecycle.State.RESUMED)
        assertTrue(results.isNotEmpty())
        data.value = 0
        owner.lifecycle.markState(Lifecycle.State.DESTROYED)
        data.value = 1
        assertEquals(listOf(-1, 0), results)
    }

    @Test
    fun observe_LifecycleOwner_dispose() {
        val owner = TestLifecycleOwner()
        val data = MutableLiveData<Unit>()
        val observation = data.observe(owner) { }
        with(data) {
            assertTrue(hasObservers())
            assertFalse(hasActiveObservers())
        }
        owner.lifecycle.markState(Lifecycle.State.RESUMED)
        with(data) {
            assertTrue(hasActiveObservers())
        }
        observation.dispose()
        with(data) {
            assertFalse(hasObservers())
            assertFalse(hasActiveObservers())
        }
    }

    @Test
    fun observeChanges_LifecycleOwner() {
        val owner = TestLifecycleOwner()
        val data = MutableLiveData<Int>().apply { value = -1 }
        val results = ArrayList<Int>()
        data.observeChanges(owner) { results += it }
        owner.lifecycle.markState(Lifecycle.State.RESUMED)
        assertTrue(results.isEmpty())
        data.value = 0
        owner.lifecycle.markState(Lifecycle.State.DESTROYED)
        data.value = 1
        assertEquals(listOf(0), results)
    }

    @Test
    fun observeChanges_LifecycleOwner_dispose() {
        val owner = TestLifecycleOwner()
        val data = MutableLiveData<Unit>()
        val observation = data.observeChanges(owner) { }
        with(data) {
            assertTrue(hasObservers())
            assertFalse(hasActiveObservers())
        }
        owner.lifecycle.markState(Lifecycle.State.RESUMED)
        with(data) {
            assertTrue(hasActiveObservers())
        }
        observation.dispose()
        with(data) {
            assertFalse(hasObservers())
            assertFalse(hasActiveObservers())
        }
    }

    @Test
    fun observeChanges_LifecycleOwner_observe_MediatorLiveData() {
        val owner = TestLifecycleOwner()
        val data = MediatorLiveData<Int>().apply { addSource(liveData(-1)) { value = it } }
        data.value = 0
        val results = ArrayList<Int>()
        data.observeChanges(owner) { results += it }
        owner.lifecycle.markState(Lifecycle.State.RESUMED)
        assertSame(-1, results.single())
    }
}
