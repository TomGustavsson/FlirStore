package com.flir.earhart.flirstore.composables

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.flir.earhart.flirstore.R

@Composable
fun AppListScreen() {
    Scaffold(
        backgroundColor = MaterialTheme.colors.primary,
        topBar = {
            TopAppBar {
                Image(
                    painter = painterResource(id = R.drawable.web_flir_logo),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary),
                    modifier = Modifier.padding(start = 24.dp, top = 10.dp, bottom = 10.dp)
                )
            }
        }) { padding ->
        LazyColumn(modifier = Modifier.padding(padding),content = {
            items(listOf("", "", "")) {
                AppRow()
                Divider(color = MaterialTheme.colors.onSecondary)
            }
        })
    }
}

@Composable
fun AppRow() {
    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
        val (image, texts, button) = createRefs()

        Image(
            painter = painterResource(id = R.drawable.ignite_gradient),
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .constrainAs(image) {
                    start.linkTo(parent.start, 12.dp)
                    top.linkTo(parent.top, 12.dp)
                    bottom.linkTo(parent.bottom, 12.dp)
                }
        )

        Column(Modifier.constrainAs(texts){
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            start.linkTo(image.end, 16.dp)
        }) {
            Text(text = "Settings app", style = MaterialTheme.typography.h1)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Version 20.0.5", style = MaterialTheme.typography.h2)
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .constrainAs(button) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end, 12.dp)
                    bottom.linkTo(parent.bottom)
                }
                .background(color = MaterialTheme.colors.onPrimary, RoundedCornerShape(6.dp))
                .clickable {
                    Log.d("tgiw", "update.")
                }
                .padding(12.dp)
        ) {
            Text(
                text = "Download",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.button,
                color = MaterialTheme.colors.primary
            )
        }
    }
}