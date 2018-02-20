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

package com.github.tmurakami.aackt.persistence.room.runtime

import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

/**
 * Creates a [RoomDatabase].Builder for a persistent database of the given file [name].
 */
inline fun <reified DB : RoomDatabase> Context.databaseBuilder(
    name: String
): RoomDatabase.Builder<DB> = Room.databaseBuilder(this, DB::class.java, name)

/**
 * Creates a [RoomDatabase].Builder for an in-memory database.
 */
inline fun <reified DB : RoomDatabase> Context.inMemoryDatabaseBuilder(): RoomDatabase.Builder<DB> =
    Room.inMemoryDatabaseBuilder(this, DB::class.java)
