package com.nextuple.nsf.ui.screen.prep

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.BuildConfig
import com.nextuple.nsf.R
import com.nextuple.nsf.retrofit.dto.StageTask
import com.nextuple.nsf.retrofit.dto.StageTaskContainer
import com.nextuple.nsf.retrofit.dto.Status
import com.nextuple.nsf.ui.common.HorizontalProgressBar
import com.nextuple.nsf.ui.common.ImageList
import com.nextuple.nsf.ui.common.PrimaryButton
import com.nextuple.nsf.ui.common.SecondaryButton
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.ui.util.NoOpScanManager
import com.nextuple.nsf.ui.util.PreviewPdt
import com.nextuple.nsf.ui.util.ScanManager

@Composable
fun StageDetailsScreen(
	progressBarBackgroundColor: Color,
	holdLocationState: GenericViewState = GenericViewState.Idle,
	storeOverviewState: GenericViewState = GenericViewState.Loading,
	scanManager: ScanManager,
	athlete: String?,
	orderNum: String?,
	stageTask: StageTask?,
	onRecordHoldingLocation: (containerId: Long, holdingLocation: String) -> Unit,
	onStageCompletionCallBack: () -> Unit,
	onReprintHoldSlip: () -> Unit
) {
	val totalSteps by remember { mutableStateOf(2) }

	val isTaskCompleted = stageTask?.status?.name == "COMPLETED"
	val progressTitle = if (isTaskCompleted) stringResource(id = R.string.complete) else stringResource(id = R.string.step_2_stage)
	val unitsCompleted = if (isTaskCompleted) 2 else 1

	LaunchedEffect(Unit) {
		scanManager.set { data, _ ->
			val container = stageTask?.containers?.firstOrNull {
				it.holdingLocation.isNullOrEmpty()
			} ?: return@set

			onRecordHoldingLocation(container.id, data)
		}
	}

	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(color = BrandColor.GRAY_50)
	) {
		Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.background(progressBarBackgroundColor)
					.padding(start = 20.dp)
			) {
				HorizontalProgressBar(
					modifier = Modifier.padding(end = 129.dp),
					unitsCompleted = unitsCompleted,
					totalUnits = totalSteps,
					title = progressTitle,
					textColor = BrandColor.GRAY_50,
					isCustomTitle = true
				)

				Spacer(modifier = Modifier.height(12.dp))
			}

			Header(athlete, orderNum)
			Title()
			val containerSize = stageTask?.containers?.size ?: 0
			stageTask?.containers?.forEachIndexed { index, container ->
				Row(
					modifier = Modifier
						.padding(start = 30.dp, top = 10.dp, end = 30.dp)
						.background(BrandColor.GRAY_100)
						.height(100.dp)
						.fillMaxWidth()
						.clip(
							RoundedCornerShape(12.dp)
						)
				) {
					val imageList = container.packedItems.map {
						it.productImageUrls.firstOrNull() ?: ""
					}.filter {
						it.isNotEmpty()
					}
					val totalUnits = container.packedItems.sumOf { it.qty }
					ItemDetails(
						weight = 0.58f,
						currentIndex = index.plus(1),
						containerSize = containerSize,
						units = totalUnits,
						images = imageList
					)

					if (container.holdingLocation != null) {
						Column(
							modifier = Modifier
								.height(100.dp)
								.weight(0.42f)
								.background(BrandColor.GREEN_500),
							horizontalAlignment = Alignment.CenterHorizontally
						) {
							Image(
								modifier = Modifier
									.padding(start = 54.dp, top = 23.dp, end = 54.dp)
									.height(18.dp)
									.width(24.dp),
								painter = painterResource(id = R.drawable.ic_check_white),
								contentDescription = "Scan Done"
							)
							/*
							Text(
								modifier = Modifier.padding(top = 3.dp),
								text = "Bin ${container.packedItems.size}",
								fontSize = 16.sp,
								fontFamily = FontFamily.ARCHIVO,
								fontWeight = FontWeight.Bold,
								letterSpacing = 0.5.sp,
								color = DsgColor.GRAY_50
							)*/
							Text(
								modifier = Modifier.padding(start = 3.dp, end = 3.dp),
								text = container.holdingLocation,
								fontSize = 12.sp,
								fontFamily = FontFamily.ARCHIVO,
								fontWeight = FontWeight.Normal,
								letterSpacing = 0.5.sp,
								color = BrandColor.GRAY_50,
								textAlign = TextAlign.Center
							)
						}
					} else {
						Column(
							modifier = Modifier
								.height(100.dp)
								.weight(0.42f)
								.background(BrandColor.ORANGE_600)
								.clickable {
									if (BuildConfig.DEBUG && BuildConfig.FLAVOR.lowercase() != "prod") {
										onRecordHoldingLocation(
											container.id,
											"Bin 1 - Main Holding Area"
										)
									}
								},
							horizontalAlignment = Alignment.CenterHorizontally,
							verticalArrangement = Arrangement.Center
						) {
							Image(
								modifier = Modifier
									.padding(30.dp)
									.height(30.dp)
									.width(30.dp),
								painter = painterResource(id = R.drawable.ic_scan_white),
								contentDescription = "My Image"
							)
						}
					}
				}
			}

			PrimaryButton(
				modifier = Modifier
					.fillMaxWidth(0.7f)
					.padding(top = 32.dp)
					.align(Alignment.CenterHorizontally),
				text = stringResource(id = R.string.ok),
				enabled = isTaskCompleted,
				onButtonClick = {
					onStageCompletionCallBack.invoke()
				}
			)

			Spacer(modifier = Modifier.height(8.dp))

			SecondaryButton(
				modifier = Modifier
					.fillMaxWidth(0.7f)
					.align(Alignment.CenterHorizontally),
				text = stringResource(id = R.string.reprint_hold_slip),
				onButtonClick = {
					onReprintHoldSlip()
				}
			)
		}
		if (storeOverviewState == GenericViewState.Loading || holdLocationState == GenericViewState.Loading) {
			CircularProgressIndicator(
				modifier = Modifier.align(Alignment.Center),
				color = BrandColor.GRAY_900
			)
		}
	}
}

