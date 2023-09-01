package com.nextuple.nsf.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.nextuple.nsf.R

object FontFamily {
	val ARCHIVO = FontFamily(
		Font(R.font.archivo_thin, weight = FontWeight.Thin, style = FontStyle.Normal),
		Font(R.font.archivo_light, weight = FontWeight.Light, style = FontStyle.Normal),
		Font(R.font.archivo_regular, weight = FontWeight.Normal, style = FontStyle.Normal),
		Font(R.font.archivo_medium, weight = FontWeight.Medium, style = FontStyle.Normal),
		Font(R.font.archivo_semi_bold, weight = FontWeight.SemiBold, style = FontStyle.Normal),
		Font(R.font.archivo_bold, weight = FontWeight.Bold, style = FontStyle.Normal)
	)

// 	val SANS = FontFamily(
// 		Font(R.font.sans_thin, weight = FontWeight.Thin, style = FontStyle.Normal),
// 		Font(R.font.sans_medium, weight = FontWeight.Normal, style = FontStyle.Normal),
// 		Font(R.font.sans_bold, weight = FontWeight.SemiBold, style = FontStyle.Normal),
// 		Font(R.font.sans_black, weight = FontWeight.Bold, style = FontStyle.Normal),
// 		Font(R.font.sans_ultra, weight = FontWeight.ExtraBold, style = FontStyle.Normal)
// 	)
}
