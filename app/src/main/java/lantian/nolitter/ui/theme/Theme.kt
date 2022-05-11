package lantian.nolitter.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors()
private val LightColorPalette = lightColors()

@Composable
fun ApplicationTheme(theme: String?, content: @Composable () -> Unit) {
    MaterialTheme(
        content = content,
        colors = when (theme) {
            "light" -> LightColorPalette
            "dark" -> DarkColorPalette
            else -> if(isSystemInDarkTheme()) DarkColorPalette else LightColorPalette
        }
    )
}