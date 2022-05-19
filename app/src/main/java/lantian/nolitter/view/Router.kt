package lantian.nolitter.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import lantian.nolitter.view.model.MainViewModel
import lantian.nolitter.view.screens.*

@Composable
fun Router(innerPadding: PaddingValues, viewModel: MainViewModel, navController: NavHostController) {
    NavHost(navController = navController, startDestination = "PreferenceHome", modifier = Modifier.padding(innerPadding)) {
        composable("PreferenceHome") { PreferenceHome(navController) }
        composable("PreferenceGeneral") { PreferenceGeneral(navController) }
        composable("PreferenceInterface") { PreferenceInterface(viewModel) }
        composable("PreferenceMiscellaneous") { PreferenceMiscellaneous(viewModel) }
        composable("PreferenceSelectApps") { PreferenceSelectApps(viewModel) }
    }
}