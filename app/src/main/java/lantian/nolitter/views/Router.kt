package lantian.nolitter.views

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import lantian.nolitter.views.model.MainViewModel
import lantian.nolitter.views.screens.*

@Composable
fun Router(innerPadding: PaddingValues, viewModel: MainViewModel, navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home", modifier = Modifier.padding(innerPadding)) {
        composable("home") { Home(navController) }
        composable("general") { General(navController) }
        composable("packages") { PackageList(navController, viewModel) }
        composable("package/{packageName}") { PackagePreference(it.arguments?.getString("packageName") ?: "") }
        composable("interface") { Interface(viewModel) }
        composable("miscellaneous") { Miscellaneous(viewModel) }
    }
}