package com.nextuple.nsf.ui.screen.prep

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.R
import com.nextuple.nsf.retrofit.dto.response.StoreOverviewResponse
import com.nextuple.nsf.retrofit.dto.response.prepTaskAthleteShortName
import com.nextuple.nsf.ui.common.ImageList
import com.nextuple.nsf.ui.common.InfoModal
import com.nextuple.nsf.ui.common.PrimaryButton
import com.nextuple.nsf.ui.state.AppViewModel
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.ui.util.PreviewPdt

@Composable
fun PrepScreen(
	storeOverViewState: GenericViewState = GenericViewState.Loading,
	startTaskStatus: GenericViewState = GenericViewState.Idle,
	currentPrepStage: AppViewModel.PrepStage,
	navigateToStage2: () -> Unit,
	navigateToStage1: () -> Unit,
	prepTasks: List<StoreOverviewResponse.PrepTask>? = null,
	onStartPack: (String) -> Unit,
	startTaskCompletion: () -> Unit = {},
	onBenchActionCallback: () -> Unit = {}
) {
	LaunchedEffect(Unit) {
		when (currentPrepStage) {
			AppViewModel.PrepStage.Stage2 -> navigateToStage2.invoke()
			AppViewModel.PrepStage.Stage1 -> navigateToStage1.invoke()
			AppViewModel.PrepStage.Landing -> Unit
		}
	}
	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(color = BrandColor.GRAY_50)
	) {
		prepTasks?.ifEmpty { null }?.let {
			PrepList(preItems = it, onStartPack)
		} ?: run {
			EmptyPrepList()
		}
		if (startTaskStatus == GenericViewState.Success) {
			startTaskCompletion.invoke()
		} else if (startTaskStatus == GenericViewState.Loading || storeOverViewState == GenericViewState.Loading) {
			CircularProgressIndicator(
				modifier = Modifier.align(Alignment.Center),
				color = BrandColor.GRAY_900
			)
		} else if (startTaskStatus == GenericViewState.Failure) {
			InfoModal(
				modifier = Modifier.fillMaxWidth(0.95f),
				title = stringResource(id = R.string.info_modal_prep_on_the_bench_title),
				subTitle = stringResource(id = R.string.info_modal_prep_on_the_bench_message),
				buttonText = stringResource(id = R.string.ok),
				buttonClick = {
					onBenchActionCallback()
				},
				crossIconClick = {
					onBenchActionCallback()
				},
				dismissOnBackPress = false,
				dismissOnClickOutside = false,
				onDismissRequest = { }
			)
		}
	}
}

@Composable
fun PrepList(preItems: List<StoreOverviewResponse.PrepTask>, onStartPack: (String) -> Unit) {
	LazyColumn(modifier = Modifier.padding(top = 24.dp)) {
		items(items = preItems) {
			PrepListItem(it, onStartPack)
		}
	}
}

@Composable
fun PrepListItem(prepTask: StoreOverviewResponse.PrepTask, onStartPack: (String) -> Unit) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.wrapContentHeight()
			.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
		shape = RoundedCornerShape(12.dp),
		colors = CardDefaults.cardColors(containerColor = BrandColor.GRAY_100),
		elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.wrapContentHeight()
				.padding(12.dp),
			contentAlignment = Alignment.Center
		) {
			Column(modifier = Modifier.align(Alignment.CenterStart)) {
				Text(
					text = "${prepTask.prepTaskAthleteShortName()}",
					style = TextStyle(
						fontWeight = FontWeight.Bold,
						fontFamily = FontFamily.ARCHIVO,
						fontSize = 24.sp,
						color = BrandColor.GRAY_900
					)
				)
				Spacer(modifier = Modifier.height(12.dp))
				Text(
					text = stringResource(id = R.string.order_number_text).uppercase(),
					style = TextStyle(
						fontWeight = FontWeight.Bold,
						fontFamily = FontFamily.ARCHIVO,
						fontSize = 12.sp,
						color = BrandColor.BLACK,
						letterSpacing = 1.5.sp
					)
				)
				Text(
					text = prepTask.orderNumber,
					style = TextStyle(
						fontWeight = FontWeight.Normal,
						fontFamily = FontFamily.ARCHIVO,
						fontSize = 14.sp,
						color = BrandColor.BLACK,
						letterSpacing = 0.5.sp
					)
				)
				Spacer(modifier = Modifier.height(4.dp))
				val imageList = prepTask.items.map {
					it.productImageUrls.firstOrNull() ?: ""
				}.filter {
					it.isNotEmpty()
				}
				ImageList(imageList, defaultImagesSize = prepTask.items.size)
			}
			PrimaryButton(
				modifier = Modifier
					.wrapContentWidth()
					.align(Alignment.CenterEnd),
				text = stringResource(id = R.string.pack).uppercase(),
				enabled = true, // TODO: change later based on backend response data
				onButtonClick = { onStartPack(prepTask.id.toString()) }
			)
		}
	}
}

@Composable
fun EmptyPrepList() {
	Card(
		modifier = Modifier
			.fillMaxSize()
			.padding(24.dp),
		shape = RoundedCornerShape(12.dp),
		colors = CardDefaults.cardColors(containerColor = BrandColor.GRAY_100)
	) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(8.dp),
			contentAlignment = Alignment.Center
		) {
			Column {
				Text(
					modifier = Modifier.align(Alignment.CenterHorizontally),
					text = stringResource(id = R.string.prep_empty_orders_title),
					style = TextStyle(
						fontWeight = FontWeight.Bold,
						fontFamily = FontFamily.ARCHIVO,
						fontSize = 20.sp,
						color = BrandColor.BLACK
					)
				)
				Spacer(modifier = Modifier.height(12.dp))
				Text(
					modifier = Modifier.align(Alignment.CenterHorizontally),
					text = stringResource(id = R.string.prep_empty_orders_description),
					style = TextStyle(
						fontWeight = FontWeight.Medium,
						fontFamily = FontFamily.ARCHIVO,
						fontSize = 16.sp,
						color = BrandColor.BLACK,
						letterSpacing = 0.12.sp,
						lineHeight = 18.sp
					)
				)
			}
		}
	}
}

@Composable
@PreviewPdt
fun PreviewPrepScreen() {
	PrepScreen(
		storeOverViewState = GenericViewState.Loading,
		currentPrepStage = AppViewModel.PrepStage.Landing,
		navigateToStage1 = {},
		navigateToStage2 = {},
		onStartPack = { }
	)
}
