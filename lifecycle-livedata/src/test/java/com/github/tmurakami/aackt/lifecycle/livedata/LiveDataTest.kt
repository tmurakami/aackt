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
import androidx.lifecycle.Observer
import com.github.tmurakami.aackt.lifecycle.subscribe
import com.github.tmurakami.aackt.lifecycle.subscribeChanges
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
    fun subscribe() {
        val data = MutableLiveData(-1)
        val results = ArrayList<Int>()
        val subscription = data.subscribe { results += it }
        assertTrue(results.isNotEmpty())
        data.value = 0
        subscription.unsubscribe()
        data.value = 1
        assertEquals(listOf(-1, 0), results)
    }

    @Test
    fun subscribe_unsubscribe() {
        val data = MutableLiveData<Unit>()
        val subscription = data.subscribe { }
        with(data) {
            assertTrue(hasObservers())
            assertTrue(hasActiveObservers())
        }
        subscription.unsubscribe()
        with(data) {
            assertFalse(hasObservers())
            assertFalse(hasActiveObservers())
        }
    }

    @Test
    fun subscribe_Observer() {
        val data = MutableLiveData(-1)
        val results = ArrayList<Int>()
        val subscription = data.subscribe(Observer { results += it })
        assertTrue(results.isNotEmpty())
        data.value = 0
        subscription.unsubscribe()
        data.value = 1
        assertEquals(listOf(-1, 0), results)
    }

    @Test
    fun subscribe_Observer_unsubscribe() {
        val data = MutableLiveData<Unit>()
        val subscription = data.subscribe(Observer { })
        with(data) {
            assertTrue(hasObservers())
            assertTrue(hasActiveObservers())
        }
        subscription.unsubscribe()
        with(data) {
            assertFalse(hasObservers())
            assertFalse(hasActiveObservers())
        }
    }

    @Test
    fun subscribeChanges() {
        val data = MutableLiveData(-1)
        val results = ArrayList<Int>()
        val subscription = data.subscribeChanges { results += it }
        assertTrue(results.isEmpty())
        data.value = 0
        subscription.unsubscribe()
        data.value = 1
        assertEquals(listOf(0), results)
    }

    @Test
    fun subscribeChanges_unsubscribe() {
        val data = MutableLiveData<Unit>()
        val subscription = data.subscribeChanges { }
        with(data) {
            assertTrue(hasObservers())
            assertTrue(hasActiveObservers())
        }
        subscription.unsubscribe()
        with(data) {
            assertFalse(hasObservers())
            assertFalse(hasActiveObservers())
        }
    }

    @Test
    fun subscribeChanges_subscribe_MediatorLiveData() {
        val data = MediatorLiveData<Int>().apply { addSource(MutableLiveData(-1)) { value = it } }
        data.value = 0
        val results = ArrayList<Int>()
        data.subscribeChanges { results += it }
        assertSame(-1, results.single())
    }

    @Test
    fun subscribeChanges_Observer() {
        val data = MutableLiveData(-1)
        val results = ArrayList<Int>()
        val subscription = data.subscribeChanges(Observer { results += it })
        assertTrue(results.isEmpty())
        data.value = 0
        subscription.unsubscribe()
        data.value = 1
        assertEquals(listOf(0), results)
    }

    @Test
    fun subscribeChanges_Observer_unsubscribe() {
        val data = MutableLiveData<Unit>()
        val subscription = data.subscribeChanges(Observer { })
        with(data) {
            assertTrue(hasObservers())
            assertTrue(hasActiveObservers())
        }
        subscription.unsubscribe()
        with(data) {
            assertFalse(hasObservers())
            assertFalse(hasActiveObservers())
        }
    }

    @Test
    fun subscribeChanges_Observer_subscribe_MediatorLiveData() {
        val data = MediatorLiveData<Int>().apply { addSource(MutableLiveData(-1)) { value = it } }
        data.value = 0
        val results = ArrayList<Int>()
        data.subscribeChanges(Observer { results += it })
        assertSame(-1, results.single())
    }

    @Test
    fun subscribe_LifecycleOwner() {
        val owner = TestLifecycleOwner()
        val data = MutableLiveData(-1)
        val results = ArrayList<Int>()
        data.subscribe(owner) { results += it }
        owner.lifecycle.markState(Lifecycle.State.RESUMED)
        assertTrue(results.isNotEmpty())
        data.value = 0
        owner.lifecycle.markState(Lifecycle.State.DESTROYED)
        data.value = 1
        assertEquals(listOf(-1, 0), results)
    }

    @Test
    fun subscribe_LifecycleOwner_unsubscribe() {
        val owner = TestLifecycleOwner()
        val data = MutableLiveData<Unit>()
        val subscription = data.subscribe(owner) { }
        with(data) {
            assertTrue(hasObservers())
            assertFalse(hasActiveObservers())
        }
        owner.lifecycle.markState(Lifecycle.State.RESUMED)
        with(data) {
            assertTrue(hasActiveObservers())
        }
        subscription.unsubscribe()
        with(data) {
            assertFalse(hasObservers())
            assertFalse(hasActiveObservers())
        }
    }

    @Test
    fun subscribe_LifecycleOwner_Observer() {
        val owner = TestLifecycleOwner()
        val data = MutableLiveData(-1)
        val results = ArrayList<Int>()
        data.subscribe(owner, Observer { results += it })
        owner.lifecycle.markState(Lifecycle.State.RESUMED)
        assertTrue(results.isNotEmpty())
        data.value = 0
        owner.lifecycle.markState(Lifecycle.State.DESTROYED)
        data.value = 1
        assertEquals(listOf(-1, 0), results)
    }

    @Test
    fun subscribe_LifecycleOwner_Observer_unsubscribe() {
        val owner = TestLifecycleOwner()
        val data = MutableLiveData<Unit>()
        val subscription = data.subscribe(owner, Observer { })
        with(data) {
            assertTrue(hasObservers())
            assertFalse(hasActiveObservers())
        }
        owner.lifecycle.markState(Lifecycle.State.RESUMED)
        with(data) {
            assertTrue(hasActiveObservers())
        }
        subscription.unsubscribe()
        with(data) {
            assertFalse(hasObservers())
            assertFalse(hasActiveObservers())
        }
    }

    @Test
    fun subscribeChanges_LifecycleOwner() {
        val owner = TestLifecycleOwner()
        val data = MutableLiveData(-1)
        val results = ArrayList<Int>()
        data.subscribeChanges(owner) { results += it }
        owner.lifecycle.markState(Lifecycle.State.RESUMED)
        assertTrue(results.isEmpty())
        data.value = 0
        owner.lifecycle.markState(Lifecycle.State.DESTROYED)
        data.value = 1
        assertEquals(listOf(0), results)
    }

    @Test
    fun subscribeChanges_LifecycleOwner_unsubscribe() {
        val owner = TestLifecycleOwner()
        val data = MutableLiveData<Unit>()
        val subscription = data.subscribeChanges(owner) { }
        with(data) {
            assertTrue(hasObservers())
            assertFalse(hasActiveObservers())
        }
        owner.lifecycle.markState(Lifecycle.State.RESUMED)
        with(data) {
            assertTrue(hasActiveObservers())
        }
        subscription.unsubscribe()
        with(data) {
            assertFalse(hasObservers())
            assertFalse(hasActiveObservers())
        }
    }

    @Test
    fun subscribeChanges_LifecycleOwner_subscribe_MediatorLiveData() {
        val owner = TestLifecycleOwner()
        val data = MediatorLiveData<Int>().apply { addSource(MutableLiveData(-1)) { value = it } }
        data.value = 0
        val results = ArrayList<Int>()
        data.subscribeChanges(owner) { results += it }
        owner.lifecycle.markState(Lifecycle.State.RESUMED)
        assertSame(-1, results.single())
    }

    @Test
    fun subscribeChanges_LifecycleOwner_Observer() {
        val owner = TestLifecycleOwner()
        val data = MutableLiveData(-1)
        val results = ArrayList<Int>()
        data.subscribeChanges(owner, Observer { results += it })
        owner.lifecycle.markState(Lifecycle.State.RESUMED)
        assertTrue(results.isEmpty())
        data.value = 0
        owner.lifecycle.markState(Lifecycle.State.DESTROYED)
        data.value = 1
        assertEquals(listOf(0), results)
    }

    @Test
    fun subscribeChanges_LifecycleOwner_Observer_unsubscribe() {
        val owner = TestLifecycleOwner()
        val data = MutableLiveData<Unit>()
        val subscription = data.subscribeChanges(owner, Observer { })
        with(data) {
            assertTrue(hasObservers())
            assertFalse(hasActiveObservers())
        }
        owner.lifecycle.markState(Lifecycle.State.RESUMED)
        with(data) {
            assertTrue(hasActiveObservers())
        }
        subscription.unsubscribe()
        with(data) {
            assertFalse(hasObservers())
            assertFalse(hasActiveObservers())
        }
    }

    @Test
    fun subscribeChanges_LifecycleOwner_Observer_subscribe_MediatorLiveData() {
        val owner = TestLifecycleOwner()
        val data = MediatorLiveData<Int>().apply { addSource(MutableLiveData(-1)) { value = it } }
        data.value = 0
        val results = ArrayList<Int>()
        data.subscribeChanges(owner, Observer { results += it })
        owner.lifecycle.markState(Lifecycle.State.RESUMED)
        assertSame(-1, results.single())
    }
}
