package com.client.inningstempotracker.ui.match

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.client.inningstempotracker.theme.ColorTokens
import com.client.inningstempotracker.theme.Dimensions
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMatchScreen(
    onMatchCreated: (Int) -> Unit,
    onBack: () -> Unit
) {
    val viewModel: CreateMatchViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val matchName by viewModel.matchName.collectAsState()
    val matchFormat by viewModel.matchFormat.collectAsState()
    val matchDate by viewModel.matchDate.collectAsState()
    val nameError by viewModel.nameError.collectAsState()
    val dateError by viewModel.dateError.collectAsState()

    var formatExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        if (state is CreateMatchState.Success) {
            onMatchCreated((state as CreateMatchState.Success).matchId)
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Match") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Dimensions.md),
            verticalArrangement = Arrangement.spacedBy(Dimensions.md)
        ) {
            OutlinedTextField(
                value = matchName,
                onValueChange = viewModel::onNameChange,
                label = { Text("Match Name") },
                isError = nameError != null,
                supportingText = { nameError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = formatExpanded,
                onExpandedChange = { formatExpanded = it }
            ) {
                OutlinedTextField(
                    value = matchFormat,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Format") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = formatExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = formatExpanded,
                    onDismissRequest = { formatExpanded = false }
                ) {
                    viewModel.formats.forEach { format ->
                        DropdownMenuItem(
                            text = { Text(format) },
                            onClick = {
                                viewModel.onFormatChange(format)
                                formatExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = matchDate,
                onValueChange = viewModel::onDateChange,
                label = { Text("Date (yyyy-MM-dd)") },
                isError = dateError != null,
                supportingText = { dateError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Dimensions.sm))

            when (state) {
                is CreateMatchState.Loading -> {
                    CircularProgressIndicator(
                        color = ColorTokens.PrimaryAccent,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                is CreateMatchState.Error -> {
                    Text(
                        text = (state as CreateMatchState.Error).message,
                        color = ColorTokens.Error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Button(
                        onClick = { viewModel.createMatch() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Retry")
                    }
                }
                else -> {
                    Button(
                        onClick = { viewModel.createMatch() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Create Match")
                    }
                }
            }
        }
    }
}