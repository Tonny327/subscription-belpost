package com.belpost.subscription.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.belpost.subscription.LocalAppContainer
import com.belpost.subscription.presentation.screens.cart.CartScreen
import com.belpost.subscription.presentation.screens.detail.DetailScreen
import com.belpost.subscription.presentation.screens.main.MainScreen
import com.belpost.subscription.presentation.screens.profile.ProfileScreen
import com.belpost.subscription.presentation.screens.success.SuccessScreen
import com.belpost.subscription.presentation.viewmodel.CartViewModel
import com.belpost.subscription.presentation.viewmodel.MainViewModel
import com.belpost.subscription.presentation.viewmodel.ProfileViewModel
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
    object Cart : Screen("cart")
    object Profile : Screen("profile")
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
        composable(Screen.Main.route) { backStackEntry ->
            val mainViewModel: MainViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = appContainer.mainViewModelFactory
            )
            val cartViewModel: CartViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = appContainer.cartViewModelFactory
            )
            val cartItems by cartViewModel.items.collectAsState()

            MainScreen(
                viewModel = mainViewModel,
                onOpenDetails = { publication ->
                    val json = gson.toJson(publication)
                    val encoded = java.net.URLEncoder.encode(json, Charsets.UTF_8.name())
                    navController.navigate(Screen.Detail.createRoute(encoded))
                },
                cartCount = cartItems.size,
                onOpenCart = { navController.navigate(Screen.Cart.route) },
                onOpenProfile = { navController.navigate(Screen.Profile.route) },
                onAddToCart = { publication ->
                    cartViewModel.addToCart(
                        publication = publication,
                        period = com.belpost.subscription.presentation.subscription.SubscriptionPeriod.ONE_MONTH
                    )
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
            val cartViewModel: CartViewModel = viewModel(
                viewModelStoreOwner = remember(backStackEntry) {
                    navController.getBackStackEntry(Screen.Main.route)
                },
                factory = appContainer.cartViewModelFactory
            )

            DetailScreen(
                publication = publication,
                viewModel = subscriptionViewModel,
                onBack = { navController.popBackStack() },
                 onAddToCart = { pub, period ->
                     cartViewModel.addToCart(pub, period)
                     navController.navigate(Screen.Cart.route)
                 },
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

        composable(Screen.Cart.route) { backStackEntry ->
            val cartViewModel: CartViewModel = viewModel(
                viewModelStoreOwner = remember(backStackEntry) {
                    navController.getBackStackEntry(Screen.Main.route)
                },
                factory = appContainer.cartViewModelFactory
            )
            CartScreen(
                viewModel = cartViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            val profileViewModel: ProfileViewModel = viewModel(
                factory = appContainer.profileViewModelFactory
            )
            ProfileScreen(
                viewModel = profileViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

