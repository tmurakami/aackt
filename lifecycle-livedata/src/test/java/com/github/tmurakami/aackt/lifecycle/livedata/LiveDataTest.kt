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
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.github.tmurakami.aackt.lifecycle.subscribe
import com.github.tmurakami.aackt.lifecycle.subscribeChanges
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LiveDataTest {
    @[Rule JvmField]
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun testSubscribe() {
        val data = MutableLiveData(-1)
        val observer = TestObserver<Int>()
        val subscription = data.subscribe(observer::onChanged)
        observer.assertValuesOnly(-1)
        repeat(3, data::setValue)
        subscription.unsubscribe()
        data.value = 3
        observer.assertValuesOnly(-1, 0, 1, 2)
    }

    @Test
    fun testSubscribe_unsubscribe() {
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
    fun testSubscribe_Observer() {
        val data = MutableLiveData(-1)
        val observer = TestObserver<Int>()
        val subscription = data.subscribe(observer)
        observer.assertValuesOnly(-1)
        repeat(3, data::setValue)
        subscription.unsubscribe()
        data.value = 3
        observer.assertValuesOnly(-1, 0, 1, 2)
    }

    @Test
    fun testSubscribe_Observer_unsubscribe() {
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
    fun testSubscribeChanges() {
        val data = MutableLiveData(-1)
        val observer = TestObserver<Int>()
        val subscription = data.subscribeChanges(observer::onChanged)
        observer.assertNoValues()
        repeat(3, data::setValue)
        subscription.unsubscribe()
        data.value = 3
        observer.assertValuesOnly(0, 1, 2)
    }

    @Test
    fun testSubscribeChanges_unsubscribe() {
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
    fun testSubscribeChanges_subscribe_MediatorLiveData() {
        val data = MediatorLiveData<Int>().apply { addSource(MutableLiveData(-1), this::setValue) }
        data.value = 0
        val observer = TestObserver<Int>()
        data.subscribeChanges(observer::onChanged)
        observer.assertValuesOnly(-1)
    }

    @Test
    fun testSubscribeChanges_Observer() {
        val data = MutableLiveData(-1)
        val observer = TestObserver<Int>()
        val subscription = data.subscribeChanges(observer)
        observer.assertNoValues()
        repeat(3, data::setValue)
        subscription.unsubscribe()
        data.value = 3
        observer.assertValuesOnly(0, 1, 2)
    }

    @Test
    fun testSubscribeChanges_Observer_unsubscribe() {
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
    fun testSubscribeChanges_Observer_subscribe_MediatorLiveData() {
        val data = MediatorLiveData<Int>().apply { addSource(MutableLiveData(-1), this::setValue) }
        data.value = 0
        val observer = TestObserver<Int>()
        data.subscribeChanges(observer)
        observer.assertValuesOnly(-1)
    }

    @Test
    fun testSubscribe_LifecycleOwner() {
        val owner = FakeLifecycleOwner()
        val data = MutableLiveData(-1)
        val observer = TestObserver<Int>()
        data.subscribe(owner, observer::onChanged)
        owner.resume().use {
            observer.assertValuesOnly(-1)
            repeat(3, data::setValue)
        }
        data.value = 3
        observer.assertValuesOnly(-1, 0, 1, 2)
    }

    @Test
    fun testSubscribe_LifecycleOwner_unsubscribe() {
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
    fun testSubscribe_LifecycleOwner_Observer() {
        val owner = FakeLifecycleOwner()
        val data = MutableLiveData(-1)
        val observer = TestObserver<Int>()
        data.subscribe(owner, observer)
        owner.resume().use {
            observer.assertValuesOnly(-1)
            repeat(3, data::setValue)
        }
        data.value = 3
        observer.assertValuesOnly(-1, 0, 1, 2)
    }

    @Test
    fun testSubscribe_LifecycleOwner_Observer_unsubscribe() {
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
    fun testSubscribeChanges_LifecycleOwner() {
        val owner = FakeLifecycleOwner()
        val data = MutableLiveData(-1)
        val observer = TestObserver<Int>()
        data.subscribeChanges(owner, observer::onChanged)
        owner.resume().use {
            observer.assertNoValues()
            repeat(3, data::setValue)
        }
        data.value = 3
        observer.assertValuesOnly(0, 1, 2)
    }

    @Test
    fun testSubscribeChanges_LifecycleOwner_unsubscribe() {
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
    fun testSubscribeChanges_LifecycleOwner_subscribe_MediatorLiveData() {
        val owner = FakeLifecycleOwner()
        val data = MediatorLiveData<Int>().apply { addSource(MutableLiveData(-1), this::setValue) }
        data.value = 0
        val observer = TestObserver<Int>()
        data.subscribeChanges(owner, observer::onChanged)
        owner.resume().use { observer.assertValuesOnly(-1) }
    }

    @Test
    fun testSubscribeChanges_LifecycleOwner_Observer() {
        val owner = FakeLifecycleOwner()
        val data = MutableLiveData(-1)
        val observer = TestObserver<Int>()
        data.subscribeChanges(owner, observer)
        owner.resume().use {
            observer.assertNoValues()
            repeat(3, data::setValue)
        }
        data.value = 3
        observer.assertValuesOnly(0, 1, 2)
    }

    @Test
    fun testSubscribeChanges_LifecycleOwner_Observer_unsubscribe() {
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
    fun testSubscribeChanges_LifecycleOwner_Observer_subscribe_MediatorLiveData() {
        val owner = FakeLifecycleOwner()
        val data = MediatorLiveData<Int>().apply { addSource(MutableLiveData(-1), this::setValue) }
        data.value = 0
        val observer = TestObserver<Int>()
        data.subscribeChanges(owner, observer)
        owner.resume().use { observer.assertValuesOnly(-1) }
    }
}
