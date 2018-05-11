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
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import com.github.tmurakami.aackt.lifecycle.addObserver
import com.github.tmurakami.aackt.lifecycle.bindLiveData
import com.github.tmurakami.aackt.lifecycle.distinct
import com.github.tmurakami.aackt.lifecycle.distinctBy
import com.github.tmurakami.aackt.lifecycle.doOnActive
import com.github.tmurakami.aackt.lifecycle.doOnChanged
import com.github.tmurakami.aackt.lifecycle.doOnInactive
import com.github.tmurakami.aackt.lifecycle.drop
import com.github.tmurakami.aackt.lifecycle.dropWhile
import com.github.tmurakami.aackt.lifecycle.filter
import com.github.tmurakami.aackt.lifecycle.filterIsInstance
import com.github.tmurakami.aackt.lifecycle.filterNot
import com.github.tmurakami.aackt.lifecycle.filterNotNull
import com.github.tmurakami.aackt.lifecycle.liveData
import com.github.tmurakami.aackt.lifecycle.map
import com.github.tmurakami.aackt.lifecycle.mapNotNull
import com.github.tmurakami.aackt.lifecycle.plus
import com.github.tmurakami.aackt.lifecycle.switchMap
import com.github.tmurakami.aackt.lifecycle.take
import com.github.tmurakami.aackt.lifecycle.takeWhile
import com.github.tmurakami.aackt.lifecycle.unbindLiveData
import com.github.tmurakami.aackt.lifecycle.zip
import com.github.tmurakami.aackt.lifecycle.zipWithNext
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class LiveDataTest {

    @[Rule JvmField]
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun liveData() = assertEquals("test", liveData("test").value)

    @Test
    fun addObserver() {
        val src = MutableLiveData<Int>()
        val results = ArrayList<Int>()
        val observer = src.addObserver { results += it }
        src.value = 0
        src.removeObserver(observer)
        src.value = 1
        assertSame(0, results.single())
    }

    @Test
    fun addObserver_Observer() {
        val src = MutableLiveData<Int>()
        val results = ArrayList<Int?>()
        val observer = src.addObserver(Observer { results += it })
        src.value = 0
        src.removeObserver(observer)
        src.value = 1
        assertSame(0, results.single())
    }

    @Test
    fun bindLiveData() {
        val owner = TestLifecycleOwner()
        val src = MutableLiveData<Int>()
        val results = ArrayList<Int>()
        owner.bindLiveData(src) { results += it }
        owner.lifecycle.markState(Lifecycle.State.RESUMED)
        src.value = 0
        owner.lifecycle.markState(Lifecycle.State.DESTROYED)
        src.value = 1
        assertSame(0, results.single())
    }

    @Test
    fun bindLiveData_Observer() {
        val owner = TestLifecycleOwner()
        val src = MutableLiveData<Int>()
        val results = ArrayList<Int?>()
        owner.bindLiveData(src, Observer { results += it })
        owner.lifecycle.markState(Lifecycle.State.RESUMED)
        src.value = 0
        owner.lifecycle.markState(Lifecycle.State.DESTROYED)
        src.value = 1
        assertSame(0, results.single())
    }

    @Test
    fun unbindLiveData() {
        val owner = TestLifecycleOwner()
        val src = MutableLiveData<Int>()
        val results = ArrayList<Int>()
        owner.bindLiveData(src) { results += it }
        owner.lifecycle.markState(Lifecycle.State.RESUMED)
        src.value = 0
        owner.unbindLiveData(src)
        src.value = 1
        assertSame(0, results.single())
    }

    @Test
    fun map() {
        val src = MutableLiveData<Int>()
        val observer = src.map { it * it }.test()
        src.values(1, 2, 3, 4, 5)
        observer.assertValues(1, 4, 9, 16, 25)
    }

    @Test
    fun mapNotNull() {
        val src = MutableLiveData<Int>()
        val observer = src.mapNotNull { if (it % 2 == 0) null else it }.test()
        src.values(1, 2, 3, 4, 5)
        observer.assertValues(1, 3, 5)
    }

    @Test
    fun switchMap() {
        val src = MutableLiveData<Int>()
        var activeCount = 0
        var inactiveCount = 0
        val observer = src.switchMap {
            liveData(it).doOnActive { activeCount++ }.doOnInactive { inactiveCount++ }
        }.test()
        src.values(1, 2, 3, 4, 5)
        observer.assertValues(1, 2, 3, 4, 5)
        assertSame(5, activeCount)
        assertSame(4, inactiveCount)
    }

    @Test
    fun doOnActive() {
        var invoked = false
        val data = MutableLiveData<Int>().doOnActive { invoked = true }
        assertFalse(invoked)
        data.test()
        assertTrue(invoked)
    }

    @Test
    fun doOnInactive() {
        var invoked = false
        val data = MutableLiveData<Int>().doOnInactive { invoked = true }
        val observer = data.test()
        assertFalse(invoked)
        data.removeObserver(observer)
        assertTrue(invoked)
    }

    @Test
    fun doOnChanged() {
        val src = MutableLiveData<Unit>()
        var invoked = false
        val data = src.doOnChanged { invoked = true }
        assertFalse(invoked)
        data.test()
        src.value = Unit
        assertTrue(invoked)
    }

    @Test
    fun filter() {
        val src = MutableLiveData<Int>()
        val observer = src.filter { it > 10 }.test()
        src.values(2, 30, 22, 5, 60, 1)
        observer.assertValues(30, 22, 60)
    }

    @Test
    fun filterNot() {
        val src = MutableLiveData<Int>()
        val observer = src.filterNot { it > 10 }.test()
        src.values(2, 30, 22, 5, 60, 1)
        observer.assertValues(2, 5, 1)
    }

    @Test
    fun filterNotNull() {
        val src = MutableLiveData<Int?>()
        val observer = src.filterNotNull().test()
        src.values(null, 1, null, 2, 3, null, null, 4, null)
        observer.assertValues(1, 2, 3, 4)
    }

    @Test
    fun filterIsInstance() {
        val src = MutableLiveData<Any?>()
        val observer = src.filterIsInstance<Int>().test()
        src.values(null, true, 1.toByte(), 2.toChar(), 3.0, 4.0f, 5, 6L, 7.toShort(), "8")
        observer.assertValues(5)
    }

    @Test
    fun distinct() {
        val src = MutableLiveData<Int>()
        val observer = src.distinct().test()
        src.values(1, 2, 2, 1, 3)
        observer.assertValues(1, 2, 3)
    }

    @Test
    fun distinctBy() {
        val src = MutableLiveData<Int>()
        val observer = src.distinctBy { it % 3 }.test()
        src.values(0, 1, 2, 3, 4, 5)
        observer.assertValues(0, 1, 2)
    }

    @Test
    fun drop() {
        val src = MutableLiveData<Int>()
        val observer = src.drop(2).test()
        src.values(1, 2, 3, 4)
        observer.assertValues(3, 4)
    }

    @Test
    fun dropWhile() {
        val src = MutableLiveData<Int>()
        val observer = src.dropWhile { it < 3 }.test()
        src.values(1, 2, 3, 4, 3, 2, 1)
        observer.assertValues(3, 4, 3, 2, 1)
    }

    @Test
    fun take() {
        val src = MutableLiveData<Int>()
        val observer = src.take(2).test()
        src.values(1, 2, 3, 4)
        observer.assertValues(1, 2)
    }

    @Test
    fun takeWhile() {
        val src = MutableLiveData<Int>()
        val observer = src.takeWhile { it < 3 }.test()
        src.values(1, 2, 3, 4, 3, 2, 1)
        observer.assertValues(1, 2)
    }

    @Test
    fun plus() {
        val anySrc = MutableLiveData<Any>()
        val intSrc = MutableLiveData<Int>()
        val observer = (anySrc + intSrc).test()
        anySrc.values(20, 40, 60)
        intSrc.value = 1
        anySrc.values(80, 100)
        intSrc.value = 1
        observer.assertValues(20, 40, 60, 1, 80, 100, 1)
    }

    @Test
    fun zip() {
        val intSrc = MutableLiveData<Int>()
        val stringSrc = MutableLiveData<String>()
        val observer = intSrc.zip(stringSrc).test()
        intSrc.value = 1
        stringSrc.value = "a"
        intSrc.value = 2
        stringSrc.values("b", "c")
        intSrc.values(3, 4)
        observer.assertValues(1 to "a", 2 to "b", 3 to "c")
    }

    @Test
    fun zip_transform() {
        val intSrc = MutableLiveData<Int>()
        val stringSrc = MutableLiveData<String>()
        val observer = intSrc.zip(stringSrc) { a, b -> "$a$b" }.test()
        intSrc.value = 1
        stringSrc.value = "a"
        intSrc.value = 2
        stringSrc.values("b", "c")
        intSrc.values(3, 4)
        observer.assertValues("1a", "2b", "3c")
    }

    @Test
    fun zipWithNext() {
        val src = MutableLiveData<Any>()
        val observer = src.zipWithNext().test()
        src.values(1, "a", 2, "b", "c", 3, 4)
        observer.assertValues(1 to "a", "a" to 2, 2 to "b", "b" to "c", "c" to 3, 3 to 4)
    }

    @Test
    fun zipWithNext_transform() {
        val src = MutableLiveData<Any>()
        val observer = src.zipWithNext { a, b -> "$a$b" }.test()
        src.values(1, "a", 2, "b", "c", 3, 4)
        observer.assertValues("1a", "a2", "2b", "bc", "c3", "34")
    }

    private fun <T> MutableLiveData<T>.values(vararg values: T) = values.forEach { value = it }
}
