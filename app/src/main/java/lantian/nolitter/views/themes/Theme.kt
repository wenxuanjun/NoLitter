package lantian.nolitter.views.themes

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme()
private val DarkColorScheme = darkColorScheme()
val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

@Composable
fun ApplicationTheme(theme: String?, content: @Composable () -> Unit) {
    val lightColor = if (dynamicColor) dynamicLightColorScheme(LocalContext.current) else LightColorScheme
    val darkColor = if (dynamicColor) dynamicDarkColorScheme(LocalContext.current) else DarkColorScheme
    MaterialTheme(
        content = content,
        colorScheme = when (theme) {
            "light" -> lightColor
            "dark" -> darkColor
            else -> if(isSystemInDarkTheme()) darkColor else lightColor
        }
    )
}