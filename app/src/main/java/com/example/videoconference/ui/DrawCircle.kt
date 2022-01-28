package com.example.videoconference.ui

import android.graphics.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.example.videoconference.R
import com.example.videoconference.ui.theme.listColors
import java.util.*

@Composable
fun DrawCircle(fullName: String, onClick: () -> Unit) {
    val paint = Paint()
    val paint1 = Paint().asFrameworkPaint()
    val bounds = Rect()
    val letter by remember {
        mutableStateOf(
            fullName.substring(0, 1)
                .uppercase(Locale.getDefault())
        )
    }
    val colorR by remember { mutableStateOf(listColors.random()) }
    val context = LocalContext.current
    Canvas(modifier = Modifier
        .width(54.dp)
        .clickable { onClick() },
        onDraw = {
            paint1.apply {
                isAntiAlias = true
                textSize = 16.sp.toPx()
                color = 0xFFFFFFFF.toInt()
                typeface = ResourcesCompat.getFont(
                    context,
                    R.font.akronim
                )
                getTextBounds(
                    letter,
                    0,
                    letter.length,
                    bounds
                )
            }
            drawIntoCanvas {
                paint.color = colorR
                it.drawCircle(
                    paint = paint,
                    radius = 26.dp.toPx(),
                    center = Offset(
                        27.dp.toPx(),
                        24.dp.toPx()
                    )
                )
                it.nativeCanvas.drawText(
                    letter,
                    27.dp.toPx() - bounds.width() / 2,
                    24.dp.toPx() + bounds.height() / 2,
                    paint1
                )
            }
        })
}