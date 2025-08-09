package com.example.guidingtool

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// -------------------- State + VM --------------------
data class OnboardingState(
    val name: String = "",
    val region: String = "",
    val termStartMillis: Long? = null,
    val termEndMillis: Long? = null
)

class OnboardingViewModel : ViewModel() {
    var ui by mutableStateOf(OnboardingState())
        private set

    fun setName(v: String) { ui = ui.copy(name = v) }
    fun setRegion(v: String) { ui = ui.copy(region = v) }
    fun setTerm(start: Long?, end: Long?) { ui = ui.copy(termStartMillis = start, termEndMillis = end) }

    fun canGoRegion() = ui.name.isNotBlank()
    fun canGoTerm() = ui.region.isNotBlank()
    fun canFinish(): Boolean {
        val s = ui.termStartMillis
        val e = ui.termEndMillis
        return s != null && e != null && e >= s
    }
}

// -------------------- Activity --------------------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val nav = rememberNavController()
                val vm: OnboardingViewModel = viewModel()

                NavHost(navController = nav, startDestination = Routes.Name) {
                    composable(Routes.Name) {
                        NameScreen(
                            value = vm.ui.name,
                            onChange = vm::setName,
                            onNext = { if (vm.canGoRegion()) nav.navigate(Routes.Region) }
                        )
                    }
                    composable(Routes.Region) {
                        RegionScreen(
                            value = vm.ui.region,
                            onChange = vm::setRegion,
                            onBack = { nav.popBackStack() },
                            onNext = { if (vm.canGoTerm()) nav.navigate(Routes.Term) }
                        )
                    }
                    composable(Routes.Term) {
                        TermScreen(
                            startMillis = vm.ui.termStartMillis,
                            endMillis = vm.ui.termEndMillis,
                            onSet = vm::setTerm,
                            onBack = { nav.popBackStack() },
                            onNext = { if (vm.canFinish()) nav.navigate(Routes.Done) }
                        )
                    }
                    composable(Routes.Done) {
                        DoneScreen(
                            state = vm.ui,
                            onFinish = {
                                // Later: save to DataStore, then navigate to Home()
                                // For now, just pop back or show a snackbar, etc.
                            }
                        )
                    }
                }
            }
        }
    }
}

// -------------------- Screens --------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameScreen(value: String, onChange: (String) -> Unit, onNext: () -> Unit) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionScreen(value: String, onChange: (String) -> Unit, onBack: () -> Unit, onNext: () -> Unit) {
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
                dismissButton = {
                    TextButton(onClick = { openStart = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = state)
            }
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
                dismissButton = {
                    TextButton(onClick = { openEnd = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = state)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoneScreen(state: OnboardingState, onFinish: () -> Unit) {
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
            Text("Name: ${state.name}")
            Text("Region: ${state.region}")
            Text("Term: ${state.termStartMillis?.let { formatDate(it) } ?: "-"} → ${state.termEndMillis?.let { formatDate(it) } ?: "-"}")
        }
    }
}

// -------------------- Helpers --------------------
private fun formatDate(millis: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date(millis))
}
