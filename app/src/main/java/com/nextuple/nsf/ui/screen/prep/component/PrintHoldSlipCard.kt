package com.nextuple.nsf.ui.screen.prep.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.R
import com.nextuple.nsf.retrofit.dto.PackTaskItem
import com.nextuple.nsf.ui.common.PrimaryButton
import com.nextuple.nsf.ui.component.ExpandableStepCard
import com.nextuple.nsf.ui.screen.settings.getPrinterConnectIcon
import com.nextuple.nsf.ui.screen.settings.getPrinterIcon
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily
import com.nextuple.nsf.ui.util.Printer

@Composable
fun PrintHoldSlipCard(
	isActive: Boolean,
	printer: Printer,
	packItems: List<PackTaskItem>,
	isMultiUnit: Boolean,
	onPrintHoldSlipEvent: () -> Unit,
	showPrinterModalEvent: () -> Unit,
	onDisConnectPrinter: (Printer) -> Unit
) {
	@Composable
	fun ExtraContent() {
		Text(
			modifier = Modifier
				.padding(start = 60.dp, top = 12.dp),
			text = "Printer".uppercase(),
			style = TextStyle(
				fontSize = 14.sp,
				fontFamily = FontFamily.ARCHIVO,
				fontWeight = FontWeight(700),
				letterSpacing = 1.5.sp
			)
		)

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(top = 10.dp, bottom = 4.dp),
			horizontalArrangement = Arrangement.Center
		) {
			Card(
				modifier = Modifier
					.width(207.dp)
					.wrapContentHeight(),
				shape = RoundedCornerShape(4.dp),
				colors = CardDefaults.cardColors(containerColor = if (printer.connectionStatus) BrandColor.GREEN_50 else BrandColor.GRAY_100)
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
								"No Printer Selected"
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
					if (printer.connectionStatus) {
						Image(
							modifier = Modifier
								.padding(7.dp)
								.clickable {
									onDisConnectPrinter(printer)
								},
							painter = painterResource(id = getPrinterConnectIcon(printer.connectionStatus)),
							contentDescription = "connect printer"
						)
					}
				}
			}
		}

		Row(
			modifier = Modifier
				.fillMaxWidth(),
			horizontalArrangement = Arrangement.Center
		) {
			PrimaryButton(
				modifier = Modifier
					.fillMaxWidth(.65f)
					.padding(vertical = 15.dp),
				text = if (printer.connectionStatus && !isMultiUnit) {
					stringResource(id = R.string.print_hold_slip)
				} else if (printer.connectionStatus) {
					stringResource(
						R.string.print_all
					)
				} else {
					"Select Printer".uppercase()
				},
				onButtonClick = {
					if (printer.connectionStatus) {
						onPrintHoldSlipEvent()
					} else {
						showPrinterModalEvent()
					}
				}
			)
		}
	}

	if (packItems.size <= 1) {
		ExpandableStepCard(
			stepNumber = "1",
			title = stringResource(R.string.bopl_prep_1),
			isActive = isActive,
			isComplete = !isActive,
			extraContent = { ExtraContent() }
		)
	} else {
		ExpandableStepCard(
			stepNumber = "1",
			title = null,
			annotatedTitle = if (isActive) {
				buildAnnotatedString {
					append("Print ")
					withStyle(style = SpanStyle(color = BrandColor.ORANGE_700)) {
						append(packItems.size.toString())
					}
					append(" Hold Slips")
				}
			} else {
				buildAnnotatedString {
					append("Print ")
					append(packItems.size.toString())
					append(" Hold Slips")
				}
			},
			isActive = isActive,
			isComplete = !isActive,
			extraContent = { ExtraContent() }
		)
	}
}

@Preview
@Composable
private fun PrintHoldSlipCardPreview() {
	PrintHoldSlipCard(
		isActive = true,
		printer = Printer("", ""),
		packItems = listOf(),
		isMultiUnit = false,
		onPrintHoldSlipEvent = { },
		showPrinterModalEvent = { },
		onDisConnectPrinter = { }
	)
}
