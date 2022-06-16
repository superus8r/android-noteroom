package org.kabiri.android.noteroom.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.kabiri.android.noteroom.*
import org.kabiri.android.noteroom.model.NoteEntity
import org.kabiri.android.noteroom.viewmodel.HomeViewModelAbstract

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
class NoteRoomAppAndroidTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val fakeSelectedNoteState = mutableStateOf<NoteEntity?>(null)
    private val fakeNoteList = mutableListOf<NoteEntity>()
    private val mockViewModel = object: HomeViewModelAbstract {
        override val selectedNoteState: State<NoteEntity?> = fakeSelectedNoteState
        override val noteListFlow: Flow<List<NoteEntity>> = flowOf(fakeNoteList)
        override fun addOrUpdateNote(note: NoteEntity) {
            fakeNoteList.clear() // clear the list each time a note is added.
            fakeNoteList.add(note) }
        override fun deleteNote(note: NoteEntity) { fakeNoteList.remove(note) }
        override fun selectNote(note: NoteEntity) { fakeSelectedNoteState.value = note }
        override fun resetSelectedNote() { fakeSelectedNoteState.value = null }
    }

    @Before
    fun setUp() {
        fakeSelectedNoteState.value = null
        fakeNoteList.clear()
    }

    @Test
    fun testComposeNodesAppearsCorrectly() {

        composeTestRule.setContent {
            NoteRoomApp(homeViewModel = mockViewModel)
        }

        // assert nodes exist in home screen and open note screen
        composeTestRule.onNodeWithTag(HOME_BUTTON_NOTE_ADD).assertIsDisplayed().performClick()
        // assert nodes exist in note screen
        composeTestRule.onNodeWithTag(NOTE_TEXT_FIELD).assertExists()
        composeTestRule.onNodeWithTag(NOTE_BUTTON_DONE).assertIsDisplayed()
        composeTestRule.onNodeWithTag(NOTE_BUTTON_BACK).assertIsDisplayed()
    }

    @Test
    fun testAddingAndUpdatingANoteWorksCorrectly() {

        val fakeNote = NoteEntity(roomId = 123, text = "doesn't matter")

        composeTestRule.setContent {
            NoteRoomApp(homeViewModel = mockViewModel)
        }

        // click on add button
        composeTestRule.onNodeWithTag(HOME_BUTTON_NOTE_ADD).performClick()

        // type some text in note text field and click on done button
        composeTestRule.onNodeWithTag(NOTE_TEXT_FIELD).performClick()
            .performTextInput(fakeNote.text)
        composeTestRule.onNodeWithTag(NOTE_BUTTON_DONE).performClick()

        // back in home screen, click on the note we just added to edit it
        composeTestRule.onAllNodesWithText(fakeNote.text).assertCountEquals(1)
            .onFirst().performClick()

        // edit the note and click on done button
        composeTestRule.onNodeWithText(fakeNote.text).performTextInput("-updated")
        composeTestRule.onNodeWithTag(NOTE_BUTTON_DONE).performClick()

        // back in home screen, check if updated text is shown.
        composeTestRule.onNodeWithText(fakeNoteList.first().text).assertIsDisplayed()
    }

    @Test
    fun testSwipingANoteDeletesItCorrectly() {

        val fakeNote = NoteEntity(roomId = 123, text = "doesn't matter")

        composeTestRule.setContent {
            NoteRoomApp(homeViewModel = mockViewModel)
        }

        // add a note item
        composeTestRule.onNodeWithTag(HOME_BUTTON_NOTE_ADD).performClick()
        composeTestRule.onNodeWithTag(NOTE_TEXT_FIELD).performClick()
            .performTextInput(fakeNote.text)
        composeTestRule.onNodeWithTag(NOTE_BUTTON_DONE).performClick()

        // back in home screen, drag the note to delete it
        composeTestRule.onNodeWithText(fakeNote.text).performTouchInput { swipeLeft() }

        // assert note is deleted from the list using swipe
        assertThat(fakeNoteList.size).isEqualTo(0)
    }
}