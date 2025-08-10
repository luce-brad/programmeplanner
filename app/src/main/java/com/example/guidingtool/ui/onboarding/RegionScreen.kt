package com.example.guidingtool.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionScreen(
    value: String,
    onChange: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val regions = listOf(
        "North West", "North East", "Midlands", "London & South East",
        "South West", "Scotland", "Wales", "Northern Ireland"
    )
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Your Region") }) },
        bottomBar = {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = onBack) { Text("Back") }
                Button(onClick = onNext, enabled = value.isNotBlank()) { Text("Next") }
            }
        }
    ) { p ->
        Column(Modifier.padding(p).padding(16.dp)) {
            Text("Select your region")
            Spacer(Modifier.height(8.dp))

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = value,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Region") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    regions.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = { onChange(it); expanded = false }
                        )
                    }
                }
            }
        }
    }
}
