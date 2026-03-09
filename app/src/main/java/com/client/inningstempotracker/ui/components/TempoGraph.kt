package com.client.inningstempotracker.ui.components

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.client.inningstempotracker.data.db.OverEntity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@Composable
fun TempoGraph(
    overs: List<OverEntity>,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = true
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.granularity = 1f
                axisRight.isEnabled = false
                setBackgroundColor(Color.WHITE)
            }
        },
        update = { chart ->
            if (overs.isEmpty()) {
                chart.clear()
                return@AndroidView
            }

            val powerplayEntries = mutableListOf<Entry>()
            val middleEntries = mutableListOf<Entry>()
            val deathEntries = mutableListOf<Entry>()

            overs.forEach { over ->
                val entry = Entry(over.overNumber.toFloat(), over.runs.toFloat())
                when (over.phaseType) {
                    "Powerplay" -> powerplayEntries.add(entry)
                    "Middle" -> middleEntries.add(entry)
                    "Death" -> deathEntries.add(entry)
                }
            }

            val dataSets = mutableListOf<LineDataSet>()

            if (powerplayEntries.isNotEmpty()) {
                dataSets.add(LineDataSet(powerplayEntries, "Powerplay").apply {
                    color = Color.parseColor("#2476D1")
                    setCircleColor(Color.parseColor("#2476D1"))
                    lineWidth = 2f
                    circleRadius = 4f
                    valueTextSize = 10f
                })
            }

            if (middleEntries.isNotEmpty()) {
                dataSets.add(LineDataSet(middleEntries, "Middle").apply {
                    color = Color.parseColor("#5EB8FF")
                    setCircleColor(Color.parseColor("#5EB8FF"))
                    lineWidth = 2f
                    circleRadius = 4f
                    valueTextSize = 10f
                })
            }

            if (deathEntries.isNotEmpty()) {
                dataSets.add(LineDataSet(deathEntries, "Death").apply {
                    color = Color.parseColor("#F9A825")
                    setCircleColor(Color.parseColor("#F9A825"))
                    lineWidth = 2f
                    circleRadius = 4f
                    valueTextSize = 10f
                })
            }

            chart.data = LineData(dataSets.toList())
            chart.invalidate()
        },
        modifier = modifier
    )
}