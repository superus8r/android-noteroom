package org.kabiri.android.noteroom.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.kabiri.android.noteroom.data.repository.NoteRepository
import org.kabiri.android.noteroom.model.NoteEntity
import javax.inject.Inject

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

interface HomeViewModelAbstract {
    val selectedNoteState: State<NoteEntity?>
    val noteListFlow: Flow<List<NoteEntity>>
    fun addOrUpdateNote(note: NoteEntity)
    fun updateNote(note: NoteEntity)
    fun deleteNote(note: NoteEntity)
    fun selectNote(note: NoteEntity)
    fun resetSelectedNote()
}

@HiltViewModel
class HomeViewModel
@Inject constructor(
    private val noteRepository: NoteRepository,
): ViewModel(), HomeViewModelAbstract {
    private val ioScope = CoroutineScope(Dispatchers.IO)

    private val _selectedNoteState: MutableState<NoteEntity?> = mutableStateOf(null)
    override val selectedNoteState: State<NoteEntity?>
        get() = _selectedNoteState

    override val noteListFlow: Flow<List<NoteEntity>> = noteRepository.getAllFlow()

    override fun addOrUpdateNote(note: NoteEntity) {
        ioScope.launch {
            if (note.roomId == null) {
                noteRepository.insert(note = note)
            } else {
                noteRepository.update(note = note)
            }
        }
    }

    override fun updateNote(note: NoteEntity) {
        ioScope.launch {
            noteRepository.update(note = note)
        }
    }

    override fun deleteNote(note: NoteEntity) {
        ioScope.launch {
            noteRepository.delete(note = note)
        }
    }

    override fun selectNote(note: NoteEntity) {
        _selectedNoteState.value = note
    }

    override fun resetSelectedNote() {
        _selectedNoteState.value = null
    }

}