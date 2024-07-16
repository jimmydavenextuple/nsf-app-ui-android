package com.nextuple.nsf.ui.screen.pick

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import com.nextuple.nsf.ui.theme.BrandColor
import kotlinx.coroutines.delay

@Composable
fun CustomTextPopup(
	content: String,
	onDismiss: () -> Unit,
	onHide: () -> Unit,
	screenPositionX: Int,
	screenPositionY: Int
) {
	LaunchedEffect(Unit) {
		delay(1500)
		onHide()
	}

	Popup(
		popupPositionProvider = object : PopupPositionProvider {
			override fun calculatePosition(
				anchorBounds: IntRect,
				windowSize: IntSize,
				layoutDirection: LayoutDirection,
				popupContentSize: IntSize
			): IntOffset {
				return IntOffset(
					x = screenPositionX,
					y = screenPositionY
				)
			}
		},
		onDismissRequest = onDismiss,
		content = {
			Box(
				modifier = Modifier
					.background(color = BrandColor.GRAY_800, shape = RoundedCornerShape(4.dp))
					.padding(horizontal = 10.dp, vertical = 2.dp)
			) {
				Text(
					text = content,
					color = Color.White,
					fontSize = 8.sp,
					fontWeight = FontWeight(400),
					letterSpacing = 0.5.sp
				)
			}
		}
	)
}

@Composable
@Preview
private fun CustomTextPopupPreview() {
	CustomTextPopup(
		content = "Copied",
		onDismiss = {},
		onHide = {},
		screenPositionX = 0,
		screenPositionY = 0
	)
}
