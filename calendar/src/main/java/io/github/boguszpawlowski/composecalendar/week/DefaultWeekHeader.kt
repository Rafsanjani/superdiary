package io.github.boguszpawlowski.composecalendar.week

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import java.time.DayOfWeek
import java.time.format.TextStyle.SHORT
import java.util.Locale
import kotlin.DeprecationLevel.WARNING

@Composable
@Deprecated(
    level = WARNING,
    replaceWith = ReplaceWith(
        "DefaultDaysOfWeekHeader(daysOfWeek, modifier)",
        "io.github.boguszpawlowski.composecalendar.week.DefaultDaysOfWeekHeader",
    ),
    message = "Replace with DefaultDaysOfWeekHeader, DefaultWeekHeader will be removed in future versions"
)
public fun DefaultWeekHeader(
    daysOfWeek: List<DayOfWeek>,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        daysOfWeek.forEach { dayOfWeek ->
            Text(
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(SHORT, Locale.getDefault()),
                modifier = Modifier
                  .weight(1f)
                  .wrapContentHeight()
            )
        }
    }
}

@Composable
public fun DefaultDaysOfWeekHeader(
    daysOfWeek: List<DayOfWeek>,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        daysOfWeek.forEach { dayOfWeek ->
            Text(
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(SHORT, Locale.getDefault()),
                modifier = Modifier
                  .weight(1f)
                  .wrapContentHeight(),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

internal fun <T> Array<T>.rotateRight(n: Int): List<T> = takeLast(n) + dropLast(n)