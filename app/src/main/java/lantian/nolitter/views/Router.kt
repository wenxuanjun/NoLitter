package lantian.nolitter.views

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import lantian.nolitter.views.model.MainViewModel
import lantian.nolitter.views.model.PackageViewModel
import lantian.nolitter.views.screens.*

@Composable
fun Router(innerPadding: PaddingValues, viewModel: MainViewModel, navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home", modifier = Modifier.padding(innerPadding)) {
        composable("home") { Home(navController) }
        composable("general") { General(viewModel) }
        composable("interface") { Interface(viewModel) }
        composable("miscellaneous") { Miscellaneous(viewModel) }
        composable("packages") { PackageList(navController, viewModel, it.getPackageViewModel(navController)) }
        composable("package/{packageName}") {
            val packageName = it.arguments?.getString("packageName") ?: ""
            PackagePreference(packageName, viewModel, it.getPackageViewModel(navController))
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