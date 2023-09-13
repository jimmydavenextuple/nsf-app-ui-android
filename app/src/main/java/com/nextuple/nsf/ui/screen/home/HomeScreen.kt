package com.nextuple.nsf.ui.screen.home

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.common.TopLabeledTextField
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily
import com.nextuple.nsf.ui.util.NoOpScanManager
import com.nextuple.nsf.ui.util.PreviewPdt
import com.nextuple.nsf.ui.util.ScanManager

@Composable
fun HomeScreen(
	scanManager: ScanManager,
	onSearchClick: (String) -> Unit
) {
	val context = LocalContext.current

	val focusManager = LocalFocusManager.current

	val isInvalid = remember { mutableStateOf(false) }

	val errorMessage = remember { mutableStateOf("") }

	val showProgressBar = remember { mutableStateOf(false) }

	var searchInput by remember { mutableStateOf("") }

	// Colors will change based on input search
	val searchBackgroundColor: Color
	val searchTextColor: Color
	val searchTintColor: Color

	if (searchInput.isNotBlank()) {
		searchBackgroundColor = BrandColor.ORANGE_600
		searchTextColor = BrandColor.GRAY_50
		searchTintColor = BrandColor.GRAY_50
	} else {
		searchBackgroundColor = BrandColor.GRAY_200
		searchTextColor = BrandColor.GRAY_600
		searchTintColor = BrandColor.GRAY_600
	}

	fun setErrorMessage(error: String) {
		isInvalid.value = true
		errorMessage.value = error
		showProgressBar.value = false
	}

	LaunchedEffect(Unit) {
		scanManager.set { data, _ ->
			searchInput = data
		}
	}

	BackHandler {
		val activity = context as? Activity
		activity?.finish()
	}

	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(BrandColor.GRAY_100)
	) {
		Column {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.background(color = searchBackgroundColor)
					.padding(start = 20.dp)
					.height(40.dp)
			) {
				Row(
					verticalAlignment = Alignment.CenterVertically
				) {
					Box(
						modifier = Modifier.fillMaxSize(),
						contentAlignment = Alignment.TopStart
					) {
						Image(
							modifier = Modifier.fillMaxHeight().padding(top = 10.dp, bottom = 10.dp),
							painter = painterResource(R.drawable.nextuple_fulllogo),
							contentDescription = null,
							contentScale = ContentScale.FillHeight
						)
					}

//					Image(
//						modifier = Modifier.fillMaxHeight().padding(top = 10.dp, bottom = 10.dp),
//						painter = painterResource(R.drawable.nextuple_logo),
//						contentDescription = "nextuple logo"
//					)
//					Text(
//						text = stringResource(id = R.string.nsf_label),
//						fontFamily = FontFamily.ARCHIVO,
//						fontWeight = FontWeight.Bold,
//						fontStyle = FontStyle.Normal,
//						modifier = Modifier
//							.fillMaxWidth()
//							.padding(start = 8.dp),
//						fontSize = 16.sp,
//						color = BrandColor.BLACK
//					)

				}
			}
		}


		if (showProgressBar.value) {
			CircularProgressIndicator(
				modifier = Modifier.align(Alignment.Center),
				color = Color.Gray
			)
		}
		Card(
			modifier = Modifier.padding(24.dp, 64.dp, 24.dp, 24.dp),
			shape = RoundedCornerShape(12.dp),
			colors = CardDefaults.cardColors(containerColor = BrandColor.GRAY_50)
		) {
			Column(
				horizontalAlignment = Alignment.Start,
				verticalArrangement = Arrangement.Top,
				modifier = Modifier
					.padding(16.dp)
					.fillMaxHeight(0.4f)
			) {
				Text(
					text = stringResource(id = R.string.order_search_label),
					fontFamily = FontFamily.ARCHIVO,
					fontWeight = FontWeight.Bold,
					fontStyle = FontStyle.Normal,
					modifier = Modifier
						.align(Alignment.Start)
						.fillMaxWidth(),
					fontSize = 20.sp,
					color = BrandColor.BLUE_800_NT
				)
				Box(
					modifier = Modifier
						.fillMaxWidth()
						.padding(top = 8.dp)
				) {
					TopLabeledTextField(
						modifier = Modifier.fillMaxWidth(0.75f),
						labelText = stringResource(id = R.string.order_number_or_last_name),
						fieldValue = searchInput,
						isInvalid = isInvalid.value,
						onValueChange = {
							searchInput = it
							isInvalid.value = false
						},
						errorMessage = errorMessage.value,
						textFieldShape = RoundedCornerShape(
							topStart = 6.dp,
							bottomStart = 6.dp,
							topEnd = 0.dp,
							bottomEnd = 0.dp
						),
						keyboardActions = KeyboardActions(onDone = {
							focusManager.clearFocus()
							setErrorMessage("Invalid Order Id")
						})
					)
					Surface(
						shape = RoundedCornerShape(
							topStart = 0.dp,
							bottomStart = 0.dp,
							topEnd = 6.dp,
							bottomEnd = 6.dp
						),
						modifier = Modifier
							.fillMaxWidth(0.25f)
							.padding(top = 1.dp, start = 0.dp)
							.align(Alignment.CenterEnd)
					) {
						Column(
							modifier = Modifier
								.height(40.dp)
								.align(Alignment.CenterEnd)
								.background(color = BrandColor.BLUE_100_NT)
								.clickable(enabled = searchInput.isNotBlank()) {
									onSearchClick(searchInput)
								},

							verticalArrangement = Arrangement.Center
						) {
							Icon(
								modifier = Modifier
									.align(Alignment.CenterHorizontally)
									.size(18.dp),
								imageVector = ImageVector.vectorResource(R.drawable.ic_order_search),
								tint = BrandColor.BLUE_800_NT,
								contentDescription = ""
							)
							Text(
								text = stringResource(id = R.string.search).uppercase(),
								style = TextStyle(
									fontFamily = FontFamily.ARCHIVO,
									fontWeight = FontWeight.Bold,
									fontStyle = FontStyle.Normal,
									letterSpacing = 1.5.sp,
									fontSize = 10.sp
								),
								modifier = Modifier.align(Alignment.CenterHorizontally),
								color = BrandColor.BLUE_800_NT
							)
						}
					}
				}
			}
		}
	}
}

@Composable
@PreviewPdt
fun PreviewHomeScreen() {
	HomeScreen(scanManager = NoOpScanManager(), onSearchClick = {})
}
