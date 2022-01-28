package com.example.videoconference.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ChangePhoto(
    visible: Boolean,
    fullName: String,
    photoUrl: String,
    onClick: () -> Unit,
    inRow: @Composable (RowScope) -> Unit
) {

    Row(
        Modifier.padding(10.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (photoUrl.isEmpty() && fullName.isNotEmpty()) {
            DrawCircle(fullName) { onClick() }
        } else SetImage(photoUrl) { onClick() }


        inRow(this)
    }
}