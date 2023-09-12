package lantian.nolitter.views.screens

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import lantian.nolitter.LocalActivity
import lantian.nolitter.views.model.MainViewModel
import lantian.nolitter.views.model.PackageViewModel

@Composable
fun PackageRedirect(
    packageName: String,
    packageViewModel: PackageViewModel,
    viewModel: MainViewModel = hiltViewModel(LocalActivity.current)
) {


}
