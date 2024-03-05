package com.nextuple.nsf.ui.nav

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.BuildConfig
import com.nextuple.nsf.R
import com.nextuple.nsf.service.dto.User
import com.nextuple.nsf.ui.common.OmniTextField
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily
import com.nextuple.nsf.ui.util.NoOpScanManager
import com.nextuple.nsf.ui.util.ScanManager
import com.nextuple.nsf.util.SubFulfillmentType

data class AppTopBarDropdownMenuItems(
	val label: String,
	@DrawableRes
	val iconResId: Int? = null,
	val onClick: () -> Unit = {}
)

@Composable
fun AppTopBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = BrandColor.BLUE_300_NT,
    dropdownBackgroundColor: Color = BrandColor.BLUE_300_NT,
    screen: Screen,
    user: User?,
    orderType: SubFulfillmentType?,
    items: List<AppTopBarDropdownMenuItems>,
    showSearchBar: Boolean = false,
    toggleSearchBar: (Boolean) -> Unit = {},
    scanManager: ScanManager = NoOpScanManager(),
    onSearchAction: (String) -> Unit = {},
    onBackAction: () -> Unit = {}
) {
	var searchInput by remember { mutableStateOf("") }

	// Resetting the search bar on screen change
	LaunchedEffect(screen) {
		toggleSearchBar(false)
		searchInput = ""

		if (screen.isSearchEnabled) {
			scanManager.set { data, _ ->
				searchInput = data
				onSearchAction(data)
			}
		}
	}

	Row(
		modifier = modifier
			.height(48.dp)
			.background(backgroundColor)
			.fillMaxWidth()
			.padding(start = 16.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween
	) {
		if (showSearchBar) {
			OmniTextField(
				modifier = Modifier.padding(top = 4.dp, bottom = 8.dp, end = 16.dp),
				icon = ImageVector.vectorResource(id = R.drawable.ic_search),
				iconTint = BrandColor.BLUE_250_NT,
				fieldValue = searchInput,
				hintText = stringResource(id = R.string.search_hint).uppercase(),
				onValueChange = { searchInput = it },
				keyboardActions = KeyboardActions(onDone = {
					onSearchAction(searchInput)
				})
			)
		} else {
			ScreenTitle(
				screen = screen,
				orderType = orderType,
				onBackAction = onBackAction
			)
			UserProfile(
				user = user,
				dropdownBackgroundColor = dropdownBackgroundColor,
				items = items,
				screen = screen,
				showSearchBar = {
					toggleSearchBar(true)
				}
			)
		}
	}
}

@Composable
private fun ScreenTitle(
    screen: Screen,
    orderType: SubFulfillmentType?,
    onBackAction: () -> Unit
) {
	val ctx = LocalContext.current

	val titleTextStyle = if (screen == Screen.HOME) {
		TextStyle(
			color = BrandColor.GRAY_50,
			fontFamily = FontFamily.SANS,
			fontWeight = FontWeight.Bold,
			fontStyle = FontStyle.Normal,
			letterSpacing = 1.sp,
			fontSize = 32.sp
		)
	} else {
		TextStyle(
			color = BrandColor.GRAY_50,
			fontFamily = FontFamily.ARCHIVO,
			fontWeight = FontWeight.Bold,
			fontStyle = FontStyle.Normal,
			letterSpacing = 0.5.sp,
			fontSize = 20.sp
		)
	}

	val title = if (screen == Screen.PICK_DETAILS) {
		screen.title + when (orderType) {
			SubFulfillmentType.BOPIS -> " Pickup"
			SubFulfillmentType.BOPL -> " BOPL"
			else -> ""
		}
	} else if (screen == Screen.PREP_ORDER) {
		screen.title + when (orderType) {
			SubFulfillmentType.BOPL -> " BOPL"
			else -> ""
		}
	} else {
		screen.title
	}

	Row(
		verticalAlignment = Alignment.CenterVertically
	) {
		if (screen.isBackEnabled) {
			IconButton(
				modifier = Modifier.width(IntrinsicSize.Min),
				onClick = { onBackAction() }
			) {
				Icon(
					modifier = Modifier
						.padding(end = 4.dp),
					imageVector = Icons.Default.ArrowBack,
					tint = BrandColor.GRAY_50,
					contentDescription = null
				)
			}
		}

		Text(
			modifier = Modifier.clickable {
				Toast.makeText(ctx, BuildConfig.APP_VERSION, Toast.LENGTH_LONG).show()
			},
			text = title,
			style = titleTextStyle
		)
	}
}

@Composable
private fun UserProfile(
    modifier: Modifier = Modifier,
    user: User?,
    dropdownBackgroundColor: Color,
    items: List<AppTopBarDropdownMenuItems>,
    screen: Screen,
    showSearchBar: () -> Unit = {}
) {
	var isExpanded by remember { mutableStateOf(false) }

	Row(
		modifier = modifier,
		verticalAlignment = Alignment.CenterVertically
	) {
		if (screen.isSearchEnabled) {
			IconButton(
				modifier = Modifier.width(30.dp),
				onClick = { showSearchBar() }
			) {
				Icon(
					modifier = Modifier.size(20.dp),
					imageVector = ImageVector.vectorResource(R.drawable.ic_search),
					tint = BrandColor.GRAY_50,
					contentDescription = null
				)
			}
		}

		Box(
			modifier = Modifier.width(IntrinsicSize.Max),
			contentAlignment = Alignment.CenterEnd
		) {
			UserInfo(
				modifier = Modifier.padding(end = 16.dp),
				user = user,
				isExpanded = isExpanded,
				expand = { isExpanded = true }
			)

			MaterialTheme(
				shapes = MaterialTheme.shapes.copy(
					extraSmall = RoundedCornerShape(0.dp, 0.dp, 0.dp, 8.dp)
				)
			) {
				DropdownMenu(
					modifier = Modifier
						.width(165.dp)
						.padding(0.dp)
						.background(color = dropdownBackgroundColor),
					expanded = isExpanded,
					onDismissRequest = { isExpanded = false }
				) {
					items.forEach { (label, iconResId, onClick) ->
						DropdownMenuItem(
							modifier = Modifier
								.height(32.dp)
								.padding(start = 8.dp, end = 16.dp, top = 0.dp),
							text = {
								Text(
									text = label.uppercase(),
									color = BrandColor.GRAY_100,
									fontSize = 12.sp,
									lineHeight = 13.sp,
									fontWeight = FontWeight.Bold,
									fontStyle = FontStyle.Normal,
									letterSpacing = 0.5.sp,
									fontFamily = FontFamily.ARCHIVO
								)
							},
							leadingIcon = {
								if (iconResId != null) {
									Icon(
										modifier = Modifier.size(16.dp).padding(0.dp),
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

@Composable
private fun UserInfo(
	modifier: Modifier = Modifier,
	user: User?,
	isExpanded: Boolean,
	expand: () -> Unit = {}
) {
	Row(
		modifier = modifier,
		verticalAlignment = Alignment.CenterVertically
	) {
		IconButton(
			modifier = Modifier.width(IntrinsicSize.Min),
			onClick = { expand() }
		) {
			if (user?.getInitials() != null) {
				Text(
					modifier = Modifier
						.drawBehind {
							drawCircle(
								color = BrandColor.BLUE_800_NT,
								radius = this.size.maxDimension - 8
							)
						},
					text = user.getInitials().orEmpty(),
					color = BrandColor.GRAY_50,
					fontFamily = FontFamily.ARCHIVO,
					fontWeight = FontWeight.Bold,
					fontStyle = FontStyle.Normal,
					fontSize = 16.sp,
					letterSpacing = 0.5.sp,
					textAlign = TextAlign.Center
				)
			} else {
				Icon(
					modifier = Modifier.padding(8.dp),
					imageVector = ImageVector.vectorResource(R.drawable.ic_profile),
					tint = BrandColor.GRAY_50,
					contentDescription = null
				)
			}
		}

		if (isExpanded) {
			Column(
				modifier = Modifier.widthIn(0.dp, 100.dp),
				verticalArrangement = Arrangement.spacedBy((-2).dp),
				horizontalAlignment = Alignment.Start
			) {
				Text(
					text = user?.getFullName().orEmpty(),
					color = BrandColor.GRAY_50,
					fontFamily = FontFamily.ARCHIVO,
					fontWeight = FontWeight.Bold,
					fontStyle = FontStyle.Normal,
					fontSize = 14.sp,
					letterSpacing = 0.5.sp,
					textAlign = TextAlign.Start,
					overflow = TextOverflow.Ellipsis,
					maxLines = 1
				)
				Text(
					text = user?.dks.orEmpty().uppercase(),
					color = BrandColor.GRAY_50,
					fontFamily = FontFamily.ARCHIVO,
					fontWeight = FontWeight.Light,
					fontStyle = FontStyle.Normal,
					letterSpacing = 0.5.sp,
					fontSize = 10.sp,
					textAlign = TextAlign.Start
				)
			}
		}
	}
}

@Composable
@Preview
fun PreviewAppTopBarMain() {
	AppTopBar(
		modifier = Modifier.fillMaxWidth(),
		screen = Screen.HOME,
		orderType = null,
		user = User(
			firstName = "Anna",
			lastName = "Heisey",
			dks = "dks0564797"
		),
		items = listOf(
			AppTopBarDropdownMenuItems(label = "Settings", R.drawable.ic_settings),
			AppTopBarDropdownMenuItems(label = "Feedback", R.drawable.ic_feedback),
			AppTopBarDropdownMenuItems(label = "Logout", R.drawable.ic_logout)
		)
	)
}

@Composable
@Preview
fun PreviewAppTopBarSettings() {
	AppTopBar(
		modifier = Modifier.fillMaxWidth(),
		screen = Screen.SETTINGS,
		orderType = null,
		user = User(
			firstName = "Laurancefff",
			lastName = "Alexander",
			dks = "dks0564792"
		),
		items = listOf()
	)
}

@Composable
@Preview
fun PreviewAppTopBarPickDetailsBopis() {
	AppTopBar(
		modifier = Modifier.fillMaxWidth(),
		screen = Screen.PICK_DETAILS,
		orderType = SubFulfillmentType.BOPIS,
		user = User(
			firstName = "Anna",
			lastName = "Heisey",
			dks = "dks0564797"
		),
		items = listOf()
	)
}

@Composable
@Preview
fun PreviewAppTopBarPickDetailsBopl() {
	AppTopBar(
		modifier = Modifier.fillMaxWidth(),
		screen = Screen.PICK_DETAILS,
		orderType = SubFulfillmentType.BOPL,
		user = User(
			firstName = "Anna",
			lastName = "Heisey",
			dks = "dks0564797"
		),
		items = listOf()
	)
}
