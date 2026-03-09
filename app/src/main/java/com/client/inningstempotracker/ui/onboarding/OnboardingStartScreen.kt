package com.client.inningstempotracker.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.client.inningstempotracker.theme.ColorTokens
import com.client.inningstempotracker.theme.Dimensions
import org.koin.androidx.compose.koinViewModel

@Composable
fun OnboardingStartScreen(
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    val viewModel: OnboardingViewModel = koinViewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SportsCricket,
            contentDescription = null,
            tint = ColorTokens.PrimaryAccent,
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(Dimensions.xl))

        Text(
            text = "Innings Tempo Tracker",
            style = MaterialTheme.typography.displayLarge,
            color = ColorTokens.TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimensions.md))

        Text(
            text = "Analyze the pace of cricket innings over by over. Track runs, wickets and phases to uncover key patterns.",
            style = MaterialTheme.typography.bodyLarge,
            color = ColorTokens.TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimensions.xl))

        Text(
            text = "1 / 3",
            style = MaterialTheme.typography.bodySmall,
            color = ColorTokens.TextSecondary
        )

        Spacer(modifier = Modifier.height(Dimensions.lg))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Next")
        }

        Spacer(modifier = Modifier.height(Dimensions.sm))

        TextButton(
            onClick = {
                viewModel.completeOnboarding { onSkip() }
            }
        ) {
            Text(
                text = "Skip",
                color = ColorTokens.TextSecondary
            )
        }
    }
}