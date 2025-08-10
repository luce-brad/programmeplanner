package com.example.guidingtool.onboarding

data class OnboardingState(
    val name: String = "",
    val region: String = "",
    val termStartMillis: Long? = null,
    val termEndMillis: Long? = null
)
