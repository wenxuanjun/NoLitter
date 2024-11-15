package lantian.nolitter

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import lantian.nolitter.views.AppUi

// Provide the ViewModelStoreOwner to used in the composable
val LocalActivity = staticCompositionLocalOf<ComponentActivity> {
    error("LocalActivity is not present!")
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent { CompositionLocalProvider(LocalActivity provides this@MainActivity) { AppUi() } }
    }
}
