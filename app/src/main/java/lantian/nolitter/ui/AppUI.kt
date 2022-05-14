package lantian.nolitter.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import lantian.nolitter.R
import lantian.nolitter.models.MainViewModel
import lantian.nolitter.ui.theme.ApplicationTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AppUi(viewModel: MainViewModel) {
    ApplicationTheme(viewModel.appTheme.value) {
        val navController = rememberNavController()
        var canNavigationPop by remember { mutableStateOf(false) }
        val navigateUpIcon: @Composable () -> Unit = { Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null) }
        val toolbarController = object {
            fun setTitle(title: String){
                viewModel.topAppBarTitle.value = title
            }
            fun setAction(actions: @Composable RowScope.() -> Unit) {
                viewModel.topAppBarActions.value = actions
            }
        }

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
                    navigationIcon = { if (canNavigationPop) { IconButton(content = navigateUpIcon, onClick = { navController.navigateUp() }) } else null },
                    actions = viewModel.topAppBarActions.value
                )
            },
            content = { innerPadding ->
                if (viewModel.isModuleEnabled()) Router(innerPadding, viewModel, navController)
                else ModuleNotEnabled(innerPadding)
            }
        )
    }
}

@Composable
fun ModuleNotEnabled(innerPadding: PaddingValues) {
    Column(
        modifier = Modifier.fillMaxSize().padding(innerPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        content = { Text(stringResource(R.string.ui_moduleNotEnabled)) }
    )
}