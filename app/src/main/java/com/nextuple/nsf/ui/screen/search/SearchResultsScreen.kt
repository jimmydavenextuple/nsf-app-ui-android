package com.nextuple.nsf.ui.screen.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.R
import com.nextuple.nsf.retrofit.dto.response.OrderDetailsResponse
import com.nextuple.nsf.ui.common.OmniTextField
import com.nextuple.nsf.ui.screen.order.OrderCardList
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.ui.util.NoOpScanManager
import com.nextuple.nsf.ui.util.PreviewPdt
import com.nextuple.nsf.ui.util.ScanManager

@Composable
fun SearchResultsScreen(
	viewState: GenericViewState = GenericViewState.Loading,
	orderDetailsState: GenericViewState = GenericViewState.Idle,
	searchInput: String,
	getOrders: (String?) -> Unit,
	orderResults: List<OrderDetailsResponse>?,
	getOrderDetails: (String) -> Unit,
	getOrderDetailsCompletion: () -> Unit,
	scanManager: ScanManager = NoOpScanManager(),
	newSearch: (String) -> Unit
) {
	val focusManager = LocalFocusManager.current
	var searchInputText by remember { mutableStateOf(searchInput) }
	var lastSearch by remember { mutableStateOf("") }

	LaunchedEffect(Unit) {
		getOrders(searchInput)
		lastSearch = searchInput
	}

	LaunchedEffect(Unit) {
		scanManager.set { data, _ ->
			searchInputText = data
			getOrders(data)
			lastSearch = data
			newSearch(lastSearch)
		}
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.pointerInput(Unit) {
				detectTapGestures(
					onTap = {
						focusManager.clearFocus()
						searchInputText = lastSearch
					}
				)
			}
			.background(BrandColor.GRAY_100)
	) {
		SearchBar(
			modifier = Modifier.background(BrandColor.GRAY_50),
			searchInput = searchInputText,
			onValueChange = { searchInputText = it },
			getOrders = {
				lastSearch = searchInputText
				getOrders(searchInputText)
				newSearch(lastSearch)
			}
		)

		if (viewState == GenericViewState.Loading || orderDetailsState == GenericViewState.Loading) {
			val isSearchLoading = viewState == GenericViewState.Loading
			LoadingView(modifier = Modifier.fillMaxSize(), showSearchingText = isSearchLoading)
		} else {
			if (orderResults.isNullOrEmpty()) {
				NoResultsView(
					modifier = Modifier
						.fillMaxSize()
						.padding(20.dp),
					searchInput = searchInputText
				)
			} else {
				Text(
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = 16.dp, vertical = 4.dp),
					text = "${orderResults.size} matches for \"${lastSearch}\"",
					color = BrandColor.BLACK,
					letterSpacing = 0.5.sp,
					fontSize = 12.sp
				)
				OrderCardList(
					orderList = orderResults,
					getOrderDetails = getOrderDetails,
					sddReadyList = null
				)
			}
		}
	}

	if (orderDetailsState == GenericViewState.Success) {
		getOrderDetailsCompletion.invoke()
	}
}

@Composable
private fun SearchBar(
	modifier: Modifier = Modifier,
	searchInput: String,
	onValueChange: (String) -> Unit,
	getOrders: () -> Unit
) {
	val keyboardController = LocalSoftwareKeyboardController.current

	var isFocused by remember { mutableStateOf(false) }
	val focusRequester = remember { FocusRequester() }

	val borderColor = Color(0xFFD5D6D5)
	val focusedColor = BrandColor.GREEN_500

	val borderStroke = if (isFocused) {
		BorderStroke(width = 2.dp, color = focusedColor)
	} else {
		BorderStroke(width = 1.dp, color = borderColor)
	}

	val icon = if (isFocused) ImageVector.vectorResource(id = R.drawable.ic_search) else null

	Column(modifier = modifier) {
		OmniTextField(
			modifier = Modifier
				.padding(start = 8.dp, end = 8.dp, top = 16.dp)
				.focusRequester(focusRequester)
				.onFocusChanged {
					isFocused = it.isFocused
				},
			icon = icon,
			iconTint = focusedColor,
			fieldValue = searchInput,
			onValueChange = { onValueChange(it) },
			keyboardActions = KeyboardActions(onDone = {
				keyboardController?.hide()
				getOrders()
			}),
			borderStroke = borderStroke,
			textFieldShape = RoundedCornerShape(8.dp),
			onClear = {
				isFocused = true
				focusRequester.requestFocus()
			}
		)

		if (isFocused) {
			Row(
				modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp)
			) {
				Icon(
					modifier = Modifier.padding(end = 4.dp),
					imageVector = ImageVector.vectorResource(R.drawable.ic_scan),
					contentDescription = "Scan Barcode"
				)
				Text(
					text = stringResource(id = R.string.search_results_info),
					fontSize = 10.sp,
					color = BrandColor.BLACK
				)
			}
		}

		HorizontalDivider(
			modifier = Modifier.padding(top = 8.dp),
			thickness = 1.dp,
			color = borderColor
		)
	}
}

@Composable
private fun LoadingView(modifier: Modifier = Modifier, showSearchingText: Boolean) {
	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		CircularProgressIndicator(color = BrandColor.DARK_BLUE)
		if (showSearchingText) {
			Text(
				text = "Searching...",
				fontSize = 12.sp,
				fontWeight = FontWeight.Normal,
				color = BrandColor.GRAY_900,
				textAlign = TextAlign.Center,
				letterSpacing = 0.5.sp
			)
		}
	}
}

@Composable
private fun NoResultsView(
	modifier: Modifier = Modifier,
	searchInput: String
) {
	val noSearchResultsText = buildAnnotatedString {
		append("There are no matching results for your search of ")
		withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
			append(searchInput)
		}
		append(".\nPlease try again.")
	}

	Card(
		modifier = modifier,
		shape = RoundedCornerShape(12.dp),
		colors = CardDefaults.cardColors(containerColor = BrandColor.GRAY_50)
	) {
		Column(
			modifier = Modifier.fillMaxSize(),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Image(
				imageVector = ImageVector.vectorResource(R.drawable.no_results_empty_dish),
				contentDescription = "No Results"
			)
		}
	}
}

@Composable
@PreviewPdt
private fun PreviewSearchResultsScreenLoading() {
	SearchResultsScreen(
		viewState = GenericViewState.Loading,
		searchInput = "Roosevelt",
		getOrders = {},
		orderResults = listOf(),
		getOrderDetails = {},
		getOrderDetailsCompletion = {},
		newSearch = {}
	)
}

@Composable
@PreviewPdt
private fun PreviewSearchResultsScreenNoResults() {
	SearchResultsScreen(
		viewState = GenericViewState.Success,
		searchInput = "Roosevelt",
		getOrders = {},
		orderResults = listOf(),
		getOrderDetails = {},
		getOrderDetailsCompletion = {},
		newSearch = {}
	)
}
