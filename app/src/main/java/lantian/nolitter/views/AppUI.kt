package lantian.nolitter.views

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import lantian.nolitter.views.model.MainViewModel
import lantian.nolitter.views.theme.ApplicationTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AppUi(viewModel: MainViewModel = hiltViewModel()) {
    ApplicationTheme(viewModel.appTheme) {
        val localContext = LocalContext.current
        val navController = rememberNavController()
        var canNavigationPop by remember { mutableStateOf(false) }

        DisposableEffect(navController) {
            val listener = NavController.OnDestinationChangedListener { controller, destination, _ ->
                MainViewModel.getNavigationTitle(localContext, destination.route).let {
                    // TopAppBar initialized here and updated in destination composable
                    viewModel.topAppBarContent = viewModel.topAppBarContent.copy(
                        title = { Text(it) }, actions = {}
                    )
                }
                canNavigationPop = controller.previousBackStackEntry != null
            }
            navController.addOnDestinationChangedListener(listener)
            onDispose { navController.removeOnDestinationChangedListener(listener) }
        }

        // To display the content edge-to-edge, use the Insets API
        TransparentSystemBars()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = viewModel.topAppBarContent.title,
                    navigationIcon = { TopAppBarNavigationIcon(canNavigationPop, navController) },
                    actions = viewModel.topAppBarContent.actions
                )
            },
            content = { innerPadding -> Router(innerPadding, viewModel, navController) }
        )
    }
}

@Composable
fun TransparentSystemBars() {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()

    DisposableEffect(systemUiController, useDarkIcons) {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons
        )
        onDispose {}
    }
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