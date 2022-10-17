package com.example.touchgrass.ui.shared.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.touchgrass.R
import com.example.touchgrass.service.StepCounterServiceHelper
import com.example.touchgrass.utils.Constants

/**
 * Shared custom CircularProgressbar.
 *
 * @param value is the current value provided for the circle.
 * @param target the maximum value of the circle.
 * @param fontSize font size of the value of the circle.
 * @param radius radius of the foreground and background arc.
 * @param foregroundColor the color of the foreground arc.
 * @param foregroundColor the color of the background arc.
 * @param strokeWidth thickness of the arc.
 * @param animationDuration animation of the arc.
 * @param animationDelay animation delay of the arc.
 * @param streak streak count for the Home screen circle.
 * @param isSensorOn used for the FloatingActionButton service toggle.
 * @param writing used to check if reading data from a BLE device.
 * @param isConnected for checking if the phone is connected to a BLE device.
 * @param isHydrationScreen for displaying the right progress circle info for the right screen.
 * @param isHeartRateScreen for displaying the right progress circle info for the right screen.
 * @param isStepCounterScreen for displaying the right progress circle info for the right screen.
 * @param isHomeScreen for displaying the right progress circle info for the right screen.
 */
@Composable
fun CircularProgressBar(
    value: Float = 0f,
    target: Int = 0,
    fontSize: TextUnit = 42.sp,
    radius: Dp = 125.dp,
    foregroundColor: Color = MaterialTheme.colors.secondary,
    backgroundColor: Color = MaterialTheme.colors.secondary.copy(alpha = 0.1f),
    strokeWidth: Dp = 22.dp,
    animationDuration: Int = 1000,
    animationDelay: Int = 0,
    streak: Float? = 0f,
    isSensorOn: Boolean = false,
    writing: Boolean = false,
    isConnected: Boolean = false,
    isHydrationScreen: Boolean = false,
    isHeartRateScreen: Boolean = false,
    isStepCounterScreen: Boolean = false,
    isHomeScreen: Boolean = false,
) {
    val context = LocalContext.current

    var animationPlayed by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) { animationPlayed = true }

    val curPercentage = animateFloatAsState(
        targetValue = if (animationPlayed) value else 0f,
        animationSpec = tween(
            durationMillis = animationDuration,
            delayMillis = animationDelay
        )
    )
    val calcCurValue = 280f * curPercentage.value

    val animatedTextColor by animateColorAsState(
        targetValue = if (isSensorOn)
            MaterialTheme.colors.onPrimary
        else
            MaterialTheme.colors.onPrimary.copy(alpha = 0.4f),
        animationSpec = tween(400)
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(radius * 2f)
    ) {
        Canvas(modifier = Modifier.size(radius * 2f)) {
            drawArc(
                color = backgroundColor,
                startAngle = 130f,
                sweepAngle = 280f,
                useCenter = false,
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round),
            )
            drawArc(
                color = foregroundColor,
                startAngle = 130f,
                sweepAngle = if (calcCurValue >= 280f) 280f else calcCurValue,
                useCenter = false,
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        // Displaying the right circle information depending on the screen.
        if(isHydrationScreen) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${(curPercentage.value * target).toInt()}",
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onPrimary
                )
                Text(
                    text = "/$target",
                    color = MaterialTheme.colors.onPrimary
                )
            }
        }
        else if (isHeartRateScreen) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (writing)
                        stringResource(
                            R.string.hr_bpm_txt,
                            (curPercentage.value * target).toInt()
                        )
                    else "0",
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                text = if (isConnected)
                    stringResource(R.string.connected_bt)
                else "",
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.BottomCenter),
            )
        }
        else if (isStepCounterScreen) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${(curPercentage.value * target).toInt()}",
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    color = animatedTextColor
                )
                Text(
                    text = "/$target",
                    color = animatedTextColor
                )
            }
            val contentDesc = stringResource(
                if (!isSensorOn) R.string.enable_step_sensor
                else R.string.disable_step_sensor
            )
            FloatingActionButton(
                onClick = {
                    StepCounterServiceHelper.launchForegroundService(
                        context = context,
                        action = if (isSensorOn) Constants.ACTION_STOP_SERVICE
                        else Constants.ACTION_START_SERVICE
                    )
                },
                modifier = Modifier
                    .size(54.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Icon(
                    painterResource(
                        if (isSensorOn) R.drawable.ic_pause
                        else R.drawable.ic_play_arrow
                    ),
                    contentDesc,
                    Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                )
            }
        }
        else if (isHomeScreen) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (streak != null) "${streak.toInt()}" else "0",
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.streak),
                    fontWeight = FontWeight.Light
                )
            }
            Icon(
                painter = painterResource(R.drawable.ic_snail),
                tint = Color.Unspecified,
                contentDescription = stringResource(R.string.snail_ic_desc),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(64.dp)
            )
        }
    }
}