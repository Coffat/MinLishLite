package com.example.minlishlite.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.minlishlite.presentation.deck.AddEditDeckScreen
import com.example.minlishlite.presentation.deck.DeckDetailScreen
import com.example.minlishlite.presentation.deck.DeckListScreen
import com.example.minlishlite.presentation.home.HomeScreen
import com.example.minlishlite.presentation.onboarding.OnboardingScreen
import com.example.minlishlite.presentation.progress.ProgressScreen
import com.example.minlishlite.presentation.review.ReviewTodayScreen
import com.example.minlishlite.presentation.settings.SettingsScreen
import com.example.minlishlite.presentation.study.StudyMode
import com.example.minlishlite.presentation.study.StudyScreen
import com.example.minlishlite.presentation.word.AddEditWordScreen
import com.example.minlishlite.presentation.word.WordDetailScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.ONBOARDING,
        modifier = modifier
    ) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToStudyDueToday = {
                    navController.navigate(Routes.STUDY_DUE_TODAY)
                },
                onNavigateToReviewToday = {
                    navController.navigate(Routes.REVIEW_TODAY)
                },
                onNavigateToDecks = {
                    navController.navigate(Routes.DECKS)
                },
                onNavigateToDeckDetail = { deckId ->
                    navController.navigate("deck_detail/$deckId")
                }
            )
        }

        composable(Routes.REVIEW_TODAY) {
            ReviewTodayScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToStudyDueToday = {
                    navController.navigate(Routes.STUDY_DUE_TODAY)
                }
            )
        }

        composable(Routes.STUDY_DUE_TODAY) {
            StudyScreen(
                studyMode = StudyMode.DueToday,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.DECKS) {
            DeckListScreen(
                onNavigateToAddDeck = {
                    navController.navigate(Routes.ADD_DECK)
                },
                onNavigateToDeckDetail = { deckId ->
                    navController.navigate("deck_detail/$deckId")
                }
            )
        }

        composable(Routes.ADD_DECK) {
            AddEditDeckScreen(
                deckId = null,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.EDIT_DECK,
            arguments = listOf(navArgument("deckId") { type = NavType.IntType })
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getInt("deckId")
            AddEditDeckScreen(
                deckId = deckId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.DECK_DETAIL,
            arguments = listOf(navArgument("deckId") { type = NavType.IntType })
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getInt("deckId") ?: 0
            DeckDetailScreen(
                deckId = deckId,
                onNavigateToEditDeck = { id ->
                    navController.navigate("edit_deck/$id")
                },
                onNavigateToAddWord = { id ->
                    navController.navigate("add_word/$id")
                },
                onNavigateToStudy = { id ->
                    navController.navigate("study/$id")
                },
                onNavigateToWordDetail = { wordId ->
                    navController.navigate("word_detail/$wordId")
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.ADD_WORD,
            arguments = listOf(navArgument("deckId") { type = NavType.IntType })
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getInt("deckId")
            AddEditWordScreen(
                deckId = deckId,
                wordId = null,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.EDIT_WORD,
            arguments = listOf(navArgument("wordId") { type = NavType.IntType })
        ) { backStackEntry ->
            val wordId = backStackEntry.arguments?.getInt("wordId")
            AddEditWordScreen(
                deckId = null,
                wordId = wordId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.WORD_DETAIL,
            arguments = listOf(navArgument("wordId") { type = NavType.IntType })
        ) { backStackEntry ->
            val wordId = backStackEntry.arguments?.getInt("wordId") ?: 0
            WordDetailScreen(
                wordId = wordId,
                onNavigateToEditWord = { id ->
                    navController.navigate("edit_word/$id")
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.STUDY,
            arguments = listOf(navArgument("deckId") { type = NavType.IntType })
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getInt("deckId") ?: 0
            StudyScreen(
                studyMode = StudyMode.DeckDue(deckId),
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.PROGRESS) {
            ProgressScreen()
        }

        composable(Routes.SETTINGS) {
            SettingsScreen()
        }
    }
}
