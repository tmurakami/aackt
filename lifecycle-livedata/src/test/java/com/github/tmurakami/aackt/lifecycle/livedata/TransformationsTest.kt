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
        val data = MutableLiveData<Int>()
        val observer = data.map { it * it }.test()
        repeat(5) { data.value = it }
        observer.assertValuesOnly(0, 1, 4, 9, 16)
    }

    @Test
    fun mapNotNull() {
        val data = MutableLiveData<Int>()
        val observer = data.mapNotNull { if (it % 2 == 0) null else it }.test()
        repeat(5) { data.value = it }
        observer.assertValuesOnly(1, 3)
    }

    @Test
    fun switchMap() {
        val data = MutableLiveData<Int>()
        val observer = data.switchMap { MutableLiveData(it * it) }.test()
        repeat(5) { data.value = it }
        observer.assertValuesOnly(0, 1, 4, 9, 16)
    }

    @Test
    fun doOnActive() {
        var active = false
        val data = MutableLiveData<Unit>().doOnActive { active = true }
        assertFalse(active)
        data.test()
        assertTrue(active)
    }

    @Test
    fun doOnActive_Runnable() {
        var active = false
        val data = MutableLiveData<Unit>().doOnActive(Runnable { active = true })
        assertFalse(active)
        data.test()
        assertTrue(active)
    }

    @Test
    fun doOnInactive() {
        var inactive = false
        val data = MutableLiveData<Unit>().doOnInactive { inactive = true }
        val observer = data.test()
        assertFalse(inactive)
        data.removeObserver(observer)
        assertTrue(inactive)
    }

    @Test
    fun doOnInactive_Runnable() {
        var inactive = false
        val data = MutableLiveData<Unit>().doOnInactive(Runnable { inactive = true })
        val observer = data.test()
        assertFalse(inactive)
        data.removeObserver(observer)
        assertTrue(inactive)
    }

    @Test
    fun doOnChanged() {
        val data = MutableLiveData<Unit>()
        var changed = false
        data.doOnChanged { changed = true }.test()
        assertFalse(changed)
        data.value = Unit
        assertTrue(changed)
    }

    @Test
    fun filter() {
        val data = MutableLiveData<Int>()
        val observer = data.filter { it > 10 }.test()
        data.run {
            value = 2
            value = 30
            value = 22
            value = 5
            value = 60
            value = 1
        }
        observer.assertValuesOnly(30, 22, 60)
    }

    @Test
    fun filterNot() {
        val data = MutableLiveData<Int>()
        val observer = data.filterNot { it > 10 }.test()
        data.run {
            value = 2
            value = 30
            value = 22
            value = 5
            value = 60
            value = 1
        }
        observer.assertValuesOnly(2, 5, 1)
    }

    @Test
    fun filterNotNull() {
        val data = MutableLiveData<Int?>()
        val observer = data.filterNotNull().test()
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
        observer.assertValuesOnly(1, 2, 3, 4)
    }

    @Test
    fun filterIsInstance() {
        val data = MutableLiveData<Any?>()
        val observer = data.filterIsInstance<Int>().test()
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
        observer.assertValuesOnly(5)
    }

    @Test
    fun filterIsInstance_nullable() {
        val data = MutableLiveData<Any?>()
        val observer = data.filterIsInstance<Int?>().test()
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
        observer.assertValuesOnly(null, 5)
    }

    @Test
    fun distinct() {
        val data = MutableLiveData<Int>()
        val observer = data.distinct().test()
        data.run {
            value = 0
            value = 2
            value = 1
            value = 2
            value = 1
            value = 1
        }
        observer.assertValuesOnly(0, 2, 1)
    }

    @Test
    fun distinctBy() {
        val data = MutableLiveData<Int>()
        val observer = data.distinctBy { it % 2 }.test()
        data.run {
            value = 0
            value = 2
            value = 1
            value = 2
            value = 1
            value = 1
        }
        observer.assertValuesOnly(0, 1)
    }

    @Test
    fun distinctBy_with_identityHashCode() {
        val data = MutableLiveData<String>()
        val observer = data.distinctBy { System.identityHashCode(it) }.test()
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
        observer.assertValuesOnly(s1, s2)
    }

    @Test
    fun distinctUntilChangedBy() {
        val data = MutableLiveData<Int>()
        val observer = data.distinctUntilChangedBy { it % 2 }.test()
        data.run {
            value = 0
            value = 2
            value = 1
            value = 2
            value = 1
            value = 1
        }
        observer.assertValuesOnly(0, 1, 2, 1)
    }

    @Test
    fun distinctUntilChangedBy_with_identityHashCode() {
        val data = MutableLiveData<String>()
        val observer = data.distinctUntilChangedBy { System.identityHashCode(it) }.test()
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
        observer.assertValuesOnly(s1, s2, s1, s2)
    }

    @Test
    fun drop() {
        val data = MutableLiveData<Int>()
        val observer = data.drop(3).test()
        repeat(5) { data.value = it }
        observer.assertValuesOnly(3, 4)
    }

    @Test
    fun dropWhile() {
        val data = MutableLiveData<Int>()
        val observer = data.dropWhile { it < 4 }.test()
        repeat(5) { data.value = it }
        (3 downTo 0).forEach { data.value = it }
        observer.assertValuesOnly(4, 3, 2, 1, 0)
    }

    @Test
    fun take() {
        val data = MutableLiveData<Int>()
        val observer = data.take(3).test()
        repeat(5) { data.value = it }
        observer.assertValuesOnly(0, 1, 2)
    }

    @Test
    fun takeWhile() {
        val data = MutableLiveData<Int>()
        val observer = data.takeWhile { it < 4 }.test()
        repeat(5) { data.value = it }
        (3 downTo 0).forEach { data.value = it }
        observer.assertValuesOnly(0, 1, 2, 3)
    }

    @Test
    fun plus() {
        val anyData = MutableLiveData<Any>()
        val intData = MutableLiveData<Int>()
        val observer = (anyData + intData).test()
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
        observer.assertValuesOnly(20, 40, 60, 1, 80, 100, 1)
    }

    @Test
    fun combineLatest() {
        val intData = MutableLiveData<Int>()
        val charData = MutableLiveData<Char>()
        val observer = intData.combineLatest(charData).test()
        intData.value = 1
        charData.value = 'a'
        intData.value = 2
        ('b'..'c').forEach { charData.value = it }
        (3..4).forEach { intData.value = it }
        observer.assertValuesOnly(1 to 'a', 2 to 'a', 2 to 'b', 2 to 'c', 3 to 'c', 4 to 'c')
    }

    @Test
    fun combineLatest_transform() {
        val intData = MutableLiveData<Int>()
        val charData = MutableLiveData<Char>()
        val observer = intData.combineLatest(charData) { a, b -> "$a$b" }.test()
        intData.value = 1
        charData.value = 'a'
        intData.value = 2
        ('b'..'c').forEach { charData.value = it }
        (3..4).forEach { intData.value = it }
        observer.assertValuesOnly("1a", "2a", "2b", "2c", "3c", "4c")
    }

    @Test
    fun withLatestFrom() {
        val intData = MutableLiveData<Int>()
        val charData = MutableLiveData<Char>()
        val observer = intData.withLatestFrom(charData).test()
        intData.value = 1
        charData.value = 'a'
        intData.value = 2
        ('b'..'c').forEach { charData.value = it }
        (3..4).forEach { intData.value = it }
        observer.assertValuesOnly(2 to 'a', 3 to 'c', 4 to 'c')
    }

    @Test
    fun withLatestFrom_transform() {
        val intData = MutableLiveData<Int>()
        val charData = MutableLiveData<Char>()
        val observer = intData.withLatestFrom(charData) { a, b -> "$a$b" }.test()
        intData.value = 1
        charData.value = 'a'
        intData.value = 2
        ('b'..'c').forEach { charData.value = it }
        (3..4).forEach { intData.value = it }
        observer.assertValuesOnly("2a", "3c", "4c")
    }

    @Test
    fun zip() {
        val intData = MutableLiveData<Int>()
        val charData = MutableLiveData<Char>()
        val observer = intData.zip(charData).test()
        intData.value = 1
        charData.value = 'a'
        intData.value = 2
        ('b'..'c').forEach { charData.value = it }
        (3..4).forEach { intData.value = it }
        observer.assertValuesOnly(1 to 'a', 2 to 'b', 3 to 'c')
    }

    @Test
    fun zip_transform() {
        val intData = MutableLiveData<Int>()
        val charData = MutableLiveData<Char>()
        val observer = intData.zip(charData) { a, b -> "$a$b" }.test()
        intData.value = 1
        charData.value = 'a'
        intData.value = 2
        ('b'..'c').forEach { charData.value = it }
        (3..4).forEach { intData.value = it }
        observer.assertValuesOnly("1a", "2b", "3c")
    }

    @Test
    fun zipWithNext() {
        val data = MutableLiveData<Any>()
        val observer = data.zipWithNext().test()
        data.run {
            value = 1
            value = 'a'
            value = 2
            value = 'b'
            value = 'c'
            value = 3
            value = 4
        }
        observer.assertValuesOnly(1 to 'a', 'a' to 2, 2 to 'b', 'b' to 'c', 'c' to 3, 3 to 4)
    }

    @Test
    fun zipWithNext_transform() {
        val data = MutableLiveData<Any>()
        val observer = data.zipWithNext { a, b -> "$a$b" }.test()
        data.run {
            value = 1
            value = 'a'
            value = 2
            value = 'b'
            value = 'c'
            value = 3
            value = 4
        }
        observer.assertValuesOnly("1a", "a2", "2b", "bc", "c3", "34")
    }
}
