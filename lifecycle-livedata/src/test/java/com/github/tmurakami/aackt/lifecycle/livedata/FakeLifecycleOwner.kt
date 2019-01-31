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

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State.DESTROYED
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import java.io.Closeable

class FakeLifecycleOwner : LifecycleOwner, Closeable {
    private val registry = LifecycleRegistry(this)

    override fun getLifecycle(): Lifecycle = registry

    fun resume(): FakeLifecycleOwner = registry.run {
        check(currentState > DESTROYED) { "Already destroyed" }
        currentState = RESUMED
        this@FakeLifecycleOwner
    }

    override fun close() = registry.run { currentState = DESTROYED }
}