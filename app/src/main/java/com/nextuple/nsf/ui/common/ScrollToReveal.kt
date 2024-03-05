package com.nextuple.nsf.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.nextuple.nsf.ui.util.PreviewPdt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrollToReveal(
	mainContent: @Composable ColumnScope.() -> Unit,
	secondaryContent: @Composable RowScope.() -> Unit,
	secondaryModifier: Modifier = Modifier,
	scrollState: ScrollState = rememberScrollState(),
	defaultRevealContent: Boolean = false
) {
	val revealContent = remember { mutableStateOf(defaultRevealContent) }

	val nestedScrollConnection = remember {
		object : NestedScrollConnection {

			override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
				// Hide FAB
				if (available.y < -1) {
					revealContent.value = true
				}
				// Show FAB
				if (available.y > 1) {
					revealContent.value = false
				}
				return Offset.Zero
			}
		}
	}

	Scaffold(
		Modifier
			.nestedScroll(nestedScrollConnection)
			.fillMaxSize()
	) {
		Column(
			modifier = Modifier
				.padding(it)
				.fillMaxSize()
				.verticalScroll(scrollState),
			verticalArrangement = Arrangement.SpaceBetween

		) {
			mainContent()
			AnimatedVisibility(
				visible = revealContent.value,
				enter = slideInVertically(initialOffsetY = { it * 2 }),
				exit = slideOutVertically(targetOffsetY = { it * 2 })
			) {
				Row(
					modifier = secondaryModifier,
					horizontalArrangement = Arrangement.Center
				) {
					secondaryContent()
				}
			}
		}
	}
}

@PreviewPdt
@Composable
fun PreviewDemo() {
	ScrollToReveal(
		mainContent = {},
		secondaryContent = {},
		secondaryModifier = Modifier.padding(top = 17.dp),
		defaultRevealContent = true,
		scrollState = ScrollState(0)
	)
}
