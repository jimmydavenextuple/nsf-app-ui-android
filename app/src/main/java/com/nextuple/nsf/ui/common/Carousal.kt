package com.nextuple.nsf.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.theme.BrandColor

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Carousel(modifier: Modifier = Modifier, images: List<String>) {
	val pagerState = rememberPagerState()

	Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
		if (images.isEmpty()) {
			Image(
				modifier = Modifier.padding(vertical = 16.dp),
				painter = painterResource(id = R.drawable.placeholder_image),
				contentDescription = "placeholder"
			)
		} else {
			HorizontalPager(
				modifier = Modifier.weight(1f),
				pageCount = images.size,
				state = pagerState
			) { index ->
				val image = images[index]
				AsyncImage(
					model = image,
					error = painterResource(id = R.drawable.placeholder_image),
					placeholder = painterResource(id = R.drawable.placeholder_image),
					contentDescription = null,
					contentScale = ContentScale.FillHeight
				)
			}

			Row(
				modifier = Modifier.padding(vertical = 6.dp),
				horizontalArrangement = Arrangement.Center
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
}

@Composable
@Preview(showBackground = true)
fun CarouselPreview() {
	Carousel(images = listOf())
}
