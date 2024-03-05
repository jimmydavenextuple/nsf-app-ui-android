package com.nextuple.nsf.ui.screen.order

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.nextuple.nsf.R.string
import com.nextuple.nsf.ui.common.ActionsCard
import com.nextuple.nsf.ui.common.ActionsCardAction
import com.nextuple.nsf.ui.state.Printer
import com.nextuple.nsf.util.OrderStatus
import com.nextuple.nsf.util.OrderStatus.PACK
import com.nextuple.nsf.util.OrderStatus.READY

@Composable
fun OrderManagementCard(
	modifier: Modifier = Modifier,
	orderStatus: OrderStatus,
	printer: Printer,
	showCancelButton: Boolean,
	onPrintHoldSlipEvent: () -> Unit,
	showPrinterModalEvent: () -> Unit,
	showCancelConfirmationModalEvent: () -> Unit,
	showEditLocationModalEvent: () -> Unit,
	onPackOrder: () -> Unit
) {
	val (primaryAction, secondaryActions) = when {
		orderStatus == PACK -> {
			ActionsCardAction(
				label = stringResource(id = string.pack_order),
				isEnabled = true,
				onClick = onPackOrder
			) to emptyList()
		}

		OrderStatus.isReadyStatus(orderStatus) -> {
			null to mutableListOf(
				ActionsCardAction(
					label = stringResource(id = string.print_hold_slip),
					isEnabled = true,
					onClick = {
						if (printer.connectionStatus) {
							onPrintHoldSlipEvent()
						} else {
							showPrinterModalEvent()
						}
					}
				)
			).apply {
				if (showCancelButton) {
					add(
						ActionsCardAction(
							label = stringResource(id = string.cancel_order),
							isEnabled = true,
							onClick = showCancelConfirmationModalEvent
						)
					)
				}
				add(
					ActionsCardAction(
						label = stringResource(string.edit_hold_location),
						isEnabled = true,
						onClick = showEditLocationModalEvent
					)
				)
			}
		}

		else -> null to emptyList<ActionsCardAction>()
	}

	if (primaryAction != null || secondaryActions.isNotEmpty()) {
		ActionsCard(
			modifier = modifier,
			title = stringResource(id = string.order_management),
			primaryAction = primaryAction,
			secondaryActions = secondaryActions
		)
	}
}

@Composable
@Preview
fun PreviewOrderManagementCard() {
	OrderManagementCard(
		orderStatus = READY,
		printer = Printer(printerName = "CVS Receipt Printer"),
		showCancelButton = true,
		onPrintHoldSlipEvent = {},
		showPrinterModalEvent = {},
		showCancelConfirmationModalEvent = {},
		showEditLocationModalEvent = {}
	) {}
}

@Composable
@Preview
fun PreviewOrderManagementCardStatusPack() {
	OrderManagementCard(
		orderStatus = PACK,
		printer = Printer(printerName = "CVS Receipt Printer"),
		showCancelButton = true,
		onPrintHoldSlipEvent = {},
		showPrinterModalEvent = {},
		showCancelConfirmationModalEvent = {},
		showEditLocationModalEvent = {}
	) {}
}
