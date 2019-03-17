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

@file:Suppress("FunctionName")

package com.github.tmurakami.aackt.lifecycle

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.SavedStateVMFactory

/**
 * Creates a [ViewModelFactoryCreator] responsible for instantiation of [SavedStateVMFactory].
 */
fun SavedStateVMFactoryCreator(
    fragment: Fragment,
    defaultArgs: Bundle? = null
): ViewModelFactoryCreator = { SavedStateVMFactory(fragment, defaultArgs) }

/**
 * Creates a [ViewModelFactoryCreator] responsible for instantiation of [SavedStateVMFactory].
 */
fun SavedStateVMFactoryCreator(
    activity: FragmentActivity,
    defaultArgs: Bundle? = null
): ViewModelFactoryCreator = { SavedStateVMFactory(activity, defaultArgs) }
