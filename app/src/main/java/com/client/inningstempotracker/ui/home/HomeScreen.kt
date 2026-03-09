package com.client.inningstempotracker.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
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
import com.client.inningstempotracker.theme.ColorTokens
import com.client.inningstempotracker.theme.Dimensions
import com.client.inningstempotracker.ui.components.MatchSummaryCard
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCreateMatch: () -> Unit,
    onMatchClick: (Int) -> Unit
) {
    val viewModel: HomeViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Innings Tempo Tracker",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateMatch,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New Match") },
                containerColor = ColorTokens.PrimaryAccent,
                contentColor = ColorTokens.Card
            )
        }
    ) { paddingValues ->
        when (val s = state) {
            is HomeState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = ColorTokens.PrimaryAccent)
                }
            }

            is HomeState.Success -> {
                if (s.recentMatches.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(Dimensions.lg),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No matches yet",
                            style = MaterialTheme.typography.headlineMedium,
                            color = ColorTokens.TextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(Dimensions.sm))
                        Text(
                            text = "Create your first match to start tracking innings tempo",
                            style = MaterialTheme.typography.bodyLarge,
                            color = ColorTokens.TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(Dimensions.lg))
                        Button(onClick = onCreateMatch) {
                            Text("Create Match")
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(Dimensions.md),
                        verticalArrangement = Arrangement.spacedBy(Dimensions.sm)
                    ) {
                        item {
                            Text(
                                text = "Recent Innings",
                                style = MaterialTheme.typography.headlineMedium,
                                color = ColorTokens.TextPrimary,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(Dimensions.sm))
                        }
                        items(s.recentMatches) { match ->
                            MatchSummaryCard(
                                match = match,
                                onClick = { onMatchClick(match.id) }
                            )
                        }
                    }
                }
            }

            is HomeState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(Dimensions.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Something went wrong",
                        style = MaterialTheme.typography.headlineMedium,
                        color = ColorTokens.Error
                    )
                    Spacer(modifier = Modifier.height(Dimensions.sm))
                    Text(
                        text = s.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = ColorTokens.TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}