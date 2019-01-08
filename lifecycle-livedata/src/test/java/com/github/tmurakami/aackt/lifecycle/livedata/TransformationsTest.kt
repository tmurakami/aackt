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
import com.github.tmurakami.aackt.lifecycle.distinctBy
import com.github.tmurakami.aackt.lifecycle.distinctUntilChangedBy
import com.github.tmurakami.aackt.lifecycle.doOnActive
import com.github.tmurakami.aackt.lifecycle.doOnChanged
import com.github.tmurakami.aackt.lifecycle.doOnInactive
import com.github.tmurakami.aackt.lifecycle.drop
import com.github.tmurakami.aackt.lifecycle.dropWhile
import com.github.tmurakami.aackt.lifecycle.filter
import com.github.tmurakami.aackt.lifecycle.filterIsInstance
import com.github.tmurakami.aackt.lifecycle.filterNot
import com.github.tmurakami.aackt.lifecycle.filterNotNull
import com.github.tmurakami.aackt.lifecycle.map
import com.github.tmurakami.aackt.lifecycle.mapNotNull
import com.github.tmurakami.aackt.lifecycle.mutableLiveData
import com.github.tmurakami.aackt.lifecycle.plus
import com.github.tmurakami.aackt.lifecycle.switchMap
import com.github.tmurakami.aackt.lifecycle.take
import com.github.tmurakami.aackt.lifecycle.takeWhile
import com.github.tmurakami.aackt.lifecycle.withLatestFrom
import com.github.tmurakami.aackt.lifecycle.zip
import com.github.tmurakami.aackt.lifecycle.zipWithNext
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotSame
import kotlin.test.assertTrue

class TransformationsTest {
    @[Rule JvmField]
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun map() {
        val actual = mutableListOf<Int>()
        val data = MutableLiveData<Int>()
        data.map { it * it }.observeForever { actual += it }
        (1..5).forEach { data.value = it }
        assertEquals(listOf(1, 4, 9, 16, 25), actual)
    }

    @Test
    fun mapNotNull() {
        val actual = mutableListOf<Int>()
        val data = MutableLiveData<Int>()
        data.mapNotNull { if (it % 2 == 0) null else it }.observeForever { actual += it }
        (1..5).forEach { data.value = it }
        assertEquals(listOf(1, 3, 5), actual)
    }

    @Test
    fun switchMap() {
        val actual = mutableListOf<Int>()
        val data = MutableLiveData<Int>()
        data.switchMap { mutableLiveData(it * it) }.observeForever { actual += it }
        (1..5).forEach { data.value = it }
        assertEquals(listOf(1, 4, 9, 16, 25), actual)
    }

    @Test
    fun doOnActive() {
        var active = false
        val data = MutableLiveData<Unit>().doOnActive { active = true }
        assertFalse(active)
        data.observeForever { }
        assertTrue(active)
    }

    @Test
    fun doOnInactive() {
        var inactive = false
        val data = MutableLiveData<Unit>().doOnInactive { inactive = true }
        val observer = Observer<Unit> { }.also { data.observeForever(it) }
        assertFalse(inactive)
        data.removeObserver(observer)
        assertTrue(inactive)
    }

    @Test
    fun doOnChanged() {
        val data = MutableLiveData<Unit>()
        var changed = false
        data.doOnChanged { changed = true }.observeForever { }
        assertFalse(changed)
        data.value = Unit
        assertTrue(changed)
    }

    @Test
    fun filter() {
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
        assertEquals(listOf(30, 22, 60), actual)
    }

    @Test
    fun filterNot() {
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
        assertEquals(listOf(2, 5, 1), actual)
    }

    @Test
    fun filterNotNull() {
        val actual = mutableListOf<Int?>()
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
        assertEquals(listOf<Int?>(1, 2, 3, 4), actual)
    }

    @Test
    fun filterIsInstance() {
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
        assertEquals(listOf(5), actual)
    }

    @Test
    fun filterIsInstance_nullable() {
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
        assertEquals(listOf(null, 5), actual)
    }

    @Test
    fun distinct() {
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
        assertEquals(listOf(0, 2, 1), actual)
    }

    @Test
    fun distinctBy() {
        val actual = mutableListOf<Int>()
        val data = MutableLiveData<Int>()
        data.distinctBy { it % 2 }.observeForever { actual += it }
        data.run {
            value = 0
            value = 2
            value = 1
            value = 2
            value = 1
            value = 1
        }
        assertEquals(listOf(0, 1), actual)
    }

    @Test
    fun distinctBy_with_identityHashCode() {
        val actual = mutableListOf<String>()
        val data = MutableLiveData<String>()
        data.distinctBy { System.identityHashCode(it) }.observeForever { actual += it }
        val s1 = String()
        val s2 = String()
        assertEquals(s1, s2)
        assertNotSame(s1, s2)
        data.run {
            value = s1
            value = s2
            value = s1
            value = s2
            value = s2
        }
        assertEquals(listOf(s1, s2), actual)
    }

    @Test
    fun distinctUntilChangedBy() {
        val actual = mutableListOf<Int>()
        val data = MutableLiveData<Int>()
        data.distinctUntilChangedBy { it % 2 }.observeForever { actual += it }
        data.run {
            value = 0
            value = 2
            value = 1
            value = 2
            value = 1
            value = 1
        }
        assertEquals(listOf(0, 1, 2, 1), actual)
    }

