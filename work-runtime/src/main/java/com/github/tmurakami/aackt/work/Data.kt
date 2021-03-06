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

package com.github.tmurakami.aackt.work

import androidx.work.Data
import androidx.work.values
import kotlin.reflect.KProperty

/**
 * Returns a value associated with the [property] name.
 */
operator fun <T> Data.getValue(thisRef: Any?, property: KProperty<*>): T {
    val name = property.name
    val values = values
    @Suppress("UNCHECKED_CAST")
    return if (values.containsKey(name)) values[name] as T else throw NoSuchElementException(name)
}
