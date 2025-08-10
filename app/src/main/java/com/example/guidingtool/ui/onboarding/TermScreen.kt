package com.example.guidingtool.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.guidingtool.util.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermScreen(
    startMillis: Long?,
    endMillis: Long?,
    onSet: (Long?, Long?) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    var openStart by remember { mutableStateOf(false) }
    var openEnd by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Term dates") }) },
        bottomBar = {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = onBack) { Text("Back") }
                Button(
                    onClick = onNext,
                    enabled = startMillis != null && endMillis != null && endMillis >= startMillis
                ) { Text("Next") }
            }
        }
    ) { p ->
        Column(Modifier.padding(p).padding(16.dp)) {
            Text("Pick your term start and end")
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { openStart = true }) {
                    Text(startMillis?.let { formatDate(it) } ?: "Start date")
                }
                OutlinedButton(onClick = { openEnd = true }) {
                    Text(endMillis?.let { formatDate(it) } ?: "End date")
                }
            }
        }

        if (openStart) {
            val state = rememberDatePickerState(initialSelectedDateMillis = startMillis)
            DatePickerDialog(
                onDismissRequest = { openStart = false },
                confirmButton = {
                    TextButton(onClick = {
                        onSet(state.selectedDateMillis, endMillis)
                        openStart = false
                    }) { Text("OK") }
                },
                dismissButton = { TextButton(onClick = { openStart = false }) { Text("Cancel") } }
            ) { DatePicker(state = state) }
        }

        if (openEnd) {
            val state = rememberDatePickerState(initialSelectedDateMillis = endMillis)
            DatePickerDialog(
                onDismissRequest = { openEnd = false },
                confirmButton = {
                    TextButton(onClick = {
                        onSet(startMillis, state.selectedDateMillis)
                        openEnd = false
                    }) { Text("OK") }
                },
                dismissButton = { TextButton(onClick = { openEnd = false }) { Text("Cancel") } }
            ) { DatePicker(state = state) }
        }
    }
}
