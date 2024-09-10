package com.nextuple.nsf.ui.screen.order

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.R
import com.nextuple.nsf.retrofit.dto.PackedItem
import com.nextuple.nsf.ui.common.TertiaryButton
import com.nextuple.nsf.ui.theme.BrandColor

@Composable
fun SubstitutionsAccordion(
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    packedItemList: List<PackedItem>?
) {
    var substitutionsApplied = false

    packedItemList?.forEach { packedItem ->
        if (packedItem.originalItem != null) {
            substitutionsApplied = true
            return@forEach
        }
    }

    if (substitutionsApplied) {
        var isExpanded by remember {
            mutableStateOf(expanded)
        }

        Card(
            modifier = modifier,
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = BrandColor.GRAY_50)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.substitutions_applied),
                        fontSize = 18.sp,
                        fontWeight = FontWeight(700),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )
                    TertiaryButton(
                        text = if (isExpanded) {
                            stringResource(id = R.string.hide)
                        } else {
                            stringResource(id = R.string.show)
                        },
                        onButtonClick = {
                            isExpanded = !isExpanded
                        },
                        tag = "showHideBtn"
                    )
                    Icon(
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .clickable { isExpanded = !isExpanded },
                        painter = if (isExpanded) {
                            painterResource(id = R.drawable.ic_arrow_up)
                        } else {
                            painterResource(id = R.drawable.ic_arrow_drop_down)
                        },
                        contentDescription = "back button"
                    )
                }

                if (isExpanded) {
                    Spacer(modifier = Modifier.height(5.dp))
                    packedItemList?.forEach { packedItem ->
                        if (packedItem.originalItem != null) {
                            Row {
                                Text(
                                    text = "\u2022",
                                    style = TextStyle(textAlign = TextAlign.Center),
                                    modifier = Modifier.width(20.dp),
                                )
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append(packedItem.originalItem!!.productName)
                                        }

                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                                            append(" replaced by ")
                                        }

                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append(packedItem.productName)
                                        }
                                    },
                                    fontSize = 12.sp,
                                    letterSpacing = 0.5.sp,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}