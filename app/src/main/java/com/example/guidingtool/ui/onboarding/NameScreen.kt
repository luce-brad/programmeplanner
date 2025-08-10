package com.example.guidingtool.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameScreen(
    value: String,
    onChange: (String) -> Unit,
    onNext: () -> Unit
) {
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Welcome") }) },
        bottomBar = {
            Box(Modifier.fillMaxWidth().padding(16.dp)) {
                Button(
                    onClick = onNext,
                    enabled = value.isNotBlank(),
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) { Text("Next") }
            }
        }
    ) { p ->
        Column(Modifier.padding(p).padding(16.dp)) {
            Text("What’s your name?")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = value,
                onValueChange = onChange,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
