package com.nextuple.nsf.ui.screen.home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
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
import com.nextuple.nsf.BuildConfig
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.common.ButtonState
import com.nextuple.nsf.ui.common.PrimaryButton
import com.nextuple.nsf.ui.common.TopLabeledTextField
import com.nextuple.nsf.ui.theme.BrandColor
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
	onSubmit: (nodeNo: String, userId: String) -> Unit,
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

	var nodeNo by remember {
		mutableStateOf("")
	}

	var userId by remember {
		mutableStateOf(
			if (BuildConfig.DEBUG && BuildConfig.AUTO_FILL_USER_ID) {
				BuildConfig.USER_ID.lowercase()
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
				onSubmit(nodeNo, userId)
			}
		)
	}

	if (!errorMessage.isNullOrEmpty() && !isFormInvalid) {
		Toast.makeText(
			LocalContext.current,
			errorMessage,
			Toast.LENGTH_LONG
		).show()
	}

	Box(
		modifier = Modifier.fillMaxSize()
			.background(
				brush = Brush.verticalGradient(
					colors = listOf(
						BrandColor.BG_GRADIENT_TOP, // Start color
						BrandColor.STRONG_PINK // End color
					),
				),
				alpha = .8f
			)
	) {
		Image(
			modifier = Modifier
				.padding(50.dp, 100.dp, 0.dp, 100.dp)
				.matchParentSize(),
			painter = painterResource(id = R.drawable.nextuple_building_blocks),
			contentDescription = null,
			contentScale = ContentScale.FillBounds,
		)

		Column(
			modifier = Modifier
				.align(Alignment.TopStart)
				.fillMaxWidth()
				.padding(10.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			Text(
				text = "Store",
				modifier = Modifier
					.align(Alignment.Start)
					.padding(top = 20.dp)
					.clickable {
						Toast
							.makeText(ctx, BuildConfig.APP_VERSION, Toast.LENGTH_SHORT)
							.show()
					},
				style = TextStyle(
					fontWeight = FontWeight.ExtraBold,
					fontStyle = FontStyle.Normal,
					letterSpacing = 1.5.sp,
					fontSize = 50.sp,
					color = BrandColor.WHITE,
					shadow = Shadow(
						color = Color.Black,
						offset = Offset(3f, 3f),
						blurRadius = 2f
					)
				)
			)
			Text(
				text = "Fulfillment",
				modifier = Modifier
					.align(Alignment.Start)
					.clickable {
						Toast
							.makeText(ctx, BuildConfig.APP_VERSION, Toast.LENGTH_SHORT)
							.show()
					},
				style = TextStyle(
					fontWeight = FontWeight.ExtraBold,
					fontStyle = FontStyle.Normal,
					letterSpacing = 1.5.sp,
					fontSize = 50.sp,
					color = BrandColor.WHITE,
					shadow = Shadow(
						color = Color.Black,
						offset = Offset(3f, 3f),
						blurRadius = 2f
					)
				)
			)
			TopLabeledTextField(
				modifier = Modifier
					.padding(top = 2.dp),
				labelColor = Color.White,
				labelText = "",
				hintText = stringResource(id = R.string.login_node_id_text_label),
				fieldValue = nodeNo,
				isInvalid = nodeNo.isEmpty() && isFormInvalid,
				onValueChange = {
					resetIsFormInvalid()
					nodeNo = it.trim()
				},
				errorMessage = stringResource(id = R.string.login_node_id_text_invalid_error),
				keyboardActions = KeyboardActions(onDone = {
					focusManager.clearFocus()
					onSubmit(nodeNo, userId)
				})
			)
			TopLabeledTextField(
				modifier = Modifier,
				labelColor = Color.White,
				labelText = "",
				hintText = stringResource(id = R.string.login_user_id_text_label),
				fieldValue = userId,
				isInvalid = userId.isEmpty() && isFormInvalid,
				onValueChange = {
					resetIsFormInvalid()
					userId = it.trim()
				},
				errorMessage = stringResource(id = R.string.login_user_id_text_invalid_error),
				keyboardActions = KeyboardActions(onDone = {
					focusManager.clearFocus()
					onSubmit(nodeNo, userId)
				})
			)
			PrimaryButton(
				modifier = Modifier
					.width(width = 110.dp)
					.padding(top = 100.dp),
				text = stringResource(id = R.string.login_text).uppercase(),
				buttonColor = Color(0xFF0060AA),
				enabled = nodeNo.isNotBlank() && userId.isNotBlank(),
				buttonState = if (showProgressBar) {
					ButtonState.LOADING
				} else {
					ButtonState.DEFAULT
				},
				onButtonClick = {
					onSubmit(nodeNo, userId)
				},
				buttonShape = RoundedCornerShape(22.dp),
			)
		}

		Box(
			modifier = Modifier.fillMaxSize(),
			contentAlignment = Alignment.TopEnd,
		) {
			Image(
				modifier = Modifier
					.padding(top = 10.dp, end = 10.dp)
					.height(30.dp),
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
