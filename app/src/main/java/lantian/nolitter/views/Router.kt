package lantian.nolitter.views

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import lantian.nolitter.views.model.PackageViewModel
import lantian.nolitter.views.screens.General
import lantian.nolitter.views.screens.Home
import lantian.nolitter.views.screens.Interface
import lantian.nolitter.views.screens.Miscellaneous
import lantian.nolitter.views.screens.PackageList
import lantian.nolitter.views.screens.PackagePreference
import lantian.nolitter.views.screens.PackageRedirect

@Composable
fun Router(
    innerPadding: PaddingValues,
    navController: NavHostController
) {
    val enterSlideDirection = AnimatedContentTransitionScope.SlideDirection.Left
    val exitSlideAnimation = AnimatedContentTransitionScope.SlideDirection.Right
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier.padding(innerPadding),
        contentAlignment = Alignment.TopCenter,
        enterTransition = { slideIntoContainer(towards = enterSlideDirection) },
        exitTransition = { slideOutOfContainer(towards = enterSlideDirection) },
        popEnterTransition = { slideIntoContainer(towards = exitSlideAnimation ) },
        popExitTransition = { slideOutOfContainer(towards = exitSlideAnimation) },
    ) {
        composable("home") {
            Home(navigateToComposable = { destination -> navController.navigate(destination)})
        }
        composable("general") { General() }
        composable("interface") { Interface() }
        composable("miscellaneous") { Miscellaneous() }
        composable("packages") {
            PackageList(
                packageViewModel = it.getPackageViewModel(navController),
                navigateToPackage = { packageName -> navController.navigate("package/$packageName") }
            )
        }
        composable("package/{packageName}") {
            PackagePreference(
                packageName = it.arguments?.getString("packageName") ?: "",
                packageViewModel = it.getPackageViewModel(navController),
                navigateToCustomRedirect = { packageName -> navController.navigate("package/$packageName/redirect") }
            )
        }
        composable("package/{packageName}/redirect") {
            PackageRedirect(
                packageName = it.arguments?.getString("packageName") ?: "",
                packageViewModel = it.getPackageViewModel(navController)
            )
        }
    }
}

@Composable
@SuppressLint("UnrememberedGetBackStackEntry")
fun NavBackStackEntry.getPackageViewModel(navController: NavController): PackageViewModel {
    val parentId = destination.parent!!.id
    val parentBackStackEntry = remember { navController.getBackStackEntry(parentId) }
    return hiltViewModel(parentBackStackEntry)
}
