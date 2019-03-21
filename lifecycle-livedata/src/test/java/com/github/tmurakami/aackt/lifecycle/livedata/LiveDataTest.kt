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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.github.tmurakami.aackt.lifecycle.getValue
import com.github.tmurakami.aackt.lifecycle.subscribe
import com.github.tmurakami.aackt.lifecycle.subscribeChanges
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class LiveDataTest {
    @[Rule JvmField]
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun getValue() {
        val data = object : LiveData<Int>(0) {}
        val value by data
        assertSame(0, value)
    }

    @Test
    fun subscribe() {
        val data = MutableLiveData(-1)
        val observer = TestObserver<Int>()
        val subscription = data.subscribe { observer.onChanged(it) }
        observer.assertValuesOnly(-1)
        repeat(3) { data.value = it }
        subscription.unsubscribe()
        data.value = 3
        observer.assertValuesOnly(-1, 0, 1, 2)
    }

    @Test
    fun subscribe_unsubscribe() {
        val data = MutableLiveData<Unit>()
        val subscription = data.subscribe { }
        data.run {
            assertTrue(hasObservers())
            assertTrue(hasActiveObservers())
            subscription.unsubscribe()
            assertFalse(hasObservers())
            assertFalse(hasActiveObservers())
        }
    }

    @Test
    fun subscribe_Observer() {
        val data = MutableLiveData(-1)
        val observer = TestObserver<Int>()
        val subscription = data.subscribe(observer)
        observer.assertValuesOnly(-1)
        repeat(3) { data.value = it }
        subscription.unsubscribe()
        data.value = 3
        observer.assertValuesOnly(-1, 0, 1, 2)
    }

    @Test
    fun subscribe_Observer_unsubscribe() {
        val data = MutableLiveData<Unit>()
        val subscription = data.subscribe(Observer { })
        data.run {
            assertTrue(hasObservers())
            assertTrue(hasActiveObservers())
            subscription.unsubscribe()
            assertFalse(hasObservers())
            assertFalse(hasActiveObservers())
        }
    }

    @Test
    fun subscribeChanges() {
        val data = MutableLiveData(-1)
        val observer = TestObserver<Int>()
        val subscription = data.subscribeChanges { observer.onChanged(it) }
        observer.assertNoValues()
        repeat(3) { data.value = it }
        subscription.unsubscribe()
        data.value = 3
        observer.assertValuesOnly(0, 1, 2)
    }

    @Test
    fun subscribeChanges_unsubscribe() {
        val data = MutableLiveData<Unit>()
        val subscription = data.subscribeChanges { }
        data.run {
            assertTrue(hasObservers())
            assertTrue(hasActiveObservers())
            subscription.unsubscribe()
            assertFalse(hasObservers())
            assertFalse(hasActiveObservers())
        }
    }

    @Test
    fun subscribeChanges_subscribe_MediatorLiveData() {
        val data = MediatorLiveData<Int>().apply { addSource(MutableLiveData(-1)) { value = it } }
        data.value = 0
        val observer = TestObserver<Int>()
        data.subscribeChanges { observer.onChanged(it) }
        observer.assertValuesOnly(-1)
    }

    @Test
    fun subscribeChanges_Observer() {
        val data = MutableLiveData(-1)
        val observer = TestObserver<Int>()
        val subscription = data.subscribeChanges(observer)
        observer.assertNoValues()
        repeat(3) { data.value = it }
        subscription.unsubscribe()
        data.value = 3
        observer.assertValuesOnly(0, 1, 2)
    }

    @Test
    fun subscribeChanges_Observer_unsubscribe() {
        val data = MutableLiveData<Unit>()
        val subscription = data.subscribeChanges(Observer { })
        data.run {
            assertTrue(hasObservers())
            assertTrue(hasActiveObservers())
            subscription.unsubscribe()
            assertFalse(hasObservers())
            assertFalse(hasActiveObservers())
        }
    }

    @Test
    fun subscribeChanges_Observer_subscribe_MediatorLiveData() {
        val data = MediatorLiveData<Int>().apply { addSource(MutableLiveData(-1)) { value = it } }
        data.value = 0
        val observer = TestObserver<Int>()
        data.subscribeChanges(observer)
        observer.assertValuesOnly(-1)
    }

    @Test
    fun subscribe_LifecycleOwner() {
        val owner = FakeLifecycleOwner()
        val data = MutableLiveData(-1)
        val observer = TestObserver<Int>()
        data.subscribe(owner) { observer.onChanged(it) }
        owner.resume().use {
            observer.assertValuesOnly(-1)
            repeat(3) { data.value = it }
        }
        data.value = 3
        observer.assertValuesOnly(-1, 0, 1, 2)
    }

    @Test
    fun subscribe_LifecycleOwner_unsubscribe() {
        val owner = FakeLifecycleOwner()
        val data = MutableLiveData<Unit>()
        val subscription = data.subscribe(owner) { }
        data.run {
            assertTrue(hasObservers())
            assertFalse(hasActiveObservers())
            owner.resume().use {
                assertTrue(hasActiveObservers())
                subscription.unsubscribe()
                assertFalse(hasObservers())
                assertFalse(hasActiveObservers())
            }
        }
    }

    @Test
    fun subscribe_LifecycleOwner_Observer() {
        val owner = FakeLifecycleOwner()
        val data = MutableLiveData(-1)
        val observer = TestObserver<Int>()
        data.subscribe(owner, observer)
        owner.resume().use {
            observer.assertValuesOnly(-1)
            repeat(3) { data.value = it }
        }
        data.value = 3
        observer.assertValuesOnly(-1, 0, 1, 2)
    }

    @Test
    fun subscribe_LifecycleOwner_Observer_unsubscribe() {
        val owner = FakeLifecycleOwner()
        val data = MutableLiveData<Unit>()
        val subscription = data.subscribe(owner, Observer { })
        data.run {
            assertTrue(hasObservers())
            assertFalse(hasActiveObservers())
            owner.resume().use {
                assertTrue(hasActiveObservers())
                subscription.unsubscribe()
                assertFalse(hasObservers())
                assertFalse(hasActiveObservers())
            }
        }
    }

    @Test
    fun subscribeChanges_LifecycleOwner() {
        val owner = FakeLifecycleOwner()
        val data = MutableLiveData(-1)
        val observer = TestObserver<Int>()
        data.subscribeChanges(owner) { observer.onChanged(it) }
        owner.resume().use {
            observer.assertNoValues()
            repeat(3) { data.value = it }
        }
        data.value = 3
        observer.assertValuesOnly(0, 1, 2)
    }

    @Test
    fun subscribeChanges_LifecycleOwner_unsubscribe() {
        val owner = FakeLifecycleOwner()
        val data = MutableLiveData<Unit>()
        val subscription = data.subscribeChanges(owner) { }
        data.run {
            assertTrue(hasObservers())
            assertFalse(hasActiveObservers())
            owner.resume().use {
                assertTrue(hasActiveObservers())
                subscription.unsubscribe()
                assertFalse(hasObservers())
                assertFalse(hasActiveObservers())
            }
        }
    }

    @Test
    fun subscribeChanges_LifecycleOwner_subscribe_MediatorLiveData() {
        val owner = FakeLifecycleOwner()
        val data = MediatorLiveData<Int>().apply { addSource(MutableLiveData(-1)) { value = it } }
        data.value = 0
        val observer = TestObserver<Int>()
        data.subscribeChanges(owner) { observer.onChanged(it) }
        owner.resume().use { observer.assertValuesOnly(-1) }
    }

    @Test
    fun subscribeChanges_LifecycleOwner_Observer() {
        val owner = FakeLifecycleOwner()
        val data = MutableLiveData(-1)
        val observer = TestObserver<Int>()
        data.subscribeChanges(owner, observer)
        owner.resume().use {
            observer.assertNoValues()
            repeat(3) { data.value = it }
        }
        data.value = 3
        observer.assertValuesOnly(0, 1, 2)
    }

    @Test
    fun subscribeChanges_LifecycleOwner_Observer_unsubscribe() {
        val owner = FakeLifecycleOwner()
        val data = MutableLiveData<Unit>()
        val subscription = data.subscribeChanges(owner, Observer { })
        data.run {
            assertTrue(hasObservers())
            assertFalse(hasActiveObservers())
            owner.resume().use {
                assertTrue(hasActiveObservers())
                subscription.unsubscribe()
                assertFalse(hasObservers())
                assertFalse(hasActiveObservers())
            }
        }
    }

    @Test
    fun subscribeChanges_LifecycleOwner_Observer_subscribe_MediatorLiveData() {
        val owner = FakeLifecycleOwner()
        val data = MediatorLiveData<Int>().apply { addSource(MutableLiveData(-1)) { value = it } }
        data.value = 0
        val observer = TestObserver<Int>()
        data.subscribeChanges(owner, observer)
        owner.resume().use { observer.assertValuesOnly(-1) }
    }
}
