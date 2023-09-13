package com.nextuple.nsf.ui.nav

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.BuildConfig
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily

data class AppTopBarDropdownMenuItems(
	val label: String,
	@DrawableRes
	val iconResId: Int? = null,
	val onClick: () -> Unit = {}
)

@Composable
fun AppTopBar(
	modifier: Modifier = Modifier,
	backgroundColor: Color,
	title: String,
	userLastName: String,
	dks: String,
	items: List<AppTopBarDropdownMenuItems>
) {
	val ctx = LocalContext.current

	var isExpanded by remember { mutableStateOf(false) }

	Row(
		modifier = modifier
			.background(backgroundColor)
			.fillMaxWidth()
			.padding(start = 20.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween
	) {

		Text(
			modifier = Modifier.clickable {
				Toast.makeText(ctx, BuildConfig.APP_VERSION, Toast.LENGTH_LONG).show()
			},
			text = title,
			color = BrandColor.GRAY_50,
			fontFamily = FontFamily.ARCHIVO,
			fontWeight = FontWeight.Bold,
			fontStyle = FontStyle.Normal,
			fontSize = 20.sp
		)

		Row(
			verticalAlignment = Alignment.CenterVertically
		) {
			Column(
				verticalArrangement = Arrangement.spacedBy((-5).dp),
				horizontalAlignment = Alignment.End
			) {
				Text(
					text = userLastName.uppercase(),
					color = BrandColor.GRAY_50,
// 					fontFamily = FontFamily.SANS,
					fontFamily = FontFamily.ARCHIVO,
					fontWeight = FontWeight.Bold,
					fontStyle = FontStyle.Normal,
					fontSize = 24.sp,
					letterSpacing = 1.sp,
					textAlign = TextAlign.End
				)
				Text(
					text = dks.replace("dks", "").uppercase(),
					color = BrandColor.GRAY_50,
					fontFamily = FontFamily.ARCHIVO,
					fontWeight = FontWeight.Light,
					fontStyle = FontStyle.Normal,
					fontSize = 10.sp
				)
			}

			Box(modifier = Modifier.width(IntrinsicSize.Min)) {
				IconButton(
					modifier = Modifier.width(IntrinsicSize.Min),
					onClick = { isExpanded = true }
				) {
					Icon(
						modifier = Modifier
							.graphicsLayer {
								rotationX = if (isExpanded) 180f else 0f
							}
							.align(alignment = Alignment.CenterStart),
						imageVector = Icons.Default.ArrowDropDown,
						tint = BrandColor.BLUE_200_NT,
						contentDescription = null
					)
				}
				MaterialTheme(
					shapes = MaterialTheme.shapes.copy(
						extraSmall = RoundedCornerShape(
							0.dp,
							0.dp,
							16.dp,
							16.dp
						)
					)
				) {
					DropdownMenu(
						modifier = Modifier.background(backgroundColor),
						expanded = isExpanded,
						onDismissRequest = { isExpanded = false }
					) {
						items.forEach { (label, iconResId, onClick) ->
							DropdownMenuItem(
								modifier = Modifier.padding(start = 16.dp, end = 16.dp),
								text = {
									Text(
										text = label,
										color = BrandColor.GRAY_100,
										fontFamily = FontFamily.ARCHIVO
									)
								},
								leadingIcon = {
									if (iconResId != null) {
										Icon(
											imageVector = ImageVector.vectorResource(iconResId),
											tint = BrandColor.GRAY_50,
											contentDescription = null
										)
									}
								},
								onClick = {
									isExpanded = false
									onClick.invoke()
								}
							)
						}
					}
				}
			}
		}
	}
}

@Composable
@Preview
fun PreviewAppTopBar() {
	AppTopBar(
		modifier = Modifier.fillMaxWidth(),
		backgroundColor = BrandColor.BLUE_800_NT,
		title = "ORDER DETAILS",
		userLastName = "Heisey",
		dks = "dks0564797",
		items = listOf(
			AppTopBarDropdownMenuItems(label = "Settings", R.drawable.ic_settings),
			AppTopBarDropdownMenuItems(label = "Feedback", R.drawable.ic_feedback),
			AppTopBarDropdownMenuItems(label = "Logout", R.drawable.ic_logout)
		)
	)
}
