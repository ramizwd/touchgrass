package com.example.touchgrass.ui.heartratemonitor

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.example.touchgrass.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@Composable
fun HeartRateGraph(hr: Float?, sec: Float?) {

    val dataSets = remember { mutableStateListOf<Entry>() }
    if (hr != null && hr != 0f) {
        dataSets.add(Entry(sec ?: 0f, hr))
    }

    val label = "Beats Per Minute"
    val lineColor = MaterialTheme.colors.secondary.toArgb()
    val noDataTextColor = MaterialTheme.colors.onPrimary.toArgb()
    val noDataText = stringResource(R.string.no_hr_data)
    val themeTextColor = MaterialTheme.colors.onPrimary.toArgb()

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context: Context ->
            LineChart(context)
        },
        update = { lineChart ->
            var lineData: LineData? = null

            if (dataSets.isNotEmpty()) {
                val lineDataSet = LineDataSet(dataSets, label).apply {
                    color = lineColor
                    isHighlightEnabled = true
//                    lineWidth = 2f
//                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    setDrawValues(false)
                    setDrawCircles(false)
                    setDrawHighlightIndicators(false)
                }
                lineData = LineData(lineDataSet)
            }

            lineChart.apply {
                xAxis.apply {
                    isGranularityEnabled = true
                    position = XAxisPosition.BOTTOM
                    textColor = themeTextColor
                    setDrawGridLines(false)
                    setDrawAxisLine(false)
                    setExtraOffsets(0f, 0f, 0f, 10f)
                }
                axisLeft.apply {
                    isGranularityEnabled = true
                    textColor = themeTextColor
                    setDrawGridLines(false)
                }
                axisRight.apply {
                    setDrawLabels(false)
                    setDrawGridLines(false)
                    setDrawAxisLine(false)
                }
                legend.textColor = themeTextColor
                description = null
                data = lineData
                setNoDataText(noDataText)
                setNoDataTextColor(noDataTextColor)
                invalidate()
            }
        }
    )
}