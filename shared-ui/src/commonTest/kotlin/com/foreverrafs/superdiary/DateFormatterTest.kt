package com.foreverrafs.superdiary

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.foreverrafs.superdiary.ui.format
import kotlin.test.Test
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

class DateFormatterTest {
    @Test
    fun `Verify date formatting works properly`() {
        val date = LocalDate(2020, 10, 10)
        val formattedDate = date.format("YYYY")

        assertThat(formattedDate).isEqualTo("2020")
    }

    @Test
    fun `Verify datetime formatting works properly`() {
        val dateTime = LocalDate(2020, 10, 10)
            .atStartOfDayIn(TimeZone.UTC)
            .toLocalDateTime(TimeZone.UTC)

        val formattedDate = dateTime.format("YYYY")

        assertThat(formattedDate).isEqualTo("2020")
    }
}
