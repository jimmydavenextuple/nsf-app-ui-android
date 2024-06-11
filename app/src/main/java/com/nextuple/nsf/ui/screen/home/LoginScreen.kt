package com.nextuple.nsf.ui.screen.home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.BuildConfig
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.common.ButtonState
import com.nextuple.nsf.ui.common.PrimaryButton
import com.nextuple.nsf.ui.common.TopLabeledTextField
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily
import com.nextuple.nsf.ui.util.Haptics
import com.nextuple.nsf.ui.util.NoOpScanManager
import com.nextuple.nsf.ui.util.OnDataScanned
import com.nextuple.nsf.ui.util.PreviewPdt
import com.nextuple.nsf.ui.util.ScanManager

@Composable
fun LoginScreen(
	isFormInvalid: Boolean,
	resetIsFormInvalid: () -> Unit,
	errorMessage: String?,
	showProgressBar: Boolean,
	onSubmit: (nodeId: String, userId: String) -> Unit,
	isLoggedIn: Boolean,
	onLoggedIn: () -> Unit,
	scanManager: ScanManager,
	haptics: Haptics?,
	isInValidSymbology: (String) -> Boolean
) {
	if (isLoggedIn) {
		LaunchedEffect(Unit) { onLoggedIn() }
	}

	val ctx = LocalContext.current
	val focusManager = LocalFocusManager.current

	var nodeId by remember {
		mutableStateOf("")
	}

	var userId by remember {
		mutableStateOf(
			if (BuildConfig.DEBUG && BuildConfig.AUTO_FILL_DKS) {
				BuildConfig.DKS.lowercase()
			} else {
				""
			}
		)
	}

	// Awaiting approval to add to production
	if (BuildConfig.DEBUG) {
		ScanLoginEffect(
			scanManager = scanManager,
			haptics = haptics,
			isInValidSymbology = isInValidSymbology,
			onScanLogin = {
				userId = it.lowercase()
				onSubmit(nodeId, userId)
			}
		)
	}

	if(!errorMessage.isNullOrEmpty() && !isFormInvalid) {
		Toast.makeText(
			LocalContext.current,
			errorMessage,
			Toast.LENGTH_LONG
		).show()
	}

	Box(
		modifier = Modifier.fillMaxSize()
	) {
		Column(
			modifier = Modifier
				.align(Alignment.Center)
				.width(IntrinsicSize.Max),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			val annotatedText = buildAnnotatedString {
				withStyle(style = SpanStyle(color = BrandColor.PINK_NT)) {
					append("Nextuple")
				}
				append("\n")
				withStyle(style = SpanStyle(color = BrandColor.YELLOW_NT)) {
					append("Store")
				}
				append("\n")
				withStyle(style = SpanStyle(color = BrandColor.BLUE_300_NT)) {
					append("Fulfillment")
				}
			}
			Text(
				text = annotatedText,
				modifier = Modifier
					.align(Alignment.Start)
					.clickable {
						Toast
							.makeText(ctx, BuildConfig.APP_VERSION, Toast.LENGTH_LONG)
							.show()
					},
				style = TextStyle(
					fontFamily = FontFamily.ARCHIVO,
					fontWeight = FontWeight.Bold,
					fontStyle = FontStyle.Normal,
					letterSpacing = 1.5.sp,
					fontSize = 50.sp,
					color = BrandColor.GRAY_900
				)
			)
			TopLabeledTextField(
				modifier = Modifier
					.padding(top = 24.dp),
				labelText = stringResource(id = R.string.login_node_id_text_label),
				fieldValue = nodeId,
				isInvalid = nodeId.isEmpty() && isFormInvalid,
				onValueChange = {
					resetIsFormInvalid()
					nodeId = it.trim()
				},
				errorMessage = errorMessage.orEmpty() + ' ' + stringResource(id = R.string.login_node_id_text_label),
				keyboardActions = KeyboardActions(onDone = {
					focusManager.clearFocus()
					onSubmit(nodeId, userId)
				})
			)
			TopLabeledTextField(
				modifier = Modifier
					.padding(top = 6.dp),
				labelText = stringResource(id = R.string.login_user_id_text_label),
				fieldValue = userId,
				isInvalid = userId.isEmpty() && isFormInvalid,
				onValueChange = {
					resetIsFormInvalid()
					userId = it.trim()
				},
				errorMessage = errorMessage.orEmpty() + ' ' + stringResource(id = R.string.login_user_id_text_label),
				keyboardActions = KeyboardActions(onDone = {
					focusManager.clearFocus()
					onSubmit(nodeId, userId)
				})
			)
			PrimaryButton(
				modifier = Modifier
					.width(width = 152.dp)
					.padding(top = 12.dp),
				text = stringResource(id = R.string.login_text).uppercase(),
				enabled = nodeId.isNotBlank() && userId.isNotBlank(),
				buttonState = if (showProgressBar) {
					ButtonState.LOADING
				} else {
					ButtonState.DEFAULT
				},
				onButtonClick = {
					onSubmit(nodeId, userId)
				}
			)
		}

		Text(
			modifier = Modifier
				.align(Alignment.BottomStart)
				.padding(24.dp),
			text = "NSF ${BuildConfig.APP_VERSION}",
			fontSize = 10.sp,
			fontFamily = FontFamily.ARCHIVO,
			fontWeight = FontWeight(700),
			color = BrandColor.GRAY_900,
			letterSpacing = 1.5.sp
		)

		Box(
			modifier = Modifier.fillMaxSize(),
			contentAlignment = Alignment.TopEnd
		) {
			Image(
				modifier = Modifier
					.padding(top = 18.dp, end = 12.dp)
					.height(40.dp),
				painter = painterResource(R.drawable.nextuple_fulllogo),
				contentDescription = null,
				contentScale = ContentScale.FillHeight
			)
		}
	}
}

@Composable
private fun ScanLoginEffect(
	scanManager: ScanManager,
	haptics: Haptics?,
	isInValidSymbology: (String) -> Boolean,
	onScanLogin: (String) -> Unit
) {
	val onLoginScan: OnDataScanned = { scannedValue, symbology ->
		if (symbology == null || isInValidSymbology(symbology)) {
			haptics?.boop()
		} else {
			onScanLogin(scannedValue)
		}
	}

	LaunchedEffect(Unit) {
		scanManager.set(onLoginScan)
	}
}

@Composable
@PreviewPdt
private fun PreviewLoginScreen() {
	LoginScreen(
		isFormInvalid = false,
		resetIsFormInvalid = {},
		errorMessage = null,
		showProgressBar = false,
		onSubmit = { _, _ -> },
		isLoggedIn = false,
		onLoggedIn = {},
		scanManager = NoOpScanManager(),
		haptics = null,
		isInValidSymbology = { _ -> false }
	)
}

@Composable
@PreviewPdt
private fun PreviewLoginErrorScreen() {
	LoginScreen(
		isFormInvalid = true,
		resetIsFormInvalid = {},
		errorMessage = "Log in Error",
		showProgressBar = false,
		onSubmit = { _, _ -> },
		isLoggedIn = false,
		onLoggedIn = {},
		scanManager = NoOpScanManager(),
		haptics = null,
		isInValidSymbology = { _ -> false }
	)
}
