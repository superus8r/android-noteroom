package org.kabiri.android.noteroom.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.kabiri.android.noteroom.ui.home.HomeScreen
import org.kabiri.android.noteroom.ui.note.NoteScreen
import org.kabiri.android.noteroom.ui.theme.NoteRoomTheme
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

enum class Screen {
    Home, Note
}

@Composable
fun NoteRoomApp(
    homeViewModel: HomeViewModelAbstract,
) {
    val navController = rememberNavController()
    NoteRoomTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.name,
                builder = {
                    composable(Screen.Home.name) {
                        HomeScreen(
                            homeViewModel = homeViewModel,
                            onClickNote = {
                                navController.navigate(Screen.Note.name)
                            },
                            onClickAddNote = {
                                navController.navigate(Screen.Note.name)
                            }
                        )
                    }
                    composable(Screen.Note.name) {
                        NoteScreen(
                            viewModel = homeViewModel,
                            onClickClose = {
                                navController.popBackStack()
                            },
                        )
                    }
                })
        }
    }
}