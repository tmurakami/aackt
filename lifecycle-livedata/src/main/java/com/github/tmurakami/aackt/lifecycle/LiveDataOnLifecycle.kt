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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import java.util.LinkedList

internal class LiveDataOnLifecycle<T>(source: LiveData<T>) : MediatorLiveData<T>(), Observer<T> {
    @JvmField // Not to increase methods count
    val onActiveListeners = LinkedList<Runnable>()
    @JvmField // Not to increase methods count
    val onInactiveListeners = LinkedList<Runnable>()

    init {
        addSource(source, this)
    }

    override fun onActive() {
        super.onActive()
        for (listener in onActiveListeners) listener.run()
    }

    override fun onChanged(t: T) {
        value = t
    }

    override fun onInactive() {
        super.onInactive()
        for (listener in onInactiveListeners) listener.run()
    }
}
