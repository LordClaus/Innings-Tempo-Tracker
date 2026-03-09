package com.client.inningstempotracker.ui.detail

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.client.inningstempotracker.theme.ColorTokens
import com.client.inningstempotracker.theme.Dimensions
import com.client.inningstempotracker.ui.components.TempoGraph
import com.client.inningstempotracker.utils.DateFormatter
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InningsDetailScreen(
    matchId: Int,
    onEdit: (Int) -> Unit,
    onBack: () -> Unit
) {
    val viewModel: InningsDetailViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(matchId) {
        viewModel.init(matchId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Innings Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onEdit(matchId) }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = ColorTokens.PrimaryAccent
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val s = state) {
            is InningsDetailState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ColorTokens.PrimaryAccent)
                }
            }

            is InningsDetailState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(Dimensions.md),
                    verticalArrangement = Arrangement.spacedBy(Dimensions.md)
                ) {
                    item {
                        Text(
                            text = s.match.name,
                            style = MaterialTheme.typography.displayLarge,
                            color = ColorTokens.TextPrimary
                        )
                        Text(
                            text = "${s.match.format} • ${DateFormatter.formatForDisplay(s.match.date)}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = ColorTokens.TextSecondary
                        )
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ColorTokens.Card),
                            elevation = CardDefaults.cardElevation(Dimensions.cardElevation)
                        ) {
                            Column(modifier = Modifier.padding(Dimensions.md)) {
                                Text(
                                    text = "Statistics",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = ColorTokens.TextPrimary
                                )
                                Spacer(modifier = Modifier.height(Dimensions.sm))
                                StatItem("Total Runs", s.totalRuns.toString())
                                StatItem("Average per Over", String.format("%.1f", s.averagePerOver))
                                StatItem("Total Wickets", s.totalWickets.toString())
                                s.bestOver?.let {
                                    StatItem("Best Over", "Over ${it.overNumber} — ${it.runs} runs")
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ColorTokens.Card),
                            elevation = CardDefaults.cardElevation(Dimensions.cardElevation)
                        ) {
                            Column(modifier = Modifier.padding(Dimensions.md)) {
                                Text(
                                    text = "Tempo Graph",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = ColorTokens.TextPrimary
                                )
                                Spacer(modifier = Modifier.height(Dimensions.sm))
                                TempoGraph(
                                    overs = s.overs,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                )
                            }
                        }
                    }

                    items(s.overs) { over ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ColorTokens.Card),
                            elevation = CardDefaults.cardElevation(Dimensions.cardElevation)
                        ) {
                            Column(modifier = Modifier.padding(Dimensions.md)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Over ${over.overNumber}",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = ColorTokens.TextPrimary
                                    )
                                    Text(
                                        text = "${over.runs} runs",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = ColorTokens.PrimaryAccent
                                    )
                                }
                                Text(
                                    text = "${over.phaseType}${if (over.wicket) " • Wicket" else ""}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = ColorTokens.TextSecondary
                                )
                                over.note?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ColorTokens.TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            is InningsDetailState.Error -> {
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
private fun StatItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
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