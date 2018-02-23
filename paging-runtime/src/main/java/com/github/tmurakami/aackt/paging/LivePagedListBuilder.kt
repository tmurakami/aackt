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

package com.github.tmurakami.aackt.paging

import android.arch.paging.DataSource
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList

/**
 * Creates a [LivePagedListBuilder] with the given [config].
 */
inline fun <K, V> DataSource.Factory<K, V>.livePagedListBuilder(
    config: PagedList.Config
): LivePagedListBuilder<K, V> = LivePagedListBuilder(this, config)

/**
 * Creates a [LivePagedListBuilder] with the given [pageSize].
 */
inline fun <K, V> DataSource.Factory<K, V>.livePagedListBuilder(
    pageSize: Int
): LivePagedListBuilder<K, V> = LivePagedListBuilder(this, pageSize)
