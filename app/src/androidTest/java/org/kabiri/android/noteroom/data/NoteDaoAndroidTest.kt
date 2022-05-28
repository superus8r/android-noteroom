package org.kabiri.android.noteroom.data

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.kabiri.android.noteroom.model.NoteEntity

/**
 *
 * Copyright © 2022 Ali Kabiri
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the “Software”), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

class NoteDaoAndroidTest {

    private lateinit var sut: NoteDao
    private lateinit var mDb: AppDatabase

    @Before
    fun createDb() {
        mDb = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        sut = mDb.noteDao()
    }

    @After
    fun cleanUp() {
        mDb.close()
    }

    @Test
    fun testInsertNoteAndReadInList() = runBlocking {
        // arrange
        val fakeText = "some text"
        val fakeNote = NoteEntity(text = fakeText)

        // act
        sut.insert(fakeNote)
        val noteList = sut.getAllFlow().first()

        // assert
        assertThat(noteList.first().text).isEqualTo(fakeText)
    }

    @Test
    fun testUpdateNoteAndReadInList() = runBlocking {
        // arrange
        val fakeText = "some text"
        val fakeTextUpdated = "updated text"
        val fakeNote = NoteEntity(text = fakeText)

        // act
        sut.insert(fakeNote)
        val noteList = sut.getAllFlow().first()
        sut.update(noteList.first().copy(text = fakeTextUpdated))
        val noteListUpdated = sut.getAllFlow().first()

        // assert
        assertThat(noteListUpdated.first().text).isEqualTo(fakeTextUpdated)
    }

    @Test
    fun testDeleteNoteRemovesNoteFromList() = runBlocking {
        // arrange
        val fakeText = "some text"
        val fakeNote = NoteEntity(text = fakeText)

        // act
        sut.insert(fakeNote)
        val noteList = sut.getAllFlow().first()
        sut.delete(noteList.first())
        val noteListUpdated = sut.getAllFlow().first()

        // assert
        assertThat(noteListUpdated).isEmpty()
    }
}