package com.nextuple.nsf.ui.component

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.common.ButtonState
import com.nextuple.nsf.ui.common.InfoModal
import com.nextuple.nsf.ui.common.TopLabeledTextField
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.ui.util.Printer
import com.nextuple.nsf.util.StringUtils.isValidIPv4Address

@Composable
fun PrinterModal(
	ipPrefix: String?,
	printer: Printer,
	onReset: () -> Unit,
	printerConnectionState: GenericViewState,
	toggleModal: (Boolean) -> Unit,
	onConnectPrinter: (Printer, String) -> Unit
) {
	var connectModalButtonState by remember { mutableStateOf(ButtonState.DEFAULT) }
	var isConnectModalButtonEnabled by remember { mutableStateOf(false) }
	var ipInput by remember { mutableStateOf("") }

	val resetDialogData: () -> Unit = {
		connectModalButtonState = ButtonState.DEFAULT
		isConnectModalButtonEnabled = false
		ipInput = ""
		toggleModal(false)
	}

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
				isInvalid = printerConnectionState == GenericViewState.Failure,
				errorMessage = "Printer not found",
				errorFillColor = BrandColor.RED_100,
				onValueChange = {
					ipInput = it
					isConnectModalButtonEnabled = isValidIPv4Address(ipPrefix + ipInput)
				}
			)
		},
		buttonClick = {
			if (connectModalButtonState == ButtonState.DEFAULT) {
				onConnectPrinter(printer, ipPrefix + ipInput)
			}
		},
		crossIconClick = {
			resetDialogData()
		},
		onDismissRequest = {
			resetDialogData()
		}
	)

	when (printerConnectionState) {
		GenericViewState.Loading -> {
			connectModalButtonState = ButtonState.LOADING
		}

		GenericViewState.Success -> {
			connectModalButtonState = ButtonState.DONE
			Handler(Looper.getMainLooper()).postDelayed({
				onReset.invoke()
				resetDialogData()
			}, 1000)
		}

		else -> {
			connectModalButtonState = ButtonState.DEFAULT
		}
	}
}