@Composable
private fun Header(athlete: String?, orderNum: String?) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.background(BrandColor.GRAY_200)
	) {
		Column(
			modifier = Modifier
				.padding(top = 10.dp, start = 30.dp, bottom = 3.dp)
				.fillMaxWidth(.6f)
		) {
			Text(
				text = stringResource(id = R.string.athlete),
				fontFamily = FontFamily.ARCHIVO,
				fontWeight = FontWeight.Bold,
				fontSize = 10.sp,
				letterSpacing = 1.5.sp
			)
			Text(
				text = athlete ?: "",
				fontFamily = FontFamily.ARCHIVO,
				fontWeight = FontWeight.Normal,
				fontSize = 16.sp,
				letterSpacing = 0.5.sp
			)
		}

		Spacer(modifier = Modifier.weight(1f))

		Column(modifier = Modifier.padding(end = 30.dp, top = 10.dp, bottom = 10.dp)) {
			Text(
				text = stringResource(id = R.string.order_num),
				fontFamily = FontFamily.ARCHIVO,
				fontWeight = FontWeight.Bold,
				fontSize = 10.sp,
				letterSpacing = 1.5.sp
			)
			Text(
				text = orderNum ?: "",
				fontFamily = FontFamily.ARCHIVO,
				fontWeight = FontWeight.Normal,
				fontSize = 16.sp,
				letterSpacing = 0.5.sp
			)
		}
	}
}

@Composable
private fun Title() {
	Text(
		modifier = Modifier.padding(top = 4.dp, start = 24.dp),
		text = stringResource(id = R.string.scan_holding_location),
		fontFamily = FontFamily.ARCHIVO,
		fontWeight = FontWeight.Bold,
		fontSize = 20.sp,
		letterSpacing = 0.5.sp
	)
}

@Composable
private fun RowScope.ItemDetails(
	weight: Float,
	currentIndex: Int?,
	containerSize: Int?,
	units: Int?,
	images: List<String>?
) {
	Column(
		modifier = Modifier
			.weight(weight)
			.height(100.dp)
	) {
		Text(
			modifier = Modifier.padding(start = 11.dp, top = 6.dp),
			text = "Package $currentIndex/$containerSize",
			fontSize = 16.sp,
			fontFamily = FontFamily.ARCHIVO,
			fontWeight = FontWeight.Normal,
			letterSpacing = 0.5.sp
		)
		Text(
			modifier = Modifier.padding(start = 11.dp),
			text = if (units == 1) "$units unit" else "$units units",
			fontSize = 12.sp,
			fontFamily = FontFamily.ARCHIVO,
			fontWeight = FontWeight.Bold,
			letterSpacing = 0.5.sp
		)
		images?.let { ImageList(it) } ?: run {
			Image(
				modifier = Modifier
					.size(48.dp)
					.padding(8.dp),
				painter = painterResource(id = R.drawable.placeholder_image),
				contentDescription = "placeholder"
			)
		}
	}
}

@PreviewPdt
@Composable
fun StageOrderPreview() {
	StageDetailsScreen(
		progressBarBackgroundColor = BrandColor.GREEN_900,
		athlete = "Heisey, A.",
		orderNum = "000000000001",
		scanManager = NoOpScanManager(),
		stageTask = StageTask(
			id = 12,
			status = Status(code = "12", name = "COMPLETED"),
			fulfillmentRequestNumber = "123",
			containers = listOf(
				StageTaskContainer(id = 12, holdingLocation = "Hyderabad")
			)
		),
		onRecordHoldingLocation = { _, _ -> },
		onStageCompletionCallBack = {},
		onReprintHoldSlip = {}
	)
}
