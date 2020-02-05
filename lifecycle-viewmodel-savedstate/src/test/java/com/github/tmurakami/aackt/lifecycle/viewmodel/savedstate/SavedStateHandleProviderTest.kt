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

package com.github.tmurakami.aackt.lifecycle.viewmodel.savedstate

import android.os.Bundle
import android.os.Parcelable
import com.github.tmurakami.aackt.lifecycle.SavedStateHandleProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class SavedStateHandleProviderTest {
    @Test
    fun testGet() {
        val provider = SavedStateHandleProvider(FakeSavedStateRegistryOwner())
        assertSame(provider["a"], provider["a"])
        assertNotSame(provider["a"], provider["b"])
    }

    @Test
    fun testDefaultArgs() {
        val defaultArgs = Bundle()
        defaultArgs.putInt("0", 0)
        val handle = SavedStateHandleProvider(FakeSavedStateRegistryOwner(), defaultArgs)["a"]
        assertEquals(0, handle["0"]!!)
    }

    @Test
    fun testPerformSave() {
        val owner = FakeSavedStateRegistryOwner()
        val handle = SavedStateHandleProvider(owner)["a"]
        handle["0"] = 0
        val outState = Bundle()
        owner.performSave(outState)
        val internalKey = "com.github.tmurakami.aackt.lifecycle.SavedStateHandle:a"
        val keyValues = outState.run { getBundle(keySet().single()) }!!.getBundle(internalKey)!!
        assertEquals<Any>("0", keyValues.getParcelableArrayList<Parcelable>("keys")!!.single())
        assertEquals<Any>(0, keyValues.getParcelableArrayList<Parcelable>("values")!!.single())
    }

    @Test
    fun testPerformRestore() {
        val owner = FakeSavedStateRegistryOwner()
        val handle = SavedStateHandleProvider(owner)["a"]
        handle["0"] = 0
        val outState = Bundle()
        owner.performSave(outState)
        val owner2 = FakeSavedStateRegistryOwner(outState)
        assertSame(0, SavedStateHandleProvider(owner2)["a"]["0"]!!)
    }
}
