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

package com.github.tmurakami.aackt.room

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Creates a [RoomDatabase.Builder] for a persistent database of the given file [name].
 */
inline fun <reified T : RoomDatabase> Context.databaseBuilder(
    name: String
): RoomDatabase.Builder<T> = Room.databaseBuilder(this, T::class.java, name)

/**
 * Creates a [RoomDatabase.Builder] for an in-memory database.
 */
inline fun <reified T : RoomDatabase> Context.inMemoryDatabaseBuilder(): RoomDatabase.Builder<T> =
    Room.inMemoryDatabaseBuilder(this, T::class.java)

/**
 * Creates a [RoomDatabase] for a persistent database of the given file [name].
 */
inline fun <reified T : RoomDatabase> Context.createRoomDatabase(
    name: String,
    crossinline block: RoomDatabase.Builder<T>.() -> Unit = {}
): T = Room.databaseBuilder(this, T::class.java, name).apply { block() }.build()

/**
 * Creates a [RoomDatabase] for an in-memory database.
 */
inline fun <reified T : RoomDatabase> Context.createInMemoryRoomDatabase(
    crossinline block: RoomDatabase.Builder<T>.() -> Unit = {}
): T = Room.inMemoryDatabaseBuilder(this, T::class.java).apply { block() }.build()
