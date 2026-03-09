package com.client.inningstempotracker.ui.over

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.client.inningstempotracker.data.db.OverEntity
import com.client.inningstempotracker.theme.ColorTokens
import com.client.inningstempotracker.theme.Dimensions
import com.client.inningstempotracker.ui.components.OverInputForm
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverInputScreen(
    matchId: Int,
    onDone: (Int) -> Unit,
    onBack: () -> Unit
) {
    val viewModel: OverInputViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val showForm by viewModel.showForm.collectAsState()
    val runs by viewModel.runs.collectAsState()
    val wicket by viewModel.wicket.collectAsState()
    val phaseType by viewModel.phaseType.collectAsState()
    val note by viewModel.note.collectAsState()
    val runsError by viewModel.runsError.collectAsState()
    val editingOver by viewModel.editingOver.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(matchId) {
        viewModel.init(matchId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Over Input") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onDone(matchId) }) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Done",
                            tint = ColorTokens.PrimaryAccent
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddForm() },
                containerColor = ColorTokens.PrimaryAccent
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Over", tint = ColorTokens.Card)
            }
        }
    ) { paddingValues ->
        when (val s = state) {
            is OverInputState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ColorTokens.PrimaryAccent)
                }
            }

            is OverInputState.Success -> {
                if (s.overs.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(Dimensions.lg),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No overs yet",
                            style = MaterialTheme.typography.headlineMedium,
                            color = ColorTokens.TextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Tap + to add the first over",
                            style = MaterialTheme.typography.bodyLarge,
                            color = ColorTokens.TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(Dimensions.md),
                        verticalArrangement = Arrangement.spacedBy(Dimensions.sm)
                    ) {
                        items(s.overs) { over ->
                            OverCard(
                                over = over,
                                onEdit = { viewModel.showEditForm(over) },
                                onDelete = { viewModel.deleteOver(over) }
                            )
                        }
                    }
                }
            }

            is OverInputState.Error -> {
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

        if (showForm) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.hideForm() },
                sheetState = sheetState
            ) {
                val currentOvers = (state as? OverInputState.Success)?.overs ?: emptyList()
                val overNumber = editingOver?.overNumber ?: (currentOvers.size + 1)
                OverInputForm(
                    overNumber = overNumber,
                    runs = runs,
                    wicket = wicket,
                    phaseType = phaseType,
                    note = note,
                    runsError = runsError,
                    onRunsChange = viewModel::onRunsChange,
                    onWicketChange = viewModel::onWicketChange,
                    onPhaseTypeChange = viewModel::onPhaseTypeChange,
                    onNoteChange = viewModel::onNoteChange,
                    onSave = { viewModel.saveOver() },
                    onCancel = { viewModel.hideForm() }
                )
            }
        }
    }
}

@Composable
private fun OverCard(
    over: OverEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ColorTokens.Card),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.cardElevation)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Over ${over.overNumber}",
                    style = MaterialTheme.typography.titleLarge,
                    color = ColorTokens.TextPrimary
                )
                Text(
                    text = "${over.runs} runs • ${over.phaseType} ${if (over.wicket) "• Wicket" else ""}",
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
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = ColorTokens.PrimaryAccent)
            }
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ColorTokens.Error)
            }
        }
    }
}