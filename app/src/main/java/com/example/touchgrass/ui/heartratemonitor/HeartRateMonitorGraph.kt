package com.example.touchgrass.ui.heartratemonitor

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter

@Composable
fun HeartRateGraph(hr: Float?, sec: Float?) {

    val dataSets = remember { mutableStateListOf<Entry>() }
    if (hr != null && hr != 0f) {
        dataSets.add(Entry(sec ?: 0f, hr))
    }

//    dataSets.add(Entry(1f, 2f))
//    dataSets.add(Entry(2f, 3f))
//    dataSets.add(Entry(3f, 50f))
//    dataSets.add(Entry(4f, 2f))
//    dataSets.add(Entry(4f, 19f))
//    dataSets.add(Entry(4f, 20f))
//    dataSets.add(Entry(4f, 41f))
//    dataSets.add(Entry(4f, 4f))
//    dataSets.add(Entry(4f, 12f))
//    dataSets.add(Entry(4f, 2f))


    val label = "Beats Per Minute"
    val lineColor = MaterialTheme.colors.secondary.toArgb()

    AndroidView (
        modifier = Modifier.fillMaxSize(),
        factory = { context: Context ->
            LineChart(context)
        },
        update = { lineChart ->
            var lineDataSet: LineDataSet? = null
            lineDataSet = LineDataSet(dataSets, label).apply {
                color = lineColor
                isHighlightEnabled = true
                lineWidth = 2f
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawValues(false)
                setDrawCircles(false)
                setDrawHighlightIndicators(false)
            }
            val lineData = LineData(lineDataSet)
            lineChart.apply {
                xAxis.apply {
                    isGranularityEnabled = true
                    position = XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    setDrawAxisLine(false)
                    setExtraOffsets(0f,0f,0f,10f)
                }
                axisLeft.apply {
                    isGranularityEnabled = true
                    setDrawGridLines(false)
                }
                axisRight.apply {
                    setDrawLabels(false)
                    setDrawGridLines(false)
                    setDrawAxisLine(false)
                }
                description = null
                data = lineData
                invalidate()
            }
        }
    )
}