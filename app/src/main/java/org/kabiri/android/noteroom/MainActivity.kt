package org.kabiri.android.noteroom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import org.kabiri.android.noteroom.ui.home.HomeScreen
import org.kabiri.android.noteroom.ui.note.NoteScreen
import org.kabiri.android.noteroom.ui.theme.NoteRoomTheme
import org.kabiri.android.noteroom.viewmodel.HomeViewModel

enum class Screen {
    Home, Note
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val homeViewModel: HomeViewModel by viewModels()

        setContent {
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
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NoteRoomTheme {
        Greeting("Android")
    }
}