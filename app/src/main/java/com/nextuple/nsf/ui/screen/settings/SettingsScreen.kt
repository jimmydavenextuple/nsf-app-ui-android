package com.nextuple.nsf.ui.screen.settings

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColor
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.common.BackButton
import com.nextuple.nsf.ui.common.ButtonState
import com.nextuple.nsf.ui.common.InfoModal
import com.nextuple.nsf.ui.common.MultiOptionModal
import com.nextuple.nsf.ui.common.TertiaryButton
import com.nextuple.nsf.ui.common.TopLabeledTextField
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.ui.util.PreviewPdt

@Composable
fun SettingsScreen(
	printersList: List<Printer>?,
	printerConnectionState: GenericViewState = GenericViewState.Idle,
	ipPrefix: String?,
	onConnectPrinter: (Printer, String) -> Unit,
	onDisConnectPrinter: (Printer) -> Unit,
	onBackButtonClick: () -> Unit,
	onReset: () -> Unit
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding()
			.background(BrandColor.GRAY_100)
	) {
		//Header
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.background(color = BrandColor.GRAY_100)
				.padding(start = 20.dp)
				.height(40.dp)
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically
			) {
				BackButton(
					modifier = Modifier.fillMaxHeight().padding(top = 10.dp, bottom = 10.dp),
					onBackButtonClick = onBackButtonClick
				)

			}
		}
		Card(
			modifier = Modifier
				.padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 8.dp)
				.fillMaxWidth()
				.wrapContentHeight(),
			shape = RoundedCornerShape(12.dp),
			colors = CardDefaults.cardColors(containerColor = BrandColor.GRAY_50),
			elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
		) {
			Column(modifier = Modifier.padding(12.dp)) {
				Text(
					text = stringResource(id = R.string.printers_title),
					style = TextStyle(
						fontWeight = FontWeight.Bold,
						fontFamily = FontFamily.ARCHIVO,
						fontSize = 24.sp,
						color = BrandColor.BLUE_800_NT
					)
				)
				printersList?.forEach { printer ->
					PrinterListItem(printer, ipPrefix, onReset, printerConnectionState, onConnectPrinter, onDisConnectPrinter)
				}
				PrinterProblem(
					modifier = Modifier.padding(top = 22.dp, bottom = 10.dp),
					onButtonClick = {}
				)
			}
		}
	}
}

