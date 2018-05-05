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
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import com.github.tmurakami.aackt.lifecycle.MediatorLiveData
import com.github.tmurakami.aackt.lifecycle.bindSource
import com.github.tmurakami.aackt.lifecycle.unbindSource
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class MediatorLiveDataTest {

    @[Rule JvmField]
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun instantiate() = assertEquals("test", MediatorLiveData("test").value)

    @Test
    fun bindSource() {
        val data = MediatorLiveData<Unit>().apply { test() }
        val results = ArrayList<Int>()
        val src = MutableLiveData<Int>().also { data.bindSource(it) { results += it } }
        src.value = 1
        assertSame(1, results.single())
    }

    @Test
    fun bindSource_Observer() {
        val data = MediatorLiveData<Unit>().apply { test() }
        val results = ArrayList<Int?>()
        val src = MutableLiveData<Int>().also { data.bindSource(it, Observer { results += it }) }
        src.value = 1
        assertSame(1, results.single())
    }

    @Test
    fun unbindSource() {
        val data = MediatorLiveData<Unit>().apply { test() }
        val results = ArrayList<Int>()
        val src = MutableLiveData<Int>().also { data.bindSource(it) { results += it } }
        val observer = src.test()
        src.value = 1
        data.unbindSource(src)
        src.value = 2
        assertSame(1, results.size)
        assertSame(1, results[0])
        observer.assertValues(1, 2)
    }
}
