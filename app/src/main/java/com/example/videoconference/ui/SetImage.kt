package com.example.videoconference.ui

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SetImage(
    photoUrl: String,
    onClick1: () -> Unit
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    Glide.with(LocalContext.current).asBitmap()
        .load(photoUrl)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                bitmap = resource
            }

            override fun onLoadCleared(placeholder: Drawable?) {

            }
        })
    if (bitmap != null)
        Image(bitmap = bitmap!!.asImageBitmap(), modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .clickable {
                onClick1()
            },
            contentDescription = null)
}



