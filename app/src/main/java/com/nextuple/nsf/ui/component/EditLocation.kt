package com.nextuple.nsf.ui.component

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nextuple.nsf.BuildConfig
import com.nextuple.nsf.R
import com.nextuple.nsf.retrofit.dto.StageTaskContainer
import com.nextuple.nsf.ui.common.callToAction.DetailedCallToAction
import com.nextuple.nsf.ui.common.callToAction.DetailedCallToActionMode
import com.nextuple.nsf.ui.screen.prep.component.SelectHoldingAreaCard
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.ui.util.ScanManager
import kotlin.random.Random

@Composable
fun EditLocation(
	onDismissRequest: () -> Unit,
	scanLocationState: GenericViewState,
	onLocationChange: (Long, String) -> Unit,
	subfulfillmentType: String,
	scanManager: ScanManager?,
	containers: List<StageTaskContainer>?,
	holdingLocation: String?,
	holdingAreas: List<String>,
	selectHoldingActive: Boolean = true
) {
	val context = LocalContext.current
	var selectedHoldingArea by remember { mutableStateOf(holdingLocation ?: "") }
	var isSelectHoldingActive by remember { mutableStateOf(selectHoldingActive) }

	LaunchedEffect(Unit) {
		scanManager?.set { data, _ ->
			val container = containers?.firstOrNull()

			if (!data.lowercase().contains("bin")) {
				Toast.makeText(context, "", Toast.LENGTH_LONG).show()
				return@set
			}

			if (container != null && selectedHoldingArea != "") {
				val temp = "$selectedHoldingArea $data"
				onLocationChange(container.id, temp)
			}
		}
	}
	Dialog(
		onDismissRequest = {
			onDismissRequest()
		},
		properties = DialogProperties(
			dismissOnBackPress = true,
			dismissOnClickOutside = true,
			usePlatformDefaultWidth = false
		)
	) {
		Surface(
			modifier = Modifier
				.width(325.dp)
				.wrapContentHeight(),
			shape = RoundedCornerShape(4.dp),
			color = BrandColor.GRAY_100
		) {
			Column(
				modifier = Modifier
					.padding(8.dp)
					.wrapContentHeight()
			) {
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(4.dp),
					horizontalArrangement = Arrangement.SpaceBetween
				) {
					Text(
						text = "Edit Location",
						style = TextStyle(
							fontSize = 20.sp,
							fontWeight = FontWeight.Bold,
							letterSpacing = 0.5.sp
						)
					)
					Icon(
						modifier = Modifier
							.clickable { onDismissRequest() },
						imageVector = ImageVector.vectorResource(R.drawable.ic_close),
						tint = BrandColor.GRAY_900,
						contentDescription = "close"
					)
				}
				Row {
					SelectHoldingAreaCard(
						stepNumber = "1",
						isActive = isSelectHoldingActive,
						isComplete = !isSelectHoldingActive,
						holdingAreas = holdingAreas,
						selectedHoldingArea = selectedHoldingArea,
						onSelectedOptionTextChanged = { selectedHoldingArea = it },
						onSubmitHoldingArea = {
							if (subfulfillmentType != "BOPL") {
								isSelectHoldingActive = false
							} else {
								containers?.firstOrNull()?.id?.let {
									onLocationChange(
										it,
										selectedHoldingArea
									)
								}
								Handler(Looper.getMainLooper()).postDelayed({
									onDismissRequest()
								}, 500)
							}
						}
					)
				}

				Row {
					if (subfulfillmentType != "BOPL") {
						ExpandableStepCard(
							stepNumber = "2",
							title = stringResource(id = R.string.scan_location),
							isActive = !isSelectHoldingActive,
							extraContent = {
								when (scanLocationState) {
									GenericViewState.Success -> Handler(Looper.getMainLooper()).postDelayed(
										{
											onDismissRequest()
										},
										500
									)

									else -> {}
								}
								Column(
									modifier = Modifier
										.fillMaxWidth()
										.wrapContentHeight(),
									horizontalAlignment = Alignment.CenterHorizontally
								) {
									Image(
										modifier = Modifier
											.padding(top = 2.dp)
											.height(160.dp),
										imageVector = ImageVector.vectorResource(R.drawable.scan_location),
										contentDescription = "Scanning Bin"
									)
									DetailedCallToAction(
										modifier = Modifier
											.padding(top = 20.dp)
											.align(Alignment.CenterHorizontally),
										detailedCallToActionMode = when (scanLocationState) {
											is GenericViewState.Loading -> DetailedCallToActionMode.Loading()
											is GenericViewState.Success -> DetailedCallToActionMode.Done()
											else -> {
												DetailedCallToActionMode.Scan("")
											}
										},
										onClick = {
											if (BuildConfig.DEBUG && BuildConfig.FLAVOR.lowercase() != "prod") {
												val bin =
													"Bin ${Random.nextInt(from = 1, until = 100)}"

												containers?.firstOrNull()?.id?.let {
													onLocationChange(
														it,
														"$selectedHoldingArea $bin"
													)
												}
											}
										}
									)
								}
							}
						)
					}
				}
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
fun EditLocationModalPreview() {
	EditLocation(
		onDismissRequest = {},
		onLocationChange = { _, _ -> },
		scanManager = null,
		holdingLocation = "",
		containers = emptyList(),
		scanLocationState = GenericViewState.Idle,
		holdingAreas = emptyList(),
		subfulfillmentType = "BOPL"
	)
}

@Preview(showBackground = true)
@Composable
fun EditLocationModalPreview_ScanLocation() {
	EditLocation(
		onDismissRequest = {},
		onLocationChange = { _, _ -> },
		scanManager = null,
		holdingLocation = "",
		containers = emptyList(),
		scanLocationState = GenericViewState.Success,
		holdingAreas = emptyList(),
		subfulfillmentType = "BOPIS",
		selectHoldingActive = false
	)
}
