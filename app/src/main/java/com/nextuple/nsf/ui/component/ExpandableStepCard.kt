package com.nextuple.nsf.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.common.PrimaryButton
import com.nextuple.nsf.ui.common.Step
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily

@Composable
fun ExpandableStepCard(
	modifier: Modifier = Modifier,
	stepNumber: String?,
	title: String?,
	annotatedTitle: AnnotatedString = buildAnnotatedString { },
	isActive: Boolean,
	isComplete: Boolean = false,
	extraContent: @Composable () -> Unit = {}
) {
	Card(
		modifier = modifier
			.fillMaxWidth()
			.wrapContentHeight()
			.padding(horizontal = 8.dp, vertical = 4.dp),
		shape = RoundedCornerShape(4.dp),
		colors = CardDefaults.cardColors(containerColor = BrandColor.GRAY_50)
	) {
		Column(modifier = Modifier.padding(10.dp)) {
			Row(verticalAlignment = Alignment.CenterVertically) {
				Step(
					text = stepNumber,
					isActive = isActive,
					isOutlined = (isActive || stepNumber != null)
				)
				Spacer(modifier = Modifier.width(8.dp))
				if (title != null) {
					Text(
						text = title,
						fontFamily = FontFamily.ARCHIVO,
						fontSize = 20.sp,
						fontWeight = FontWeight.Bold,
						letterSpacing = 0.5.sp,
						color = if (isActive) BrandColor.BLACK else BrandColor.GRAY_500
					)
				} else {
					Text(
						text = annotatedTitle,
						fontFamily = FontFamily.ARCHIVO,
						fontSize = 20.sp,
						fontWeight = FontWeight.Bold,
						letterSpacing = 0.5.sp,
						color = if (isActive) BrandColor.BLACK else BrandColor.GRAY_500
					)
				}

				if (isComplete && !isActive) {
					Spacer(modifier = Modifier.weight(1f))
					Icon(
						modifier = Modifier.padding(start = 6.dp),
						imageVector = ImageVector.vectorResource(id = R.drawable.ic_check),
						tint = BrandColor.BLUE_300_NT,
						contentDescription = "Completed Check"
					)
				}
			}
			if (isActive) {
				Spacer(modifier = Modifier.height(8.dp))
				extraContent()
			}
		}
	}
}

@Preview
@Composable
private fun ExpandableStepCardPreviewInactive() {
	ExpandableStepCard(
		stepNumber = "1",
		title = "Step 1",
		isActive = false
	)
}

@Preview
@Composable
private fun ExpandableStepCardPreviewCompleted() {
	ExpandableStepCard(
		stepNumber = "1",
		title = "Step 1",
		isActive = false,
		isComplete = true
	)
}

@Preview
@Composable
private fun ExpandableStepCardPreviewActive() {
	ExpandableStepCard(
		stepNumber = "1",
		title = "Step 1",
		isActive = true
	) {
		Column(
			modifier = Modifier.fillMaxWidth(),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(text = "This is place holder text for the preview")
			PrimaryButton(
				modifier = Modifier.padding(vertical = 15.dp),
				text = "Select Printer".uppercase(),
				onButtonClick = {}
			)
		}
	}
}
