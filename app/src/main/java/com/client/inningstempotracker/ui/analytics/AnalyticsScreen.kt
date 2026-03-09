package com.client.inningstempotracker.ui.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.client.inningstempotracker.theme.ColorTokens
import com.client.inningstempotracker.theme.Dimensions
import com.client.inningstempotracker.ui.components.TempoGraph
import com.client.inningstempotracker.utils.DateFormatter
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen() {
    val viewModel: AnalyticsViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val selectedMatchIds by viewModel.selectedMatchIds.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics") }
            )
        }
    ) { paddingValues ->
        when (val s = state) {
            is AnalyticsState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ColorTokens.PrimaryAccent)
                }
            }

            is AnalyticsState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(Dimensions.md),
                    verticalArrangement = Arrangement.spacedBy(Dimensions.md)
                ) {
                    item {
                        Text(
                            text = "Select Innings",
                            style = MaterialTheme.typography.titleLarge,
                            color = ColorTokens.TextPrimary
                        )
                    }

                    if (s.allMatches.isEmpty()) {
                        item {
                            Text(
                                text = "No matches available",
                                style = MaterialTheme.typography.bodyLarge,
                                color = ColorTokens.TextSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        items(s.allMatches) { match ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedMatchIds.contains(match.id))
                                        ColorTokens.InputBackground
                                    else
                                        ColorTokens.Card
                                ),
                                elevation = CardDefaults.cardElevation(Dimensions.cardElevation)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(Dimensions.md),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = selectedMatchIds.contains(match.id),
                                        onCheckedChange = {
                                            viewModel.toggleMatchSelection(match.id)
                                        }
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = match.name,
                                            style = MaterialTheme.typography.titleLarge,
                                            color = ColorTokens.TextPrimary
                                        )
                                        Text(
                                            text = "${match.format} • ${DateFormatter.formatForDisplay(match.date)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = ColorTokens.TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (s.selectedAnalytics.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(Dimensions.sm))
                            Text(
                                text = "Comparison",
                                style = MaterialTheme.typography.titleLarge,
                                color = ColorTokens.TextPrimary
                            )
                        }

                        items(s.selectedAnalytics) { analytics ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = ColorTokens.Card),
                                elevation = CardDefaults.cardElevation(Dimensions.cardElevation)
                            ) {
                                Column(modifier = Modifier.padding(Dimensions.md)) {
                                    Text(
                                        text = analytics.match.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = ColorTokens.TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(Dimensions.sm))
                                    AnalyticsStatRow("Total Runs", analytics.totalRuns.toString())
                                    AnalyticsStatRow(
                                        "Avg per Over",
                                        String.format("%.1f", analytics.averagePerOver)
                                    )
                                    AnalyticsStatRow("Wickets", analytics.totalWickets.toString())
                                    analytics.bestOver?.let {
                                        AnalyticsStatRow(
                                            "Best Over",
                                            "Over ${it.overNumber} — ${it.runs} runs"
                                        )
                                    }
                                    AnalyticsStatRow("Stability", analytics.stabilityScore)
                                    Spacer(modifier = Modifier.height(Dimensions.sm))
                                    TempoGraph(
                                        overs = analytics.overs,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            is AnalyticsState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = s.message,
                        color = ColorTokens.Error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun AnalyticsStatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = ColorTokens.TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = ColorTokens.TextPrimary
        )
    }
}