package com.flir.earhart.flirstore.composables

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.flir.earhart.flirstore.R
import com.flir.earhart.flirstore.models.AppInfo
import com.flir.earhart.flirstore.viewmodel.FlirStoreViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AppListScreen(viewModel: FlirStoreViewModel, clickCallback: (AppInfo) -> Unit) {

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
            items(viewModel.availableApks) {
                AppRow(it, viewModel.queuedDownloads.contains(it.apkName)) {
                    clickCallback.invoke(it)
                }
                Divider(color = MaterialTheme.colors.onSecondary)
            }
        })
    }
}

@Composable
fun AppRow(appInfo: AppInfo, loading: Boolean, clickCallback: (AppInfo) -> Unit) {
    ConstraintLayout(modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 12.dp, start = 16.dp, end = 16.dp)) {
        val (image, texts, button) = createRefs()
        appInfo.icon?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .constrainAs(image) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
            )
        }

        Column(Modifier.constrainAs(texts){
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            start.linkTo(if(appInfo.icon != null) image.end else parent.start, 16.dp)
            end.linkTo(button.start, 16.dp)
            width = Dimension.fillToConstraints
        }

        ) {
            Text(text = appInfo.name ?: appInfo.apkName, style = MaterialTheme.typography.h1, maxLines = 2, overflow = TextOverflow.Ellipsis)
            appInfo.versionNum?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = it, style = MaterialTheme.typography.h2, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .constrainAs(button) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .background(color = if(loading) MaterialTheme.colors.onSecondary else MaterialTheme.colors.onPrimary, RoundedCornerShape(6.dp))
                .clickable {
                    clickCallback.invoke(appInfo)
                }
                .padding(12.dp)
        ) {
            Text(
                text = if(loading) "Cancel" else if(appInfo.alreadyInstalled) "Update" else "Download",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.button,
                color = MaterialTheme.colors.primary
            )
        }
    }
}