package com.example.guidingtool.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.guidingtool.util.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoneScreen(
    name: String,
    region: String,
    termStartMillis: Long?,
    termEndMillis: Long?,
    onFinish: () -> Unit
) {
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("All set!") }) },
        bottomBar = {
            Box(Modifier.fillMaxWidth().padding(16.dp)) {
                Button(onClick = onFinish, modifier = Modifier.align(Alignment.CenterEnd)) {
                    Text("Finish")
                }
            }
        }
    ) { p ->
        Column(Modifier.padding(p).padding(16.dp)) {
            Text("Name: $name")
            Text("Region: $region")
            Text("Term: ${termStartMillis?.let { formatDate(it) } ?: "-"} → ${termEndMillis?.let { formatDate(it) } ?: "-"}")
        }
    }
}
