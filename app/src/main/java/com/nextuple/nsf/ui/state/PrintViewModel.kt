package com.nextuple.nsf.ui.state

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextuple.nsf.hilt.IoDispatcher
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.ui.util.PrintUtil.Companion.connectToSocket
import com.nextuple.nsf.ui.util.Printer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.PrintWriter
import javax.inject.Inject

@HiltViewModel
class PrintViewModel @Inject constructor(
	@Suppress("UNUSED_PARAMETER")
	handler: SavedStateHandle,
	@IoDispatcher
	private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

	companion object {
		const val TAG = "PrintViewModel"
		const val TAG_ZPL = "$TAG.pageZpl"
	}

	var holdSlipPrintState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	fun printHoldSlip(
        holdSlipZPL: MutableList<String>,
        printer: Printer,
        bypassPrinter: Boolean
	) = viewModelScope.launch {
		holdSlipPrintState = GenericViewState.Loading

		Log.i("$TAG.Data", "PrintHoldSlip - Printer: $printer, bypassPrinter: $bypassPrinter")

		runCatching {
			if (holdSlipZPL.isEmpty()) throw Exception()

			if (bypassPrinter) {
				holdSlipZPL.forEach { pageZPL ->
					Log.i(TAG_ZPL, "pageZPL = $pageZPL")
				}
			} else {
				withContext(ioDispatcher) {
					connectToSocket(printer.ipAddress)?.use { socket ->
						PrintWriter(socket.getOutputStream(), true).use {
							holdSlipZPL.forEach { pageZPL ->
								it.write(pageZPL)
							}
						}
					} ?: {
						Log.e(TAG, "Issue Connecting to Printer")
						holdSlipPrintState = GenericViewState.Failure
					}
				}
			}
		}.onSuccess {
			Log.i(TAG, "Successfully printed Hold Slip")
			holdSlipPrintState = GenericViewState.Success
		}.onFailure {
			Log.e(TAG, "Hold Slip not Printed: hold slip info not found", it)
			holdSlipPrintState = GenericViewState.Failure
		}
	}

	/**
	 * Removing existing holdSlipPrint state and default to default state.
	 */
	fun resetHoldSlipPrintState() {
		holdSlipPrintState = GenericViewState.Idle
	}
}
