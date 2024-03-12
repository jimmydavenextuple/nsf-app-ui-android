package com.nextuple.nsf.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.nextuple.nsf.ui.common.InfoModal
import com.nextuple.nsf.ui.common.TopLabeledTextField

@Composable
fun EnterUpcDialog(
	onDismissRequest: () -> Unit,
	onUpcSubmit: (upc: String) -> Unit
) {
	var upc by remember { mutableStateOf("") }
	InfoModal(
		title = "Enter A UPC",
		buttonText = "Scan",
		buttonClick = { onUpcSubmit(upc) },
		crossIconClick = { onDismissRequest() },
		onDismissRequest = { onDismissRequest() },
		visualContent = {
			TopLabeledTextField(
				labelText = "",
				fieldValue = upc,
				onValueChange = { newValue -> upc = newValue }
			)
		}
	)
}

@Preview(showBackground = true)
@Composable
fun EnterUpcDialogPreview() {
	EnterUpcDialog(
		onDismissRequest = {},
		onUpcSubmit = { _ -> }
	)
}
