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

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.LinkedList
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestObserver<T> : Observer<T> {
    private val received = LinkedList<T>()
    override fun onChanged(t: T) = received.plusAssign(t)
    fun assertNoValues() = assertTrue(received.isEmpty())
    fun assertValuesOnly(vararg values: T) = assertEquals(listOf(*values), received)
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T> LiveData<T>.test(): TestObserver<T> = TestObserver<T>().also { observeForever(it) }
