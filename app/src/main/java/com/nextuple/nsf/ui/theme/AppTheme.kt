package com.nextuple.nsf.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * TODO: Once this theme is properly set up, replace AndroidManifest xml-based theme with this.
 */
@Composable
fun AppTheme(isDarkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
	MaterialTheme(
		colorScheme = if (isDarkTheme) darkColors else lightColors,
		typography = typography,
		shapes = shapes,
		content = content
	)
	val view = LocalView.current
	if(!view.isInEditMode) {
		SideEffect {
			val window = (view.context as Activity).window
			window.statusBarColor = darkColors.primary.toArgb()
		}
	}
}

// TODO: Properly set these up and remove overrides in relevant UI.
private val darkColors = darkColorScheme(
	primary = BrandColor.BLUE_300_NT
)

private val lightColors = lightColorScheme()
private val typography = Typography(
	bodyLarge = TextStyle(
		fontFamily = FontFamily.Default,
		fontWeight = FontWeight.Normal,
		fontSize = 16.sp
	)
)
private val shapes = Shapes(
	small = RoundedCornerShape(4.dp),
	medium = RoundedCornerShape(4.dp),
	large = RoundedCornerShape(0.dp)
)
