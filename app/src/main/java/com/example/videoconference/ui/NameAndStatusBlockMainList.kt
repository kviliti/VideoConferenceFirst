package com.example.videoconference.ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.videoconference.ui.theme.H7

@Composable
fun NameBlockMainList(
    name: String = "",
    lastName: String = ""
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(start = 10.dp)
    ) {
        Text(
            text = name,
            fontSize = 16.sp,
            style = H7,
            maxLines = 1, modifier = Modifier.width(LocalConfiguration
                .current.screenWidthDp.dp / 2.3f))
        Text(
            text = lastName,
            fontSize = 16.sp,
            style = H7,
            maxLines = 1, modifier = Modifier.width(LocalConfiguration
                .current.screenWidthDp.dp / 2.3f).padding(bottom = 8.dp))

    }
}