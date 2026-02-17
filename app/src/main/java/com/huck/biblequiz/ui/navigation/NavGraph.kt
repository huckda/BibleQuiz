package com.huck.biblequiz.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.huck.biblequiz.ui.screens.bookselection.BookSelectionScreen
import com.huck.biblequiz.ui.screens.chapterselection.ChapterSelectionScreen
import com.huck.biblequiz.ui.screens.modeselection.ModeSelectionScreen
import com.huck.biblequiz.ui.screens.quiz.QuizScreen
import com.huck.biblequiz.ui.screens.results.ResultsScreen
import com.huck.biblequiz.ui.screens.study.StudyScreen

object Routes {
    const val BOOK_SELECTION = "book_selection"
    const val CHAPTER_SELECTION = "chapter_selection/{bookIds}"
    const val MODE_SELECTION = "mode_selection/{selections}"
    const val STUDY = "study/{selections}"
    const val QUIZ = "quiz/{selections}"
    const val RESULTS = "results/{score}/{total}/{selections}"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.BOOK_SELECTION) {

        composable(Routes.BOOK_SELECTION) {
            BookSelectionScreen(
                onBooksSelected = { bookIds ->
                    val encoded = bookIds.joinToString(",")
                    navController.navigate("chapter_selection/$encoded")
                }
            )
        }

        composable(
            Routes.CHAPTER_SELECTION,
            arguments = listOf(navArgument("bookIds") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookIds = backStackEntry.arguments?.getString("bookIds") ?: ""
            ChapterSelectionScreen(
                bookIds = bookIds,
                onChaptersSelected = { selections ->
                    navController.navigate("mode_selection/$selections")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            Routes.MODE_SELECTION,
            arguments = listOf(navArgument("selections") { type = NavType.StringType })
        ) { backStackEntry ->
            val selections = backStackEntry.arguments?.getString("selections") ?: ""
            ModeSelectionScreen(
                selections = selections,
                onStudy = { navController.navigate("study/$selections") },
                onQuiz = { navController.navigate("quiz/$selections") },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            Routes.STUDY,
            arguments = listOf(navArgument("selections") { type = NavType.StringType })
        ) { backStackEntry ->
            val selections = backStackEntry.arguments?.getString("selections") ?: ""
            StudyScreen(
                selections = selections,
                onBack = { navController.popBackStack() },
                onStartQuiz = {
                    navController.popBackStack()
                    navController.navigate("quiz/$selections")
                }
            )
        }

        composable(
            Routes.QUIZ,
            arguments = listOf(navArgument("selections") { type = NavType.StringType })
        ) { backStackEntry ->
            val selections = backStackEntry.arguments?.getString("selections") ?: ""
            QuizScreen(
                selections = selections,
                onFinished = { score, total ->
                    navController.navigate("results/$score/$total/$selections") {
                        popUpTo("mode_selection/$selections") { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            Routes.RESULTS,
            arguments = listOf(
                navArgument("score") { type = NavType.IntType },
                navArgument("total") { type = NavType.IntType },
                navArgument("selections") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            val total = backStackEntry.arguments?.getInt("total") ?: 0
            val selections = backStackEntry.arguments?.getString("selections") ?: ""
            ResultsScreen(
                score = score,
                total = total,
                selections = selections,
                onRetry = {
                    navController.navigate("quiz/$selections") {
                        popUpTo("results/$score/$total/$selections") { inclusive = true }
                    }
                },
                onStudy = {
                    navController.navigate("study/$selections") {
                        popUpTo("results/$score/$total/$selections") { inclusive = true }
                    }
                },
                onHome = {
                    navController.navigate(Routes.BOOK_SELECTION) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
