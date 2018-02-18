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

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.support.annotation.MainThread
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 *  Creates a property delegate for a read property that gets a [ViewModel] with the property name.
 */
@MainThread
inline fun <reified T : ViewModel> viewModel(
    crossinline provider: () -> ViewModelProvider
): ReadOnlyProperty<Any?, T> = object : ReadOnlyProperty<Any?, T> {
    private var viewModel: T? = null
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (viewModel == null) {
            viewModel = provider().get(property.name, T::class.java)
        }
        return viewModel!!
    }
}
