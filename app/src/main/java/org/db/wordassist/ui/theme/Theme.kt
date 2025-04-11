package org.db.wordassist.ui.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
  primary = primaryLight,
  secondary = secondaryLight,
  tertiary = tertiaryLight,
  background = backgroundLight,
  onBackground = onBackgroundLight,
  onPrimary = onPrimaryLight,
  surface = correctBackground,
  error = wrongPositionBackground,
  onSurface = incorrectBackground,
  onSecondary = keyboard,
  onTertiary = keyboardDisabled,
  secondaryContainer = onKeyboard,
  primaryContainer = topBar

)

private val LightColorScheme = lightColorScheme(
  primary = primaryDark,
  secondary = secondaryDark,
  tertiary = tertiaryDark,
  background = backgroundDark,
  onBackground = onBackgroundDark,
  onPrimary = onPrimaryDark,
  surface = correctBackground,
  error = wrongPositionBackground,
  onSurface = incorrectBackground,
  onSecondary = keyboard,
  onTertiary = keyboardDisabled,
  secondaryContainer = onKeyboard,
  primaryContainer = topBar

)

@Composable
fun WordAssistTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),

  content: @Composable () -> Unit

) {
  val colorScheme = if (!darkTheme) {
    LightColorScheme
  } else {
    DarkColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = AppTypography,
    content = content
  )
}