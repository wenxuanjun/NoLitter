package lantian.nolitter.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import lantian.nolitter.models.MainViewModel
import lantian.nolitter.ui.screens.*

@Composable
fun Router(innerPadding: PaddingValues, viewModel: MainViewModel, navController: NavHostController) {
    NavHost(navController = navController, startDestination = "PreferenceHome", modifier = Modifier.padding(innerPadding)) {
        composable("PreferenceHome") { PreferenceHome(viewModel, navController) }
        composable("PreferenceGeneral") { PreferenceGeneral(viewModel, navController) }
        composable("PreferenceInterface") { PreferenceInterface(viewModel) }
        composable("PreferenceMiscellaneous") { PreferenceMiscellaneous(viewModel) }
        composable("PreferenceSelectApps") { PreferenceSelectApps(viewModel) }
    }
}