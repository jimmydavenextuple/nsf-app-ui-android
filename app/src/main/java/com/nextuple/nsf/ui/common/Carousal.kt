package com.nextuple.nsf.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.theme.BrandColor
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Carousel(
	modifier: Modifier = Modifier,
	images: List<String>,
	isZoomEnabled: Boolean = false,
	size: DpSize,
	indexStart: Int,
	changeIndex: (Int) -> Unit
) {
	val pagerState = rememberPagerState { images.size }
	val zoomState = rememberZoomState(
		maxScale = 5f
	)
	var firstLanding by remember { mutableStateOf(true) }
	LaunchedEffect(pagerState.currentPage) {
		zoomState.reset()
		changeIndex(pagerState.currentPage)
	}

	val coroutineScope = rememberCoroutineScope()
	LaunchedEffect(firstLanding) {
		if (firstLanding) {
			coroutineScope.launch {
				pagerState.scrollToPage(indexStart)
			}
			firstLanding = false
		}
	}

	Column(
		modifier = modifier
			.height(size.height + 20.dp)
			.width(size.width)
	) {
		Box {
			if (images.isEmpty()) {
				Image(
					modifier = Modifier.padding(vertical = 16.dp),
					painter = painterResource(id = R.drawable.placeholder_image),
					contentDescription = "placeholder"
				)
			} else {
				HorizontalPager(
					modifier = Modifier
						.size(size)
						.clip(RectangleShape),
					state = pagerState
				) { index ->
					val image = images[index]
					val rowModifier = if (isZoomEnabled) {
						Modifier
							.fillMaxSize()
							.zoomable(zoomState)
							.clip(RectangleShape)
					} else {
						Modifier
							.fillMaxSize()
					}
					Row(
						modifier = rowModifier,
						horizontalArrangement = Arrangement.Center,
						verticalAlignment = Alignment.CenterVertically
					) {
						AsyncImage(
							model = image,
							error = painterResource(id = R.drawable.placeholder_image),
							placeholder = painterResource(id = R.drawable.placeholder_image),
							contentDescription = null,
							contentScale = ContentScale.FillHeight
						)
					}
				}
			}
		}
		Row(
			modifier = Modifier
				.padding(vertical = 6.dp)
				.fillMaxWidth()
				.fillMaxHeight(),
			horizontalArrangement = Arrangement.Center,
			verticalAlignment = Alignment.Bottom
		) {
			repeat(images.size) { iteration ->
				val color = if (pagerState.currentPage == iteration) {
					BrandColor.PINK_NT
				} else {
					BrandColor.GRAY_300
				}
				Box(
					modifier = Modifier
						.padding(horizontal = 2.dp)
						.clip(CircleShape)
						.background(color)
						.size(8.dp)
				)
			}
		}
	}
}

@Composable
@Preview(showBackground = true)
fun CarouselPreview() {
	Carousel(
		images = listOf(
			"https://picsum.photos/1705",
			"https://picsum.photos/1726",
			"https://picsum.photos/1701"
		),
		size = DpSize(136.dp, 136.dp),
		indexStart = 2,
		changeIndex = { _ -> }
	)
}
