package com.nextuple.nsf.ui.screen.demo

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.ui.common.MultiOptionSubstitutionModal
import com.nextuple.nsf.ui.common.TertiaryButton
import com.nextuple.nsf.ui.state.DemoViewModel
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.ui.util.PreviewPdt

@Composable
fun DemoScreen(
    demoVM: DemoViewModel,
) {
    if (demoVM.demoCreateFRResponse != null) {
        Toast.makeText(
            LocalContext.current,
            "FR # " + demoVM.demoCreateFRResponse!!.fulfillmentRequestNumber
                    + " created for "
                    +demoVM.demoCreateFRResponse!!.fulfillmentType,
            Toast.LENGTH_LONG
        ).show()
        demoVM.demoCreateFRResponse = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding()
            .background(BrandColor.GRAY_50)
    ) {
        Card(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 8.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = BrandColor.GRAY_100),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Text(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
                text = "Create BOPIS FR",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = BrandColor.BLUE_800_NT
                )
            )
            Text(
                text = "Store #1234",
                style = TextStyle(textAlign = TextAlign.Center),
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
                fontSize = 12.sp,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Bold
            )
            Row {
                Text(
                    text = "\u2022",
                    style = TextStyle(textAlign = TextAlign.Center),
                    modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 8.dp),
                    fontSize = 12.sp,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                )
                TertiaryButton(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 24.dp, top = 8.dp, bottom = 8.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    onButtonClick = {
                        demoVM.createFRBOPIS(1)
                    },
                    text = "Variant #1",
                    enabled = demoVM.demoApiState != GenericViewState.Loading
                )
            }
            Row {
                Text(
                    text = "\u2022",
                    style = TextStyle(textAlign = TextAlign.Center),
                    modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 8.dp),
                    fontSize = 12.sp,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                )
                TertiaryButton(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 24.dp, top = 8.dp, bottom = 8.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    onButtonClick = {
                        demoVM.createFRBOPIS(2)
                    },
                    text = "Variant #2",
                    enabled = demoVM.demoApiState != GenericViewState.Loading
                )
            }
            Text(
                text = "Store #1235 - Batch Pick",
                style = TextStyle(textAlign = TextAlign.Center),
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
                fontSize = 12.sp,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Bold
            )
            Row {
                Text(
                    text = "\u2022",
                    style = TextStyle(textAlign = TextAlign.Center),
                    modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 8.dp),
                    fontSize = 12.sp,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                )
                TertiaryButton(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 24.dp, top = 8.dp, bottom = 8.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    onButtonClick = {
                        demoVM.createFRBOPIS(3)
                    },
                    text = "By Zone - Seq By Location",
                    enabled = demoVM.demoApiState != GenericViewState.Loading
                )
            }
            Row {
                Text(
                    text = "\u2022",
                    style = TextStyle(textAlign = TextAlign.Center),
                    modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 8.dp),
                    fontSize = 12.sp,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                )
                TertiaryButton(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 24.dp, top = 8.dp, bottom = 8.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    onButtonClick = {
                        demoVM.createFRBOPIS(4)
                    },
                    text = "By Zone - Seq By Dept",
                    enabled = demoVM.demoApiState != GenericViewState.Loading
                )
            }
            Text(
                text = "Store #1236",
                style = TextStyle(textAlign = TextAlign.Center),
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
                fontSize = 12.sp,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Bold
            )
            Row {
                Text(
                    text = "\u2022",
                    style = TextStyle(textAlign = TextAlign.Center),
                    modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 8.dp),
                    fontSize = 12.sp,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                )
                TertiaryButton(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 24.dp, top = 8.dp, bottom = 8.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    onButtonClick = {
                        demoVM.createFRBOPIS(1)
                    },
                    text = "For Manual Close Wave",
                    enabled = demoVM.demoApiState != GenericViewState.Loading
                )
            }
        }

        Card(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 8.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = BrandColor.GRAY_100),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Text(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
                text = "Create SDD FR",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = BrandColor.BLUE_800_NT
                )
            )
            Row {
                Text(
                    text = "\u2022",
                    style = TextStyle(textAlign = TextAlign.Center),
                    modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 8.dp),
                    fontSize = 12.sp,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                )
                TertiaryButton(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 24.dp, top = 8.dp, bottom = 8.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    onButtonClick = {
                        demoVM.createFRSDD(1)
                    },
                    text = "For Logged In Store",
                    enabled = demoVM.demoApiState != GenericViewState.Loading
                )
            }
        }
    }
}

@Composable
@PreviewPdt
fun PreviewDemoScreen() {
    DemoScreen(
        demoVM = DemoViewModel(null, null)
    )
}