    @Test
    fun distinctUntilChangedBy_with_identityHashCode() {
        val actual = mutableListOf<String>()
        val data = MutableLiveData<String>()
        data.distinctUntilChangedBy { System.identityHashCode(it) }.observeForever { actual += it }
        val s1 = String()
        val s2 = String()
        assertEquals(s1, s2)
        assertNotSame(s1, s2)
        data.run {
            value = s1
            value = s2
            value = s1
            value = s2
            value = s2
        }
        assertEquals(listOf(s1, s2, s1, s2), actual)
    }

    @Test
    fun drop() {
        val actual = mutableListOf<Int>()
        val data = MutableLiveData<Int>()
        data.drop(2).observeForever { actual += it }
        (1..4).forEach { data.value = it }
        assertEquals(listOf(3, 4), actual)
    }

    @Test
    fun dropWhile() {
        val actual = mutableListOf<Int>()
        val data = MutableLiveData<Int>()
        data.dropWhile { it < 4 }.observeForever { actual += it }
        (1..4).forEach { data.value = it }
        (3 downTo 1).forEach { data.value = it }
        assertEquals(listOf(4, 3, 2, 1), actual)
    }

    @Test
    fun take() {
        val actual = mutableListOf<Int>()
        val data = MutableLiveData<Int>()
        data.take(2).observeForever { actual += it }
        (1..4).forEach { data.value = it }
        assertEquals(listOf(1, 2), actual)
    }

    @Test
    fun takeWhile() {
        val actual = mutableListOf<Int>()
        val data = MutableLiveData<Int>()
        data.takeWhile { it < 4 }.observeForever { actual += it }
        (1..4).forEach { data.value = it }
        (3 downTo 1).forEach { data.value = it }
        assertEquals(listOf(1, 2, 3), actual)
    }

    @Test
    fun plus() {
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
        assertEquals(listOf<Any>(20, 40, 60, 1, 80, 100, 1), actual)
    }

    @Test
    fun combineLatest() {
        val actual = mutableListOf<Pair<Int, Char>>()
        val intData = MutableLiveData<Int>()
        val charData = MutableLiveData<Char>()
        intData.combineLatest(charData).observeForever { actual += it }
        intData.value = 1
        charData.value = 'a'
        intData.value = 2
        ('b'..'c').forEach { charData.value = it }
        (3..4).forEach { intData.value = it }
        assertEquals(listOf(1 to 'a', 2 to 'a', 2 to 'b', 2 to 'c', 3 to 'c', 4 to 'c'), actual)
    }

    @Test
    fun combineLatest_transform() {
        val actual = mutableListOf<String>()
        val intData = MutableLiveData<Int>()
        val charData = MutableLiveData<Char>()
        intData.combineLatest(charData) { a, b -> "$a$b" }.observeForever { actual += it }
        intData.value = 1
        charData.value = 'a'
        intData.value = 2
        ('b'..'c').forEach { charData.value = it }
        (3..4).forEach { intData.value = it }
        assertEquals(listOf("1a", "2a", "2b", "2c", "3c", "4c"), actual)
    }

    @Test
    fun withLatestFrom() {
        val actual = mutableListOf<Pair<Int, Char>>()
        val intData = MutableLiveData<Int>()
        val charData = MutableLiveData<Char>()
        intData.withLatestFrom(charData).observeForever { actual += it }
        intData.value = 1
        charData.value = 'a'
        intData.value = 2
        ('b'..'c').forEach { charData.value = it }
        (3..4).forEach { intData.value = it }
        assertEquals(listOf(2 to 'a', 3 to 'c', 4 to 'c'), actual)
    }

    @Test
    fun withLatestFrom_transform() {
        val actual = mutableListOf<String>()
        val intData = MutableLiveData<Int>()
        val charData = MutableLiveData<Char>()
        intData.withLatestFrom(charData) { a, b -> "$a$b" }.observeForever { actual += it }
        intData.value = 1
        charData.value = 'a'
        intData.value = 2
        ('b'..'c').forEach { charData.value = it }
        (3..4).forEach { intData.value = it }
        assertEquals(listOf("2a", "3c", "4c"), actual)
    }

    @Test
    fun zip() {
        val actual = mutableListOf<Pair<Int, Char>>()
        val intData = MutableLiveData<Int>()
        val charData = MutableLiveData<Char>()
        intData.zip(charData).observeForever { actual += it }
        intData.value = 1
        charData.value = 'a'
        intData.value = 2
        ('b'..'c').forEach { charData.value = it }
        (3..4).forEach { intData.value = it }
        assertEquals(listOf(1 to 'a', 2 to 'b', 3 to 'c'), actual)
    }

    @Test
    fun zip_transform() {
        val actual = mutableListOf<String>()
        val intData = MutableLiveData<Int>()
        val charData = MutableLiveData<Char>()
        intData.zip(charData) { a, b -> "$a$b" }.observeForever { actual += it }
        intData.value = 1
        charData.value = 'a'
        intData.value = 2
        ('b'..'c').forEach { charData.value = it }
        (3..4).forEach { intData.value = it }
        assertEquals(listOf("1a", "2b", "3c"), actual)
    }

    @Test
    fun zipWithNext() {
        val actual = mutableListOf<Pair<Any, Any>>()
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
        assertEquals(listOf(1 to 'a', 'a' to 2, 2 to 'b', 'b' to 'c', 'c' to 3, 3 to 4), actual)
    }

    @Test
    fun zipWithNext_transform() {
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
        assertEquals(listOf("1a", "a2", "2b", "bc", "c3", "34"), actual)
    }
}
