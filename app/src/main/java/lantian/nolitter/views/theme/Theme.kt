package lantian.nolitter.views.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

data class ColorSchemeGroup(
    val lightColor: ColorScheme,
    val darkColor: ColorScheme,
)

private val LightColorScheme = lightColorScheme()
private val DarkColorScheme = darkColorScheme()

@Composable
@RequiresApi(Build.VERSION_CODES.S)
fun ApplicationTheme(theme: String?, content: @Composable () -> Unit) {
    val colorSchemeGroup =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            ColorSchemeGroup(
                dynamicLightColorScheme(LocalContext.current),
                dynamicDarkColorScheme(LocalContext.current)
            )
        else
            ColorSchemeGroup(LightColorScheme, DarkColorScheme)

    val colorScheme = when (theme) {
        "light" -> colorSchemeGroup.lightColor
        "dark" -> colorSchemeGroup.darkColor
        else ->
            if (isSystemInDarkTheme())
                colorSchemeGroup.darkColor
            else
                colorSchemeGroup.lightColor
    }
    MaterialTheme(content = content, colorScheme = colorScheme)
}
