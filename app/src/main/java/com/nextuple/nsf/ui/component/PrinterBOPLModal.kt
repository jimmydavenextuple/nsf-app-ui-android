package com.nextuple.nsf.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.common.PrimaryButton
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.util.Printer

@Composable
fun PrinterBOPLModal(
	printersList: List<Printer>?,
	boplPrinter: Printer,
	connectPrinter: (printer: Printer, ipAddress: String) -> Unit,
	otherPrinterOnClick: () -> Unit,
	onDismissRequest: () -> Unit
) {
	val selectablePrinters = printersList.orEmpty().filter { printer -> printer.connectionStatus }
	if (selectablePrinters.isNotEmpty()) {
		val (selectedOption, onOptionSelected) = remember { mutableStateOf(selectablePrinters[0]) }

		Card(
			modifier = Modifier
				.wrapContentSize()
				.background(BrandColor.GRAY_50)
		) {
			Dialog(
				onDismissRequest = { onDismissRequest() },
				properties = DialogProperties(
					dismissOnClickOutside = true
				)
			) {
				Surface {
					Column {
						Row(
							modifier = Modifier
								.fillMaxWidth()
								.padding(vertical = 10.dp, horizontal = 20.dp),
							horizontalArrangement = Arrangement.SpaceBetween,
							verticalAlignment = Alignment.CenterVertically
						) {
							Text(
								text = "Select Printer",
								style = TextStyle(
									fontSize = 20.sp,
									fontWeight = FontWeight.SemiBold,
									color = BrandColor.BLACK
								)
							)
							Icon(
								modifier = Modifier
									.align(Alignment.CenterVertically)
									.semantics { testTag = "InfoModalCloseButton" }
									.clickable { onDismissRequest() },
								imageVector = ImageVector.vectorResource(R.drawable.ic_close),
								tint = BrandColor.GRAY_900,
								contentDescription = "close"
							)
						}
						selectablePrinters.forEach {
							RadioButtonRow(it, selectedOption, onOptionSelected)
						}
						Row(
							modifier = Modifier
								.fillMaxWidth()
								.padding(10.dp),
							horizontalArrangement = Arrangement.Center
						) {
							PrimaryButton(
								modifier = Modifier
									.fillMaxWidth()
									.semantics { testTag = "InfoModalButton" },
								text = stringResource(id = R.string.select),
								buttonColor = BrandColor.DARK_BLUE,
								onButtonClick = {
									connectPrinter(boplPrinter, selectedOption.ipAddress)
									onDismissRequest()
								},
								contentColor = BrandColor.GRAY_100
							)
						}
						Row(
							modifier = Modifier
								.fillMaxWidth()
								.padding(bottom = 20.dp),
							horizontalArrangement = Arrangement.Center
						) {
							ClickableText(
								text = AnnotatedString(stringResource(id = R.string.other_printer)),
								style = TextStyle(
									fontSize = 12.sp,
									fontWeight = FontWeight(700),
									color = BrandColor.BLACK,
									textAlign = TextAlign.Center,
									letterSpacing = 1.5.sp,
									textDecoration = TextDecoration.Underline
								),
								onClick = { otherPrinterOnClick() }
							)
						}
					}
				}
			}
		}
	} else {
		otherPrinterOnClick()
	}
}

@Composable
private fun RadioButtonRow(
	printer: Printer,
	selectedOption: Printer,
	onOptionSelected: (printer: Printer) -> Unit
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 10.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.Center
	) {
		RadioButton(
			selected = (printer.printerName == selectedOption.printerName),
			onClick = { onOptionSelected(printer) },
			colors = RadioButtonDefaults.colors(
				selectedColor = BrandColor.BLACK,
				unselectedColor = BrandColor.BLACK
			)
		)
		Text(
			modifier = Modifier.width(100.dp),
			text = "${printer.printerName} Printer",
			style = TextStyle(
				fontSize = 14.sp,
				fontWeight = FontWeight(400),
				color = BrandColor.BLACK
			)
		)
	}
}

@Preview(showBackground = true)
@Composable
fun PrinterBOPLPreview() {
	PrinterBOPLModal(
		printersList = listOf(
			Printer(
				printerName = "SFS",
				ipAddress = stringResource(id = R.string.printer_ip_caps),
				connectionStatus = true
			),
			Printer(
				printerName = "BOPIS",
				ipAddress = stringResource(id = R.string.printer_ip_caps),
				connectionStatus = true
			),
			Printer(
				printerName = "SDD",
				ipAddress = stringResource(id = R.string.printer_ip_caps),
				connectionStatus = false
			),
			Printer(
				printerName = "BOPL",
				ipAddress = stringResource(id = R.string.printer_ip_caps),
				connectionStatus = false
			)
		),
		onDismissRequest = {},
		connectPrinter = { _, _ -> },
		otherPrinterOnClick = {},
		boplPrinter = Printer("")
	)
}
