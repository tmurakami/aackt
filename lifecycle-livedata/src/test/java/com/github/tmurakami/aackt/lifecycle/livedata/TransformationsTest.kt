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
import com.github.tmurakami.aackt.lifecycle.combineLatest
import com.github.tmurakami.aackt.lifecycle.distinct
import com.github.tmurakami.aackt.lifecycle.distinctUntilChanged
import com.github.tmurakami.aackt.lifecycle.doOnActive
import com.github.tmurakami.aackt.lifecycle.doOnChanged
import com.github.tmurakami.aackt.lifecycle.doOnInactive
import com.github.tmurakami.aackt.lifecycle.drop
import com.github.tmurakami.aackt.lifecycle.dropWhile
import com.github.tmurakami.aackt.lifecycle.filter
import com.github.tmurakami.aackt.lifecycle.filterIsInstance
import com.github.tmurakami.aackt.lifecycle.filterNot
import com.github.tmurakami.aackt.lifecycle.filterNotNull
import com.github.tmurakami.aackt.lifecycle.mapNotNull
import com.github.tmurakami.aackt.lifecycle.plus
import com.github.tmurakami.aackt.lifecycle.take
import com.github.tmurakami.aackt.lifecycle.takeWhile
import com.github.tmurakami.aackt.lifecycle.withLatestFrom
import com.github.tmurakami.aackt.lifecycle.zip
import com.github.tmurakami.aackt.lifecycle.zipWithNext
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TransformationsTest {
    @[Rule JvmField]
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun testMapNotNull() {
        val expected = listOf(1, 3)
        val actual = mutableListOf<Int>()
        val data = MutableLiveData<Int>()
        data.mapNotNull { if (it % 2 == 0) null else it }.observeForever { actual += it }
        repeat(5, data::setValue)
        assertEquals(expected, actual)
    }

    @Test
    fun testDoOnActive() {
        var active = false
        val data = MutableLiveData<Unit>().doOnActive { active = true }
        assertFalse(active)
        data.observeForever { }
        assertTrue(active)
    }

    @Test
    fun testDoOnActive_Runnable() {
        var active = false
        val data = MutableLiveData<Unit>().doOnActive(Runnable { active = true })
        assertFalse(active)
        data.observeForever { }
        assertTrue(active)
    }

    @Test
    fun testDoOnInactive() {
        var inactive = false
        val data = MutableLiveData<Unit>().doOnInactive { inactive = true }
        val observer = Observer<Unit> { }.also { data.observeForever(it) }
        assertFalse(inactive)
        data.removeObserver(observer)
        assertTrue(inactive)
    }

    @Test
    fun testDoOnInactive_Runnable() {
        var inactive = false
        val data = MutableLiveData<Unit>().doOnInactive(Runnable { inactive = true })
        val observer = Observer<Unit> { }.also { data.observeForever(it) }
        assertFalse(inactive)
        data.removeObserver(observer)
        assertTrue(inactive)
    }

    @Test
    fun testDoOnChanged() {
        val data = MutableLiveData<Unit>()
        var changed = false
        data.doOnChanged { changed = true }.observeForever { }
        assertFalse(changed)
        data.value = Unit
        assertTrue(changed)
    }

    @Test
    fun testFilter() {
        val expected = listOf(30, 22, 60)
        val actual = mutableListOf<Int>()
        val data = MutableLiveData<Int>()
        data.filter { it > 10 }.observeForever { actual += it }
        data.run {
            value = 2
            value = 30
            value = 22
            value = 5
            value = 60
            value = 1
        }
        assertEquals(expected, actual)
    }

    @Test
    fun testFilterNot() {
        val expected = listOf(2, 5, 1)
        val actual = mutableListOf<Int>()
        val data = MutableLiveData<Int>()
        data.filterNot { it > 10 }.observeForever { actual += it }
        data.run {
            value = 2
            value = 30
            value = 22
            value = 5
            value = 60
            value = 1
        }
        assertEquals(expected, actual)
    }

    @Test
    fun testFilterNotNull() {
        val expected = listOf(1, 2, 3, 4)
        val actual = mutableListOf<Int>()
        val data = MutableLiveData<Int?>()
        data.filterNotNull().observeForever { actual += it }
        data.run {
            value = null
            value = 1
            value = null
            value = 2
            value = 3
            value = null
            value = null
            value = 4
            value = null
        }
        assertEquals(expected, actual)
    }

    @Test
    fun testFilterIsInstance() {
        val expected = listOf(5)
        val actual = mutableListOf<Int>()
        val data = MutableLiveData<Any?>()
        data.filterIsInstance<Int>().observeForever { actual += it }
        data.run {
            value = null
            value = true
            value = 1.toByte()
            value = '2'
            value = 3.0
            value = 4.0f
            value = 5
            value = 6L
            value = 7.toShort()
            value = "8"
        }
        assertEquals(expected, actual)
    }

    @Test
    fun testFilterIsInstance_nullable() {
        val expected = listOf(null, 5)
        val actual = mutableListOf<Int?>()
        val data = MutableLiveData<Any?>()
        data.filterIsInstance<Int?>().observeForever { actual += it }
        data.run {
            value = null
            value = true
            value = 1.toByte()
            value = '2'
            value = 3.0
            value = 4.0f
            value = 5
            value = 6L
            value = 7.toShort()
            value = "8"
        }
        assertEquals(expected, actual)
    }

    @Test
    fun testDistinct() {
        val expected = listOf(0, 1)
        val actual = mutableListOf<Int>()
        val data = MutableLiveData<Int>()
        data.distinct { it % 2 }.observeForever { actual += it }
        data.run {
            value = 0
            value = 2
            value = 1
            value = 2
            value = 1
            value = 1
        }
        assertEquals(expected, actual)
    }

    @Test
    fun testDistinct_by_default_selector() {
        val expected = listOf(0, 2, 1)
        val actual = mutableListOf<Int>()
        val data = MutableLiveData<Int>()
        data.distinct().observeForever { actual += it }
        data.run {
            value = 0
            value = 2
            value = 1
            value = 2
            value = 1
            value = 1
        }
        assertEquals(expected, actual)
    }

    @Test
    fun testDistinct_by_selector_using_identityHashCode() {
        val s1 = String()
        val s2 = String()
        val expected = listOf(s1, s2)
        val actual = mutableListOf<String>()
        val data = MutableLiveData<String>()
        data.distinct(System::identityHashCode).observeForever { actual += it }
        data.run {
            value = s1
            value = s2
            value = s1
            value = s2
            value = s2
        }
        assertEquals(expected, actual)
    }

    @Test
    fun testDistinctUntilChanged() {
        val expected = listOf(0, 1, 2, 1)
        val actual = mutableListOf<Int>()
        val data = MutableLiveData<Int>()
        data.distinctUntilChanged { it % 2 }.observeForever { actual += it }
        data.run {
            value = 0
            value = 2
            value = 1
            value = 2
            value = 1
            value = 1
        }
        assertEquals(expected, actual)
    }

    @Test
    fun testDistinctUntilChanged_by_selector_using_identityHashCode() {
        val s1 = String()
        val s2 = String()
        val expected = listOf(s1, s2, s1, s2)
        val actual = mutableListOf<String>()
        val data = MutableLiveData<String>()
        data.distinctUntilChanged(System::identityHashCode).observeForever { actual += it }
        data.run {
            value = s1
            value = s2
            value = s1
            value = s2
            value = s2
        }
        assertEquals(expected, actual)
    }

    @Test
    fun testDrop() {
        val expected = listOf(3, 4)
        val actual = mutableListOf<Int>()
        val data = MutableLiveData<Int>()
        data.drop(3).observeForever { actual += it }
        repeat(5, data::setValue)
        assertEquals(expected, actual)
    }

    @Test
    fun testDropWhile() {
        val expected = listOf(4, 3, 2, 1, 0)
        val actual = mutableListOf<Int>()
        val data = MutableLiveData<Int>()
        data.dropWhile { it < 4 }.observeForever { actual += it }
        repeat(5, data::setValue)
        (3 downTo 0).forEach(data::setValue)
        assertEquals(expected, actual)
    }

    @Test
    fun testTake() {
        val expected = listOf(0, 1, 2)
        val actual = mutableListOf<Int>()
        val data = MutableLiveData<Int>()
        data.take(3).observeForever { actual += it }
        repeat(5, data::setValue)
        assertEquals(expected, actual)
    }

    @Test
    fun testTakeWhile() {
        val expected = listOf(0, 1, 2, 3)
        val actual = mutableListOf<Int>()
        val data = MutableLiveData<Int>()
        data.takeWhile { it < 4 }.observeForever { actual += it }
        repeat(5, data::setValue)
        (3 downTo 0).forEach(data::setValue)
        assertEquals(expected, actual)
    }

    @Test
    fun testPlus() {
        val expected = listOf<Any>(20, 40, 60, 1, 80, 100, 1)
        val actual = mutableListOf<Any>()
        val anyData = MutableLiveData<Any>()
        val intData = MutableLiveData<Int>()
        (anyData + intData).observeForever { actual += it }
        anyData.run {
            value = 20
            value = 40
            value = 60
        }
        intData.value = 1
        anyData.run {
            value = 80
            value = 100
        }
        intData.value = 1
        assertEquals(expected, actual)
    }

    @Test
    fun testCombineLatest() {
        val expected = listOf<Any>(1 to 'a', 2 to 'a', 2 to 'b', 2 to 'c', 3 to 'c', 4 to 'c')
        val actual = mutableListOf<Any>()
        val intData = MutableLiveData<Int>()
        val charData = MutableLiveData<Char>()
        intData.combineLatest(charData).observeForever { actual += it }
        intData.value = 1
        charData.value = 'a'
        intData.value = 2
        ('b'..'c').forEach(charData::setValue)
        (3..4).forEach(intData::setValue)
        assertEquals(expected, actual)
    }

    @Test
    fun testCombineLatest_transform() {
        val expected = listOf("1a", "2a", "2b", "2c", "3c", "4c")
        val actual = mutableListOf<String>()
        val intData = MutableLiveData<Int>()
        val charData = MutableLiveData<Char>()
        intData.combineLatest(charData) { a, b -> "$a$b" }.observeForever { actual += it }
        intData.value = 1
        charData.value = 'a'
        intData.value = 2
        ('b'..'c').forEach(charData::setValue)
        (3..4).forEach(intData::setValue)
        assertEquals(expected, actual)
    }

    @Test
    fun testWithLatestFrom() {
        val expected = listOf<Any>(2 to 'a', 3 to 'c', 4 to 'c')
        val actual = mutableListOf<Any>()
        val intData = MutableLiveData<Int>()
        val charData = MutableLiveData<Char>()
        intData.withLatestFrom(charData).observeForever { actual += it }
        intData.value = 1
        charData.value = 'a'
        intData.value = 2
        ('b'..'c').forEach(charData::setValue)
        (3..4).forEach(intData::setValue)
        assertEquals(expected, actual)
    }

    @Test
    fun testWithLatestFrom_transform() {
        val expected = listOf("2a", "3c", "4c")
        val actual = mutableListOf<String>()
        val intData = MutableLiveData<Int>()
        val charData = MutableLiveData<Char>()
        intData.withLatestFrom(charData) { a, b -> "$a$b" }.observeForever { actual += it }
        intData.value = 1
        charData.value = 'a'
        intData.value = 2
        ('b'..'c').forEach(charData::setValue)
        (3..4).forEach(intData::setValue)
        assertEquals(expected, actual)
    }

    @Test
    fun testZip() {
        val expected = listOf<Any>(1 to 'a', 2 to 'b', 3 to 'c')
        val actual = mutableListOf<Any>()
        val intData = MutableLiveData<Int>()
        val charData = MutableLiveData<Char>()
        intData.zip(charData).observeForever { actual += it }
        intData.value = 1
        charData.value = 'a'
        intData.value = 2
        ('b'..'c').forEach(charData::setValue)
        (3..4).forEach(intData::setValue)
        assertEquals(expected, actual)
    }

    @Test
    fun testZip_transform() {
        val expected = listOf("1a", "2b", "3c")
        val actual = mutableListOf<String>()
        val intData = MutableLiveData<Int>()
        val charData = MutableLiveData<Char>()
        intData.zip(charData) { a, b -> "$a$b" }.observeForever { actual += it }
        intData.value = 1
        charData.value = 'a'
        intData.value = 2
        ('b'..'c').forEach(charData::setValue)
        (3..4).forEach(intData::setValue)
        assertEquals(expected, actual)
    }

    @Test
    fun testZipWithNext() {
        val expected = listOf<Any>(1 to 'a', 'a' to 2, 2 to 'b', 'b' to 'c', 'c' to 3, 3 to 4)
        val actual = mutableListOf<Any>()
        val data = MutableLiveData<Any>()
        data.zipWithNext().observeForever { actual += it }
        data.run {
            value = 1
            value = 'a'
            value = 2
            value = 'b'
            value = 'c'
            value = 3
            value = 4
        }
        assertEquals(expected, actual)
    }

    @Test
    fun testZipWithNext_transform() {
        val expected = listOf("1a", "a2", "2b", "bc", "c3", "34")
        val actual = mutableListOf<String>()
        val data = MutableLiveData<Any>()
        data.zipWithNext { a, b -> "$a$b" }.observeForever { actual += it }
        data.run {
            value = 1
            value = 'a'
            value = 2
            value = 'b'
            value = 'c'
            value = 3
            value = 4
        }
        assertEquals(expected, actual)
    }
}
