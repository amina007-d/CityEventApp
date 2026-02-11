package com.example.cityeventproject.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cityeventproject.domain.logic.Validators
import com.example.cityeventproject.ui.components.SimpleTopBar
import com.example.cityeventproject.ui.feed.FeedViewModel

/**
 * Ticketmaster-style Search / Filter screen.
 *
 * This screen does NOT fetch data directly. It updates FeedViewModel filters.
 * FeedViewModel is responsible for debounced keyword search + paging + robust UI states.
 */
@Composable
fun SearchScreen(
    onBack: () -> Unit,
    feedVm: FeedViewModel
) {
    var keyword by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("ALL") }
    var category by remember { mutableStateOf("All") }
    var datePreset by remember { mutableStateOf(DatePreset.AllDates) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        SimpleTopBar(title = "Search & Filters", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Search concerts & events from Ticketmaster",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = keyword,
                onValueChange = { keyword = it },
                label = { Text("Keyword (artist / event)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Divider()

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DropdownField(
                    modifier = Modifier.weight(1f),
                    label = "Country",
                    value = country,
                    options = Countries.options,
                    onSelected = { country = it }
                )
                DropdownField(
                    modifier = Modifier.weight(1f),
                    label = "Category",
                    value = category,
                    options = Categories.options,
                    onSelected = { category = it }
                )
            }

            DropdownField(
                modifier = Modifier.fillMaxWidth(),
                label = "Dates",
                value = datePreset.label,
                options = DatePreset.entries.map { it.label },
                onSelected = { selected ->
                    datePreset = DatePreset.entries.first { it.label == selected }
                }
            )

            if (error != null) {
                Text(text = error!!, color = MaterialTheme.colorScheme.error)
            } else {
                Text(
                    text = "Tip: keyword search is debounced (not on every keystroke).",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(6.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = {
                    keyword = ""
                    city = ""
                    country = "ALL"
                    category = "All"
                    datePreset = DatePreset.AllDates
                    error = null
                }) {
                    Text("Reset")
                }
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    val cc = country.takeIf { it != "ALL" }
                    val cat = category.takeIf { it != "All" }
                    val date = datePreset.toRangeUtcIso()

                    // Validation: countryCode must be 2 letters if set.
                    if (cc != null && !Validators.isValidCountryCode(cc)) {
                        error = "Country code must be 2 letters (e.g. US, GB, KZ)."
                        return@Button
                    }

                    error = null
                    feedVm.setFilters(
                        countryCode = cc,
                        city = city,
                        category = cat,
                        keyword = keyword,
                        startDateTime = date?.first,
                        endDateTime = date?.second
                    )
                    onBack()
                }) {
                    Text("Apply")
                }
            }
        }
    }
}

@Composable
private fun DropdownField(
    modifier: Modifier,
    label: String,
    value: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(6.dp))
        TextButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(value)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt) },
                    onClick = {
                        expanded = false
                        onSelected(opt)
                    }
                )
            }
        }
    }
}

private object Countries {
    // Keep it short but practical. Add more if you need.
    val options = listOf(
        "ALL",
        "US",
        "GB",
        "DE",
        "FR",
        "IT",
        "ES",
        "TR",
        "KZ",
        "RU",
        "AE"
    )
}

private object Categories {
    val options = listOf(
        "All",
        "Music",
        "Sports",
        "Arts & Theatre",
        "Film",
        "Miscellaneous"
    )
}

private enum class DatePreset(val label: String) {
    AllDates("All Dates"),
    ThisWeekend("This Weekend"),
    Next7Days("Next 7 days"),
    Next30Days("Next 30 days");

    /**
     * @return Pair(startIso, endIso) in UTC, or null for "All".
     */
    fun toRangeUtcIso(): Pair<String, String>? {
        val now = java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)
        return when (this) {
            AllDates -> null
            ThisWeekend -> {
                // Next Saturday 00:00 -> Monday 00:00
                val sat = now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SATURDAY))
                    .withHour(0).withMinute(0).withSecond(0).withNano(0)
                val mon = sat.plusDays(2) // Monday 00:00
                sat.toInstant().toString() to mon.toInstant().toString()
            }
            Next7Days -> {
                val end = now.plusDays(7)
                now.toInstant().toString() to end.toInstant().toString()
            }
            Next30Days -> {
                val end = now.plusDays(30)
                now.toInstant().toString() to end.toInstant().toString()
            }
        }
    }
}
