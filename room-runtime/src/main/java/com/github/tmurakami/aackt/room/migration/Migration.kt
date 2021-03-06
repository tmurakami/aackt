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

package com.github.tmurakami.aackt.room.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Creates a [Migration] between [startVersion] and [endVersion].
 */
@Suppress("FunctionName")
inline fun Migration(
    startVersion: Int,
    endVersion: Int,
    crossinline migrate: SupportSQLiteDatabase.() -> Unit
): Migration = object : Migration(startVersion, endVersion) {
    override fun migrate(database: SupportSQLiteDatabase) = database.migrate()
}
