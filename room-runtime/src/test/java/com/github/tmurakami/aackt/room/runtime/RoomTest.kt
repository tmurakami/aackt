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

package com.github.tmurakami.aackt.room.runtime

import android.app.Application
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase
import com.github.tmurakami.aackt.room.createInMemoryRoomDatabase
import com.github.tmurakami.aackt.room.createRoomDatabase
import java.util.concurrent.Executor
import kotlin.test.Test
import kotlin.test.assertEquals

class RoomTest {
    @Test
    fun testCreateRoomDatabase() {
        val executor = Executor {}
        val db = Application().createRoomDatabase<TestDatabase>("test") {
            allowMainThreadQueries()
            setQueryExecutor(executor)
        }
        db.assertNotMainThread() // Hidden API
        assertEquals(executor, db.queryExecutor)
    }

    @Test
    fun testCreateInMemoryRoomDatabase() {
        val executor = Executor {}
        val db = Application().createInMemoryRoomDatabase<TestDatabase> {
            allowMainThreadQueries()
            setQueryExecutor(executor)
        }
        db.assertNotMainThread() // Hidden API
        assertEquals(executor, db.queryExecutor)
    }

    @Entity
    class TestEntity(@PrimaryKey val id: Int)

    @Database(version = 1, exportSchema = false, entities = [TestEntity::class])
    abstract class TestDatabase : RoomDatabase()
}
