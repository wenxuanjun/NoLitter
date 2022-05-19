package lantian.nolitter.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import lantian.nolitter.view.model.MainViewModel
import lantian.nolitter.view.theme.ApplicationTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AppUi(viewModel: MainViewModel) {
    ApplicationTheme(viewModel.appTheme.value) {
        val navController = rememberNavController()
        var canNavigationPop by remember { mutableStateOf(false) }
        val navigateUpIcon: @Composable () -> Unit = { Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null) }

        DisposableEffect(navController) {
            val listener = NavController.OnDestinationChangedListener { controller, destination, _ ->
                viewModel.topAppBarTitle.value = viewModel.getNavigationTitle(destination.route)
                canNavigationPop = controller.previousBackStackEntry != null
            }
            navController.addOnDestinationChangedListener(listener)
            onDispose { navController.removeOnDestinationChangedListener(listener) }
        }

        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = { Text(viewModel.topAppBarTitle.value) },
                    navigationIcon = { if (canNavigationPop) { IconButton(content = navigateUpIcon, onClick = { navController.navigateUp() }) } },
                    actions = viewModel.topAppBarActions.value
                )
            },
            content = { innerPadding -> Router(innerPadding, viewModel, navController) }
        )
    }
}