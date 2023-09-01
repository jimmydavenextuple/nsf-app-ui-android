package com.nextuple.nsf.ui.screen.home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.nextuple.nsf.BuildConfig
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.common.ButtonState
import com.nextuple.nsf.ui.common.PrimaryButton
import com.nextuple.nsf.ui.common.TopLabeledTextField
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily
import com.nextuple.nsf.ui.util.PreviewPdt

@Composable
fun LoginScreen(
	isInvalid: Boolean,
	resetIsInvalid: () -> Unit,
	errorMessage: String?,
	showProgressBar: Boolean,
	onSubmitDks: (dks: String) -> Unit,
	isLoggedIn: Boolean,
	onLoggedIn: () -> Unit
) {
	if (isLoggedIn) {
		LaunchedEffect(Unit) { onLoggedIn() }
	}

	val ctx = LocalContext.current
	val focusManager = LocalFocusManager.current

	var dks by remember {
		mutableStateOf(
			""
		)
	}

	Column {
		Box(
			modifier = Modifier
				.weight(0.775f)
				.fillMaxSize()
		) {
			Box(
				modifier = Modifier
					.fillMaxSize()
					.padding(start = 24.dp)
					.zIndex(Float.MAX_VALUE),
				contentAlignment = Alignment.BottomStart
			) {
				Column(
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Text(
						text = "GOAT",
						modifier = Modifier
							.align(Alignment.Start)
							.clickable {
								Toast
									.makeText(ctx, BuildConfig.APP_VERSION, Toast.LENGTH_LONG)
									.show()
							},
						style = TextStyle(
// 							fontFamily = FontFamily.SANS,
							fontFamily = FontFamily.ARCHIVO,
							fontWeight = FontWeight.Bold,
							fontStyle = FontStyle.Normal,
							letterSpacing = 1.5.sp,
							fontSize = 100.sp,
							color = BrandColor.BLACK
						)
					)
					TopLabeledTextField(
						modifier = Modifier
							.fillMaxWidth(0.45f)
							.padding(top = 12.dp, bottom = 8.dp, start = 8.dp, end = 8.dp)
							.align(Alignment.Start),
						labelText = stringResource(id = R.string.dks_number_label),
						fieldValue = dks,
						isInvalid = isInvalid,
						onValueChange = {
							dks = it
							if (isInvalid) {
								resetIsInvalid()
							}
						},
						errorMessage = errorMessage.orEmpty(),
						keyboardActions = KeyboardActions(onDone = {
							focusManager.clearFocus()
							onSubmitDks(dks)
						})
					)
					PrimaryButton(
						modifier = Modifier
							.fillMaxWidth(0.45f)
							.padding(start = 8.dp, end = 8.dp, top = 0.dp, bottom = 24.dp)
							.align(Alignment.Start),
						text = stringResource(id = R.string.login_text).uppercase(),
						enabled = dks.isNotBlank(),
						buttonState = if (showProgressBar) {
							ButtonState.LOADING
						} else {
							ButtonState.DEFAULT
						},
						onButtonClick = {
							onSubmitDks(dks)
						}
					)
				}
			}

			Box(
				modifier = Modifier.fillMaxSize(),
				contentAlignment = Alignment.TopEnd
			) {
				Image(
					modifier = Modifier.fillMaxHeight(),
					painter = painterResource(R.drawable.phil_login),
					contentDescription = null,
					contentScale = ContentScale.FillHeight
				)
			}
		}
	}
}

@Composable
@PreviewPdt
fun PreviewLoginScreen() {
	LoginScreen(
		isInvalid = false,
		resetIsInvalid = {},
		errorMessage = null,
		showProgressBar = false,
		onSubmitDks = {},
		isLoggedIn = false,
		onLoggedIn = {}
	)
}
