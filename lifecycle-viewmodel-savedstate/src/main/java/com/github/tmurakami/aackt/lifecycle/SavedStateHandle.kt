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

@file:Suppress("NOTHING_TO_INLINE")

package com.github.tmurakami.aackt.lifecycle

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Creates a [ReadOnlyProperty] to access a [LiveData] associated with the property name.
 */
@MainThread
fun <T> SavedStateHandle.liveData(): ReadOnlyProperty<Any?, LiveData<T>> =
    object : ReadOnlyProperty<Any?, LiveData<T>> {
        override fun getValue(thisRef: Any?, property: KProperty<*>) = getLiveData<T>(property.name)
    }

/**
 * Returns a value associated with the [property] name.
 */
@MainThread
inline operator fun <reified T> SavedStateHandle.getValue(
    thisRef: Any?,
    property: KProperty<*>
): T {
    val name = property.name
    return if (contains(name)) get<Any?>(name) as T else throw NoSuchElementException(name)
}

/**
 * Associates the given value with the [property] name.
 */
@MainThread
inline operator fun <T> SavedStateHandle.setValue(thisRef: Any?, property: KProperty<*>, value: T) =
    set(property.name, value)
