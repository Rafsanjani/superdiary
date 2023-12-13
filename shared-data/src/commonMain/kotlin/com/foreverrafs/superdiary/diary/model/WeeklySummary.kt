package com.foreverrafs.superdiary.diary.model

import kotlinx.datetime.Instant

data class WeeklySummary(
    val summary: String,
    val date: Instant,
)