@Composable
private fun PrinterListItem(printer: Printer, ipPrefix: String?, onReset: () -> Unit, printerConnectionState: GenericViewState, onConnectPrinter: (Printer, String) -> Unit, onDisConnectPrinter: (Printer) -> Unit) {
	var showConnectModal by remember { mutableStateOf(false) }
	var showDisconnectModal by remember { mutableStateOf(false) }
	var connectModalButtonState by remember { mutableStateOf(ButtonState.DEFAULT) }
	var isConnectModalButtonEnabled by remember { mutableStateOf(false) }
	var ipInput by remember { mutableStateOf("") }

	fun resetDialogData() {
		connectModalButtonState = ButtonState.DEFAULT
		isConnectModalButtonEnabled = false
		ipInput = ""
	}

	Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
		Text(
			modifier = Modifier.weight(0.3f),
			text = printer.printerName,
			style = TextStyle(
				fontWeight = FontWeight.Normal,
				fontFamily = FontFamily.ARCHIVO,
				fontSize = 16.sp,
				color = BrandColor.BLACK
			)
		)
		Card(
			modifier = Modifier
				.weight(0.7f)
				.fillMaxWidth()
				.wrapContentHeight(),
			shape = RoundedCornerShape(12.dp),
			colors = CardDefaults.cardColors(containerColor = if (printer.connectionStatus) BrandColor.BLUE_100_NT else BrandColor.GRAY_100)
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(start = 8.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Image(
					modifier = Modifier.weight(0.2f),
					painter = painterResource(
						id = getPrinterIcon(printer.connectionStatus)
					),
					contentDescription = "Printer Image"
				)
				Column(
					modifier = Modifier
						.weight(0.6f)
						.padding(start = 4.dp, end = 4.dp)
				) {
					Text(
						modifier = Modifier,
						text = stringResource(id = R.string.printer_ip_caps),
						maxLines = 1,
						style = TextStyle(
							fontWeight = FontWeight.Bold,
							fontFamily = FontFamily.ARCHIVO,
							fontSize = 12.sp,
							color = BrandColor.BLACK,
							letterSpacing = 1.5.sp
						)
					)
					if (printer.connectionStatus) {
						Text(
							text = printer.ipAddress,
							maxLines = 1,
							style = TextStyle(
								fontWeight = FontWeight.Normal,
								fontFamily = FontFamily.ARCHIVO,
								fontStyle = FontStyle.Normal,
								fontSize = 12.sp,
								color = BrandColor.BLACK,
								letterSpacing = 0.5.sp
							)
						)
					}
					Text(
						text = if (printer.connectionStatus) {
							stringResource(id = R.string.printer_connected)
						} else {
							stringResource(
								id = R.string.printer_unconnected
							)
						},
						maxLines = 1,
						style = TextStyle(
							fontWeight = FontWeight.Normal,
							fontFamily = FontFamily.ARCHIVO,
							fontStyle = FontStyle.Italic,
							fontSize = 12.sp,
							color = BrandColor.BLACK,
							letterSpacing = 0.5.sp
						)
					)
				}

				Image(
					modifier = Modifier
						.weight(0.2f)
						.clickable {
							if (!printer.connectionStatus) {
								showConnectModal = true
							} else {
								showDisconnectModal = true
							}
						},
					painter = painterResource(id = getPrinterConnectIcon(printer.connectionStatus)),
					contentDescription = "connect printer"
				)
			}
		}
	}

	if (showConnectModal) {
		InfoModal(
			title = stringResource(id = R.string.connect_printer),
			subTitle = stringResource(id = R.string.connect_printer_subtitle),
			buttonText = stringResource(id = R.string.connect),
			buttonState = connectModalButtonState,
			isButtonEnabled = isConnectModalButtonEnabled,
			showCheckBox = false,
			visualContent = {
				TopLabeledTextField(
					modifier = Modifier.fillMaxWidth(0.5f),
					labelText = stringResource(id = R.string.printer_ip),
					fieldValue = ipInput,
					prefixText = ipPrefix ?: "",
					isPrefixEnabled = true,
					onValueChange = {
						ipInput = it
						isConnectModalButtonEnabled = isValidIPv4Address(ipPrefix + ipInput)
					}
				)

				Row(
					modifier = Modifier,
					horizontalArrangement = Arrangement.Center,
					verticalAlignment = Alignment.CenterVertically
				) {
					Icon(
						modifier = Modifier
							.padding(end = 8.dp)
							.size(width = 16.dp, height = 8.dp),
						painter = painterResource(id = R.drawable.ic_arrow_drop_down),
						contentDescription = "drop down arrow"
					)
					Spacer(modifier = Modifier.height(32.dp))
					TertiaryButton(
						text = stringResource(id = R.string.cant_find_the_ip),
						onButtonClick = { }
					)
				}
			},
			buttonClick = {
				if (connectModalButtonState == ButtonState.DEFAULT) {
					onConnectPrinter(printer, ipPrefix + ipInput)
				}
			},
			crossIconClick = {
				showConnectModal = false
				resetDialogData()
			},
			onDismissRequest = {
				showConnectModal = false
				resetDialogData()
			}
		)
	}

	when (printerConnectionState) {
		GenericViewState.Loading -> {
			connectModalButtonState = ButtonState.LOADING
		}

		GenericViewState.Success -> {
			connectModalButtonState = ButtonState.DONE
			Handler(Looper.getMainLooper()).postDelayed({
				onReset.invoke()
				showConnectModal = false
				resetDialogData()
			}, 1000)
		}

		else -> {
			connectModalButtonState = ButtonState.DEFAULT
		}
	}

	if (showDisconnectModal) {
		val yesOption = stringResource(id = R.string.yes)
		MultiOptionModal(
			title = stringResource(id = R.string.remove_printer),
			subTitle = stringResource(R.string.remove_printer_subtitle, printer.printerName),
			buttons = listOf(
				stringResource(id = R.string.yes),
				stringResource(id = R.string.no)
			),
			buttonClick = { optionSelected ->
				showDisconnectModal = false
				if (optionSelected == yesOption) {
					onDisConnectPrinter(printer)
				}
			},
			crossIconClick = { showDisconnectModal = false }
		) {
		}
	}
}

@Composable
private fun PrinterProblem(
	modifier: Modifier = Modifier,
	onButtonClick: () -> Unit
) {
	Row(
		modifier = modifier,
		verticalAlignment = Alignment.CenterVertically
	) {
		Icon(
			modifier = Modifier
				.padding()
				.clickable { onButtonClick() },
			painter = painterResource(id = R.drawable.ic_info),
			contentDescription = "back button",
			tint = BrandColor.BLUE_300_NT
		)

		TertiaryButton(text = stringResource(id = R.string.printer_problems)) {
			onButtonClick()
		}
	}
}

fun isValidIPv4Address(input: String): Boolean {
	val pattern = Regex("^((25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)$")
	return pattern.matches(input)
}

private fun getPrinterIcon(isConnected: Boolean): Int {
	return if (isConnected) R.drawable.ic_printer_connected else R.drawable.ic_printer_normal
}

private fun getPrinterConnectIcon(isConnected: Boolean): Int {
	return if (isConnected) R.drawable.ic_close else R.drawable.ic_plus_orange
}

data class Printer(val printerName: String, val ipAddress: String, var connectionStatus: Boolean)

@Composable
@PreviewPdt
fun PreviewPrepScreen() {
	SettingsScreen(
		printersList = listOf(
			Printer(
				printerName = "SFS",
				ipAddress = stringResource(id = R.string.printer_ip_caps),
				connectionStatus = true
			),
			Printer(
				printerName = "BOPIS",
				ipAddress = stringResource(id = R.string.printer_ip_caps),
				connectionStatus = false
			),
			Printer(
				printerName = "SDD",
				ipAddress = stringResource(id = R.string.printer_ip_caps),
				connectionStatus = false
			)
		),
		ipPrefix = "",
		onReset = {},
		onConnectPrinter = { _, _ -> },
		onDisConnectPrinter = {},
		onBackButtonClick = {}
	)
}
