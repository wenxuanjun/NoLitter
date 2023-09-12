package lantian.nolitter.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import lantian.nolitter.LocalActivity
import lantian.nolitter.views.model.MainViewModel
import lantian.nolitter.views.theme.ApplicationTheme

@Composable
@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
fun AppUi(viewModel: MainViewModel = hiltViewModel(LocalActivity.current)) {
    ApplicationTheme(viewModel.currentAppTheme) {
        val localContext = LocalContext.current
        val navController = rememberNavController()
        var canNavigationPop by remember { mutableStateOf(false) }

        DisposableEffect(navController) {
            val listener = NavController.OnDestinationChangedListener { controller, destination, _ ->
                MainViewModel.getNavigationTitle(localContext, destination.route).let {
                    // Initialize here and update in destination composable
                    viewModel.appBarContent = viewModel.appBarContent.copy(
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
                    title = viewModel.appBarContent.title,
                    navigationIcon = { TopAppBarNavigationIcon(canNavigationPop, navController) },
                    actions = viewModel.appBarContent.actions
                )
            },
            content = { innerPadding -> Router(innerPadding, navController) }
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
