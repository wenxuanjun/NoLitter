package lantian.nolitter.views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import lantian.nolitter.views.model.MainViewModel
import lantian.nolitter.views.model.TopAppBarContent
import lantian.nolitter.views.theme.ApplicationTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AppUi(viewModel: MainViewModel = hiltViewModel()) {
    ApplicationTheme(viewModel.appTheme) {
        val navController = rememberNavController()
        var canNavigationPop by remember { mutableStateOf(false) }

        DisposableEffect(navController) {
            val listener = NavController.OnDestinationChangedListener { controller, destination, _ ->
                viewModel.topAppBarContent = viewModel.topAppBarContent.copy(
                    title = viewModel.getNavigationTitle(destination.route)
                )
                canNavigationPop = controller.previousBackStackEntry != null
            }
            navController.addOnDestinationChangedListener(listener)
            onDispose { navController.removeOnDestinationChangedListener(listener) }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { TopAppBarTitle(viewModel.topAppBarContent) },
                    navigationIcon = { TopAppBarNavigationIcon(canNavigationPop, navController) },
                    actions = viewModel.topAppBarContent.actions
                )
            },
            content = { innerPadding -> Router(innerPadding, viewModel, navController) }
        )
    }
}

@Composable
fun TopAppBarTitle(topAppBarContent: TopAppBarContent) {
    if (topAppBarContent.isTitleCompose) topAppBarContent.titleCompose()
    else Text(topAppBarContent.title)
}

@Composable
fun TopAppBarNavigationIcon(canNavigationPop: Boolean, navController: NavController) {
    val navigateUpIcon: @Composable () -> Unit = {
        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
    }
    if (canNavigationPop) {
        IconButton(content = navigateUpIcon, onClick = { navController.navigateUp() })
    }
}