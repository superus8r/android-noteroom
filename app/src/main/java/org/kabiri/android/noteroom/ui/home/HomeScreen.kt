@file:OptIn(ExperimentalMaterial3Api::class)
@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package org.kabiri.android.noteroom.ui.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notes
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.kabiri.android.noteroom.HOME_BUTTON_NOTE_ADD
import org.kabiri.android.noteroom.HOME_ITEM_NOTE
import org.kabiri.android.noteroom.HOME_TITLE
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

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModelAbstract,
    onClickNote: (NoteEntity) -> Unit,
    onClickAddNote: () -> Unit,
) {
    val noteListState = homeViewModel.noteListFlow.collectAsState(initial = listOf())
    val txtState = rememberSaveable { mutableStateOf("") }
    val noteIdState: MutableState<Long?> = rememberSaveable { mutableStateOf(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(modifier = Modifier.testTag(HOME_TITLE),
                        text = stringResource(id = R.string.app_name))
                },
                navigationIcon = {
                    Icon(
                        modifier = Modifier.padding(start = 8.dp),
                        imageVector = Icons.Rounded.Notes,
                        contentDescription = null
                    )
                }
            )
        }
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(contentPadding)
                .animateContentSize(),
        ) {
            items(
                items = noteListState.value,
                key = { it.roomId ?: "" }
            ) { note ->
                val dismissState = rememberDismissState(
                    confirmStateChange = {
                        if (it == DismissValue.DismissedToStart ||
                            it == DismissValue.DismissedToEnd) {
                            // delete the item from database
                            homeViewModel.deleteNote(note)

                            return@rememberDismissState true
                        }
                        return@rememberDismissState false
                    }
                )
                NoteListItem(
                    modifier = Modifier
                        .testTag(HOME_ITEM_NOTE)
                        .animateItemPlacement(),
                    onClick = {
                        noteIdState.value = note.roomId
                        txtState.value = note.text
                        homeViewModel.selectNote(note)
                        onClickNote(note)
                    },
                    onDelete = { // delete the note on long click
                        homeViewModel.deleteNote(note)
                    },
                    note = note,
                    dismissState = dismissState
                )
            }
            item(key = "add_button") {
                Box(modifier = Modifier
                    .animateItemPlacement()
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                ) {
                    Button(
                        modifier = Modifier
                            .testTag(HOME_BUTTON_NOTE_ADD)
                            .align(Alignment.Center),
                        onClick = {
                            homeViewModel.resetSelectedNote()
                            onClickAddNote()
                        }) {
                        Text(text = stringResource(id = R.string.screen_home_button_add_note))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewHomeScreen() {
    HomeScreen(
        homeViewModel = object : HomeViewModelAbstract {
            override val selectedNoteState: State<NoteEntity?>
                get() = mutableStateOf(null)
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

            override fun addOrUpdateNote(note: NoteEntity) {}
            override fun deleteNote(note: NoteEntity) {}
            override fun selectNote(note: NoteEntity) {}
            override fun resetSelectedNote() {}
        },
        onClickNote = {},
        onClickAddNote = {},
    )
}