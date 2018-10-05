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

package com.github.tmurakami.aackt.persistence.room

import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.RxRoom
import io.reactivex.Flowable

/**
 * Creates a [Flowable] which emits [RxRoom.NOTHING] whenever one of the given [tables] is modified.
 */
inline fun RoomDatabase.createFlowable(vararg tables: String): Flowable<Any> =
    RxRoom.createFlowable(this, *tables)
