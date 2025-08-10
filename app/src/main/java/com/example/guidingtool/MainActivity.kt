package com.example.guidingtool

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.guidingtool.nav.Routes
import com.example.guidingtool.onboarding.OnboardingViewModel
import com.example.guidingtool.ui.onboarding.DoneScreen
import com.example.guidingtool.ui.onboarding.NameScreen
import com.example.guidingtool.ui.onboarding.RegionScreen
import com.example.guidingtool.ui.onboarding.TermScreen

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
                            name = vm.ui.name,
                            region = vm.ui.region,
                            termStartMillis = vm.ui.termStartMillis,
                            termEndMillis = vm.ui.termEndMillis,
                            onFinish = {
                                // TODO: save to DataStore and go to Home
                            }
                        )
                    }

                }
            }
        }
    }
}

