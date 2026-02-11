package com.example.cityeventproject.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cityeventproject.ui.auth.AuthScreen
import com.example.cityeventproject.ui.auth.AuthViewModel
import com.example.cityeventproject.ui.comments.CommentsScreen
import com.example.cityeventproject.ui.details.DetailsScreen
import com.example.cityeventproject.ui.feed.FeedScreen
import com.example.cityeventproject.ui.feed.FeedViewModel
import com.example.cityeventproject.ui.notes.NoteEditorScreen
import com.example.cityeventproject.ui.profile.ProfileScreen
import com.example.cityeventproject.ui.search.SearchScreen

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val authVm: AuthViewModel = hiltViewModel()

    LaunchedEffect(authVm.isSignedIn) {
        if (authVm.isSignedIn) navController.navigate(NavRoutes.FEED) {
            popUpTo(NavRoutes.AUTH) { inclusive = true }
        } else navController.navigate(NavRoutes.AUTH) {
            popUpTo(0)
        }
    }

    AppNavHost(navController)
}

@Composable
private fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = NavRoutes.AUTH) {
        composable(NavRoutes.AUTH) {
            AuthScreen(
                onSuccess = { navController.navigate(NavRoutes.FEED) { popUpTo(NavRoutes.AUTH) { inclusive = true } } }
            )
        }
        composable(NavRoutes.FEED) {
            val feedEntry = remember(navController) { navController.getBackStackEntry(NavRoutes.FEED) }
            val feedVm: FeedViewModel = hiltViewModel(feedEntry)
            FeedScreen(
                onOpenSearch = { navController.navigate(NavRoutes.SEARCH) },
                onOpenDetails = { navController.navigate(NavRoutes.details(it)) },
                onOpenProfile = { navController.navigate(NavRoutes.PROFILE) },
                vm = feedVm
            )
        }
        composable(NavRoutes.SEARCH) {
            val feedEntry = remember(navController) { navController.getBackStackEntry(NavRoutes.FEED) }
            val feedVm: FeedViewModel = hiltViewModel(feedEntry)
            SearchScreen(
                onBack = { navController.popBackStack() },
                feedVm = feedVm
            )
        }
        composable(NavRoutes.PROFILE) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onOpenDetails = { navController.navigate(NavRoutes.details(it)) },
                onOpenNoteEditor = { noteId -> navController.navigate(NavRoutes.noteEditor(noteId)) }
            )
        }
        composable(NavRoutes.DETAILS) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId").orEmpty()
            DetailsScreen(
                eventId = eventId,
                onBack = { navController.popBackStack() },
                onOpenComments = { navController.navigate(NavRoutes.comments(eventId)) }
            )
        }
        composable(NavRoutes.COMMENTS) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId").orEmpty()
            CommentsScreen(
                eventId = eventId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = NavRoutes.NOTE_EDITOR,
            arguments = listOf(navArgument("noteId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")
            NoteEditorScreen(
                noteId = noteId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
