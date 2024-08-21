package com.akari.levelcat.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.akari.levelcat.ui.editor.EditorScreen
import com.akari.levelcat.ui.home.HomeScreen

const val ARG_EDITOR_ID = "id"

enum class NavigationDestination(
    val title: String,
    val canNavigateUp: Boolean = false,
    val arguments: List<NamedNavArgument> = emptyList(),
    val screen: @Composable (modifier: Modifier) -> Unit,
) {
    Home(
        title = "Home",
        canNavigateUp = false,
        arguments = emptyList(),
        screen = { modifier -> HomeScreen(modifier) }
    ),
    Editor(
        title = "Editor",
        canNavigateUp = true,
        arguments = listOf(navArgument(ARG_EDITOR_ID) {
            type = NavType.LongType
            nullable = false
        }),
        screen = { modifier -> EditorScreen(modifier) }
    );

    val route = buildString {
        append(name)
        arguments.forEach { argument ->
            append("/{${argument.name}}")
        }
    }

    fun withArguments(vararg args: Any) = buildString {
        append(name)
        args.forEach { arg ->
            append("/$arg")
        }
    }
}

