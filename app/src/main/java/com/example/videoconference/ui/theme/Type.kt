package com.example.videoconference.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

import com.example.videoconference.R

// Set of Material typography styles to start with
val Typography1 = Typography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
)
val H5 = TextStyle(
    shadow = Shadow(
        offset = Offset(0f, 10f)
    ),
    letterSpacing = 1.sp,
    fontSize = 25.sp,
    fontFamily = FontFamily(Font(R.font.marckcript_regular))
)

val H4 = TextStyle(
    letterSpacing = 1.sp,
    fontSize = 20.sp,
    fontFamily = FontFamily(Font(R.font.chat1))
)
val H6 = TextStyle(
    shadow = Shadow(
        offset = Offset(30f, 15f), color = Color.Gray, blurRadius = 20f
    ),
    fontWeight = FontWeight.Medium,
    fontSize = 18.sp,
    letterSpacing = 0.15.sp,
    textAlign = TextAlign.Start,
    fontFamily = FontFamily(
        Font(R.font.akronim)
    )
)
val H8 = TextStyle(
    shadow = Shadow(
        offset = Offset(30f, 15f), color = Color.Gray, blurRadius = 20f
    ),
    fontWeight = FontWeight.Medium,
    fontSize = 18.sp,
    letterSpacing = 0.15.sp,
    textAlign = TextAlign.Start,
    fontFamily = FontFamily(
        Font(R.font.merriweathertalic)
    )
)
val H7 = TextStyle(
    shadow = Shadow(
        offset = Offset(30f, 15f), color = Color.Gray, blurRadius = 20f
    ),
    fontWeight = FontWeight.Medium,
    fontSize = 18.sp,
    letterSpacing = 0.15.sp,
    textAlign = TextAlign.Start,
    fontFamily = FontFamily(
        Font(R.font.merriweather_bold)
    )
)
val H9 = TextStyle(
    shadow = Shadow(
        offset = Offset(30f, 15f), color = Color.Gray, blurRadius = 15f
    ),
    fontWeight = FontWeight.Medium,
    fontSize = 18.sp,
    letterSpacing = 0.15.sp,
    textAlign = TextAlign.Start,
    fontFamily = FontFamily(
        Font(R.font.ass)
    )
)
val Body2 = TextStyle(
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    letterSpacing = 0.25.sp,
    textAlign = TextAlign.Center
)
val Button = TextStyle(
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    letterSpacing = 1.25.sp,
    textAlign = TextAlign.Center
)
val caption = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp
)
val Typography = Typography(
    h6 = H6,
    body2 = Body2,
    button = Button,

    defaultFontFamily = FontFamily(
        Font(R.font.marckcript_regular)
    )
    // Using default values for subtitle1, caption, etc.
)
/* Other default text styles to override
button = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.W500,
    fontSize = 14.sp
),

*/
