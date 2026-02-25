package com.belpost.subscription.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.belpost.subscription.LocalAppContainer
import com.belpost.subscription.presentation.screens.detail.DetailScreen
import com.belpost.subscription.presentation.screens.main.MainScreen
import com.belpost.subscription.presentation.screens.success.SuccessScreen
import com.belpost.subscription.presentation.viewmodel.MainViewModel
import com.belpost.subscription.presentation.viewmodel.SubscriptionViewModel
import com.google.gson.Gson

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Detail : Screen("detail/{publicationJson}") {
        fun createRoute(publicationJson: String) = "detail/$publicationJson"
    }
    object Success : Screen("success/{subscriptionJson}") {
        fun createRoute(subscriptionJson: String) = "success/$subscriptionJson"
    }
}

@Composable
fun BelpostNavHost(
    navController: NavHostController = rememberNavController()
) {
    val appContainer = LocalAppContainer.current
    val gson = remember { Gson() }

    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            val mainViewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = appContainer.mainViewModelFactory
            )
            MainScreen(
                viewModel = mainViewModel,
                onOpenDetails = { publication ->
                    val json = gson.toJson(publication)
                    val encoded = java.net.URLEncoder.encode(json, Charsets.UTF_8.name())
                    navController.navigate(Screen.Detail.createRoute(encoded))
                }
            )
        }

        composable(Screen.Detail.route) { backStackEntry ->
            val jsonEncoded = backStackEntry.arguments?.getString("publicationJson") ?: return@composable
            val json = java.net.URLDecoder.decode(jsonEncoded, Charsets.UTF_8.name())
            val publication = gson.fromJson(json, com.belpost.subscription.data.api.models.PublicationDto::class.java)

            val subscriptionViewModel: SubscriptionViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = appContainer.subscriptionViewModelFactory
            )

            DetailScreen(
                publication = publication,
                viewModel = subscriptionViewModel,
                onBack = { navController.popBackStack() },
                onSubscriptionSuccess = { response ->
                    val responseJson = gson.toJson(response)
                    val encoded = java.net.URLEncoder.encode(responseJson, Charsets.UTF_8.name())
                    navController.navigate(Screen.Success.createRoute(encoded)) {
                        popUpTo(Screen.Main.route) { saveState = true }
                    }
                }
            )
        }

        composable(Screen.Success.route) { backStackEntry ->
            val jsonEncoded = backStackEntry.arguments?.getString("subscriptionJson") ?: return@composable
            val json = java.net.URLDecoder.decode(jsonEncoded, Charsets.UTF_8.name())
            val subscription = gson.fromJson(
                json,
                com.belpost.subscription.data.api.models.SubscriptionResponseDto::class.java
            )

            SuccessScreen(
                subscription = subscription,
                onGoHome = {
                    navController.popBackStack(Screen.Main.route, inclusive = false)
                }
            )
        }
    }
}

