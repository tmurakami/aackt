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

package com.github.tmurakami.aackt.work.runtime

import androidx.work.workDataOf
import com.github.tmurakami.aackt.work.getValue
import kotlin.test.Test
import kotlin.test.assertSame

class DataTest {
    @Test
    fun testGetValue() {
        val value: Int by workDataOf("value" to 0)
        assertSame(0, value)
    }

    @Test(NoSuchElementException::class)
    fun testGetValue_noSuchElement() {
        val value: Int by workDataOf()
        value.toString()
    }
}
