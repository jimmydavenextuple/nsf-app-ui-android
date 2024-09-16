package com.nextuple.nsf.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
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
}

// TODO: Properly set these up and remove overrides in relevant UI.
private val darkColors = darkColorScheme(
	primary = BrandColor.DARK_BLUE
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
