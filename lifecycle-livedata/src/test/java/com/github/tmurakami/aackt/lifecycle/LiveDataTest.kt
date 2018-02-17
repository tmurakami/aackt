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

package com.github.tmurakami.aackt.lifecycle

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LiveDataTest {

    @Suppress("unused")
    @get:Rule
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun toLiveData() = assertEquals("", "".toLiveData().value)

    @Test
    fun doOnActive() {
        var invoked = false
        val data = MutableLiveData<Int>().doOnActive { invoked = true }
        assertFalse(invoked)
        data.addObserver { }
        assertTrue(invoked)
    }

    @Test
    fun doOnInactive() {
        var invoked = false
        val data = MutableLiveData<Int>().doOnInactive { invoked = true }
        val observer = data.addObserver { }
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
        data.addObserver { }
        src.value = Unit
        assertTrue(invoked)
    }

    @Test
    fun filter() {
        val src = MutableLiveData<Int>()
        val received = src.filter { it > 10 }.readyToObserve()
        src.values(2, 30, 22, 5, 60, 1)
        received.assertEquals(30, 22, 60)
    }

    @Test
    fun filterNot() {
        val src = MutableLiveData<Int>()
        val received = src.filterNot { it > 10 }.readyToObserve()
        src.values(2, 30, 22, 5, 60, 1)
        received.assertEquals(2, 5, 1)
    }

    @Test
    fun filterNotNull() {
        val src = MutableLiveData<Int?>()
        val received = src.filterNotNull().readyToObserve()
        src.values(null, 1, null, 2, 3, null, null, 4, null)
        received.assertEquals(1, 2, 3, 4)
    }

    @Test
    fun filterIsInstance() {
        val src = MutableLiveData<Any?>()
        val received = src.filterIsInstance<Int>().readyToObserve()
        src.values(null, true, 1.toByte(), 2.toChar(), 3.0, 4.0f, 5, 6L, 7.toShort(), "8")
        received.assertEquals(5)
    }

    @Test
    fun distinct() {
        val src = MutableLiveData<Int>()
        val received = src.distinct().readyToObserve()
        src.values(1, 2, 2, 1, 3)
        received.assertEquals(1, 2, 3)
    }

    @Test
    fun distinctBy() {
        val src = MutableLiveData<Int>()
        val received = src.distinctBy { it % 3 }.readyToObserve()
        src.values(0, 1, 2, 3, 4, 5)
        received.assertEquals(0, 1, 2)
    }

    @Test
    fun drop() {
        val src = MutableLiveData<Int>()
        val received = src.drop(2).readyToObserve()
        src.values(1, 2, 3, 4)
        received.assertEquals(3, 4)
    }

    @Test
    fun dropWhile() {
        val src = MutableLiveData<Int>()
        val received = src.dropWhile { it < 3 }.readyToObserve()
        src.values(1, 2, 3, 4, 3, 2, 1)
        received.assertEquals(3, 4, 3, 2, 1)
    }

    @Test
    fun take() {
        val src = MutableLiveData<Int>()
        val received = src.take(2).readyToObserve()
        src.values(1, 2, 3, 4)
        received.assertEquals(1, 2)
    }

    @Test
    fun test() {
        val src = MutableLiveData<Int>()
        val take = src.take(2)
        val observer = take.addObserver { println(it) }
        src.values(1, 2, 3, 4)
        take.removeObserver(observer)
        take.addObserver { println(it) }
    }

    @Test
    fun takeWhile() {
        val src = MutableLiveData<Int>()
        val received = src.takeWhile { it < 3 }.readyToObserve()
        src.values(1, 2, 3, 4, 3, 2, 1)
        received.assertEquals(1, 2)
    }

    @Test
    fun plus() {
        val anySrc = MutableLiveData<Any>()
        val intSrc = MutableLiveData<Int>()
        val received = (anySrc + intSrc).readyToObserve()
        anySrc.values(20, 40, 60)
        intSrc.value = 1
        anySrc.values(80, 100)
        intSrc.value = 1
        received.assertEquals(20, 40, 60, 1, 80, 100, 1)
    }

    @Test
    fun zip() {
        val intSrc = MutableLiveData<Int>()
        val stringSrc = MutableLiveData<String>()
        val received = intSrc.zip(stringSrc).readyToObserve()
        intSrc.value = 1
        stringSrc.value = "a"
        intSrc.value = 2
        stringSrc.values("b", "c", "d")
        intSrc.values(3, 4, 5)
        received.assertEquals(1 to "a", 2 to "b", 3 to "c", 4 to "d")
    }

    @Test
    fun zip_transform() {
        val intSrc = MutableLiveData<Int>()
        val stringSrc = MutableLiveData<String>()
        val received = intSrc.zip(stringSrc) { a, b -> "$a$b" }.readyToObserve()
        intSrc.value = 1
        stringSrc.value = "a"
        intSrc.value = 2
        stringSrc.values("b", "c", "d")
        intSrc.values(3, 4, 5)
        received.assertEquals("1a", "2b", "3c", "4d")
    }

    @Test
    fun zipWithNext() {
        val src = MutableLiveData<Any>()
        val received = src.zipWithNext().readyToObserve()
        src.values(1, "a", 2, "b", "c", "d", 3, 4, 5)
        received.assertEquals(
            1 to "a",
            "a" to 2,
            2 to "b",
            "b" to "c",
            "c" to "d",
            "d" to 3,
            3 to 4,
            4 to 5
        )
    }

    @Test
    fun zipWithNext_transform() {
        val src = MutableLiveData<Any>()
        val received = src.zipWithNext { a, b -> "$a$b" }.readyToObserve()
        src.values(1, "a", 2, "b", "c", "d", 3, 4, 5)
        received.assertEquals("1a", "a2", "2b", "bc", "cd", "d3", "34", "45")
    }

    private fun <T> LiveData<T>.readyToObserve(): List<T> =
        ArrayList<T>().apply { addObserver { add(it) } }

    private fun <T> MutableLiveData<T>.values(vararg values: T) = values.forEach { value = it }

    private fun <T> List<T>.assertEquals(vararg values: T) =
        Assert.assertEquals(values.asList(), this)
}
