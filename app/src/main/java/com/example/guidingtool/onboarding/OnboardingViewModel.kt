package com.example.guidingtool.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

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
