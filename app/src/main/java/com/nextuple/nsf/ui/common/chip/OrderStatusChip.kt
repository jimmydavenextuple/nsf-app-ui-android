package com.nextuple.nsf.ui.common.chip

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily
import com.nextuple.nsf.util.OrderStatus
import com.nextuple.nsf.util.OrderStatus.AGED
import com.nextuple.nsf.util.OrderStatus.BEING_PACKED
import com.nextuple.nsf.util.OrderStatus.BEING_STAGED
import com.nextuple.nsf.util.OrderStatus.CANCELED
import com.nextuple.nsf.util.OrderStatus.COMPLETED
import com.nextuple.nsf.util.OrderStatus.DECLINED
import com.nextuple.nsf.util.OrderStatus.EXTENDED
import com.nextuple.nsf.util.OrderStatus.INCOMING
import com.nextuple.nsf.util.OrderStatus.LATE
import com.nextuple.nsf.util.OrderStatus.PACK
import com.nextuple.nsf.util.OrderStatus.READY
import com.nextuple.nsf.util.OrderStatus.STAGE

@Composable
fun OrderStatusChip(
	modifier: Modifier = Modifier,
	statusText: String,
	backgroundColor: Color = BrandColor.PINK_NT
) {
	val orderStatus = OrderStatus.getByStatus(statusText)

	// Filled chip
	if (orderStatus == OrderStatus.DEFAULT || orderStatus == OrderStatus.CHECKED_IN) {
		StatusChip(
			modifier = modifier
				.background(color = backgroundColor, shape = RoundedCornerShape(size = 4.dp)),
			statusText = statusText,
			statusColor = BrandColor.GRAY_50
		)
	} else { // Outlined chip
		StatusChip(
			modifier = modifier
				.border(
					width = 1.dp,
					color = orderStatus.statusColor,
					shape = RoundedCornerShape(size = 2.dp)
				)
				.background(
					color = BrandColor.TRANSPARENT,
					shape = RoundedCornerShape(size = 2.dp)
				),
			statusText = orderStatus.statusText,
			statusColor = orderStatus.statusColor,
			iconDrawable = orderStatus.iconDrawable
		)
	}
}

@Composable
fun StatusChip(
	modifier: Modifier = Modifier,
	statusText: String,
	statusColor: Color,
	fontSize: TextUnit = 12.sp,
	iconDrawable: Int? = null
) {
	Row(
		modifier = modifier
			.padding(horizontal = 8.dp, vertical = 4.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.Center
	) {
		Text(
			modifier = Modifier
				.testTag("StatusText"),
			text = statusText,
			color = statusColor,
			fontSize = fontSize,
			fontFamily = FontFamily.ARCHIVO,
			fontWeight = FontWeight.Bold,
			letterSpacing = 0.5.sp
		)

		iconDrawable?.let {
			Icon(
				modifier = Modifier.padding(start = 6.dp),
				imageVector = ImageVector.vectorResource(id = it),
				tint = statusColor,
				contentDescription = statusText
			)
		}
	}
}

@Composable
@Preview
fun PreviewOrderStatusTextReady() {
	OrderStatusChip(statusText = READY.statusText)
}

@Composable
@Preview
fun PreviewOrderStatusTextCompleted() {
	OrderStatusChip(statusText = COMPLETED.statusText)
}

@Composable
@Preview
fun PreviewOrderStatusTextExtended() {
	OrderStatusChip(statusText = EXTENDED.statusText)
}

@Composable
@Preview
fun PreviewOrderStatusTextCanceled() {
	OrderStatusChip(statusText = CANCELED.statusText)
}

@Composable
@Preview
fun PreviewOrderStatusTextDeclined() {
	OrderStatusChip(statusText = DECLINED.statusText)
}

@Composable
@Preview
fun PreviewOrderStatusTextAged() {
	OrderStatusChip(statusText = AGED.statusText)
}

@Composable
@Preview
fun PreviewOrderStatusTextIncoming() {
	OrderStatusChip(statusText = INCOMING.statusText)
}

@Composable
@Preview
fun PreviewOrderStatusTextLate() {
	OrderStatusChip(statusText = LATE.statusText)
}

@Composable
@Preview
fun PreviewOrderStatusTextPack() {
	OrderStatusChip(statusText = PACK.statusText)
}

@Composable
@Preview
fun PreviewOrderStatusTextPacking() {
	OrderStatusChip(statusText = BEING_PACKED.statusText)
}

@Composable
@Preview
fun PreviewOrderStatusTextStage() {
	OrderStatusChip(statusText = STAGE.statusText)
}

@Composable
@Preview
fun PreviewOrderStatusTextStaging() {
	OrderStatusChip(statusText = BEING_STAGED.statusText)
}

@Composable
@Preview
fun PreviewOrderStatusTextCurbside() {
	OrderStatusChip(statusText = "Curbside #3")
}

@Composable
@Preview
fun PreviewOrderStatusTextCheckinTime() {
	OrderStatusChip(statusText = "4:12")
}
