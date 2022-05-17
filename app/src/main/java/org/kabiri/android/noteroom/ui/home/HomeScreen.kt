@file:OptIn(ExperimentalMaterial3Api::class)
@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package org.kabiri.android.noteroom.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.kabiri.android.noteroom.R
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

private enum class PopupState {
    Open, Close, Edit
}

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModelAbstract
) {
    val noteListState = homeViewModel.noteListFlow.collectAsState(initial = listOf())
    val txtState = rememberSaveable { mutableStateOf("") }
    val noteIdState: MutableState<Long?> = rememberSaveable { mutableStateOf(null) }
    val popupState = rememberSaveable { mutableStateOf(PopupState.Close) }

    Scaffold {
        LazyColumn {
            items(noteListState.value.size) { index ->
                val note = noteListState.value[index]
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        noteIdState.value = note.roomId
                        txtState.value = note.text
                        popupState.value = PopupState.Edit
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                // delete the note on long click
                                homeViewModel.deleteNote(note)
                            }
                        )
                    }
                    .height(54.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 16.dp, end = 16.dp),
                        text = note.text,
                        maxLines = 1,
                    )
                    Spacer(
                        modifier = Modifier
                            .height(0.5.dp)
                            .fillMaxWidth()
                            .background(color = Color.Gray.copy(alpha = 0.54f))
                            .align(Alignment.BottomCenter)
                    )
                }
            }
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        modifier = Modifier.align(Alignment.Center),
                        onClick = {
                            popupState.value = PopupState.Open
                        }) {
                        Text(text = stringResource(id = R.string.screen_home_button_add_note))
                    }
                }
            }
        }

        when (popupState.value) {
            PopupState.Open -> {
                NotePopup(
                    onClickDismiss = {
                        popupState.value = PopupState.Close
                    },
                    onClickSave = {
                        homeViewModel.addNote(note = NoteEntity(text = it))
                        popupState.value = PopupState.Close
                    }
                )
            }
            PopupState.Edit -> {
                NotePopup(
                    txtState = txtState,
                    onClickDismiss = {
                        popupState.value = PopupState.Close
                    },
                    onClickSave = {
                        homeViewModel.updateNote(
                            note = NoteEntity(
                                roomId = noteIdState.value,
                                text = it
                            )
                        )
                        popupState.value = PopupState.Close
                    }
                )
            }
            PopupState.Close -> {
            }
        }
    }
}

@Preview
@Composable
fun PreviewHomeScreen() {
    HomeScreen(
        homeViewModel = object : HomeViewModelAbstract {
            override val noteListFlow: Flow<List<NoteEntity>>
                get() = flowOf(
                    listOf(
                        NoteEntity(text = "note 1"),
                        NoteEntity(text = "note 2"),
                        NoteEntity(text = "note 3"),
                        NoteEntity(text = "note 4"),
                        NoteEntity(text = "note 5"),
                    )
                )

            override fun addNote(note: NoteEntity) {}
            override fun updateNote(note: NoteEntity) {}
            override fun deleteNote(note: NoteEntity) {}
        }
    )
}