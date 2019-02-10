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

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Adds a [RoomDatabase.Callback] using the given [onCreate] and [onOpen] actions.
 */
inline fun <T : RoomDatabase> RoomDatabase.Builder<T>.addCallback(
    crossinline onCreate: SupportSQLiteDatabase.() -> Unit = {},
    crossinline onOpen: SupportSQLiteDatabase.() -> Unit = {}
): RoomDatabase.Builder<T> = addCallback(object : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        db.onCreate()
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        db.onOpen()
    }
})

/**
 * Adds a [action] which will be called when the database is created.
 */
inline fun <T : RoomDatabase> RoomDatabase.Builder<T>.doOnCreate(
    crossinline action: SupportSQLiteDatabase.() -> Unit
): RoomDatabase.Builder<T> = addCallback(onCreate = action)

/**
 * Adds a [action] which will be called when the database has been opened.
 */
inline fun <T : RoomDatabase> RoomDatabase.Builder<T>.doOnOpen(
    crossinline action: SupportSQLiteDatabase.() -> Unit
): RoomDatabase.Builder<T> = addCallback(onOpen = action)
