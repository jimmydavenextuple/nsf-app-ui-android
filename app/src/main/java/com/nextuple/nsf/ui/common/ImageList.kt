package com.nextuple.nsf.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.theme.BrandColor

const val MAX_MAGES = 3

@Composable
fun ImageList(images: List<String>, defaultImagesSize: Int = 1) {
	LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
		if (images.isEmpty()) {
			for (i in 1..if (defaultImagesSize > MAX_MAGES) MAX_MAGES else defaultImagesSize) {
				item {
					Image(
						modifier = Modifier
							.size(48.dp)
							.padding(8.dp),
						painter = painterResource(id = R.drawable.placeholder_image),
						contentDescription = "placeholder"
					)
				}
			}
		} else {
			val imagesSize = images.size
			items(items = images.subList(0, if (imagesSize > MAX_MAGES) MAX_MAGES else images.size)) {
				ImageFromNetwork(it)
			}
		}
	}
}

@Composable
private fun ImageFromNetwork(url: String) {
	AsyncImage(
		model = ImageRequest.Builder(LocalContext.current)
			.data(url)
			.crossfade(true)
			.build(),
		contentDescription = "",
		error = painterResource(id = R.drawable.placeholder_image),
		placeholder = painterResource(id = R.drawable.placeholder_image),
		contentScale = ContentScale.Fit,
		modifier = Modifier
			.background(BrandColor.GRAY_50)
			.size(48.dp)
			.padding(8.dp)
	)
}
