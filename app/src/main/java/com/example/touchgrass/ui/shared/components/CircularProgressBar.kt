package com.example.touchgrass.ui.shared.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CircularProgressBar(
    percentage: Float,
    number: Int,
    fontSize: TextUnit = 42.sp,
    radius: Dp = 125.dp,
    color: Color = MaterialTheme.colors.secondary,
    backgroundColor: Color = Color(0xFFFFF1D6),
    strokeWidth: Dp = 22.dp,
    animationDuration: Int = 1000,
    animationDelay: Int = 0
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val curPercentage = animateFloatAsState(
        targetValue = if (animationPlayed) percentage else 0f,
        animationSpec = tween(
            durationMillis = animationDuration,
            delayMillis = animationDelay
        )
    )
    LaunchedEffect(key1 = true) { animationPlayed = true }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(radius * 2f)
    ) {
        Canvas(modifier = Modifier.size(radius * 2f)) {
            drawArc(
                color = backgroundColor,
                -90f,
                360f,
                useCenter = false,
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round),
            )
            drawArc(
                color = color,
                -90f,
                360 * curPercentage.value,
                useCenter = false,
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        if (number != 0) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${(curPercentage.value * number).toInt()}",
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold
                )
                Text("/$number")
            }
        } else {
            Icon(imageVector = Icons.Default.Person, contentDescription = "snail")
        }
    }
}