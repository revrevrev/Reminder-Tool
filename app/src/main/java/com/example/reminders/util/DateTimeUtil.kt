package com.example.reminders.util

import com.example.reminders.data.model.Reminder
import com.example.reminders.data.model.ReminderType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

object DateTimeUtil {

    // Returns the epoch-ms when this reminder should next fire (or snoozed time if set)
    fun nextFireTimeMs(reminder: Reminder): Long {
        reminder.snoozeUntil?.let { return it }
        val now = System.currentTimeMillis()
        return when (reminder.type) {
            ReminderType.ONCE -> {
                val dt = parseDateTimeMs(reminder.date!!, reminder.time)
                dt
            }
            ReminderType.WEEKLY -> nextWeeklyOccurrenceMs(reminder, after = now)
        }
    }

    // Returns the epoch-ms of the occurrence being completed (for weekly done = skipUntil target).
    // Uses the occurrence NEAREST to nowMs so that marking done before the alarm fires still
    // skips this week's occurrence, not last week's.
    fun currentOccurrenceMs(reminder: Reminder, nowMs: Long = System.currentTimeMillis()): Long {
        if (reminder.type == ReminderType.ONCE) return parseDateTimeMs(reminder.date!!, reminder.time)
        return nearestWeeklyOccurrenceMs(reminder, nowMs)
    }

    // Returns the weekly occurrence whose epoch-ms is closest to nowMs (past or future).
    private fun nearestWeeklyOccurrenceMs(reminder: Reminder, nowMs: Long): Long {
        val day = reminder.day ?: 0
        val time = LocalTime.parse(reminder.time)
        val now = LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(nowMs), ZoneId.systemDefault())
        val targetDow = intToDayOfWeek(day)

        // Most-recent occurrence at or before nowMs
        var past = now.with(TemporalAdjusters.previousOrSame(targetDow))
            .withHour(time.hour).withMinute(time.minute).withSecond(0).withNano(0)
        if (past.isAfter(now)) past = past.minusWeeks(1)

        // Next occurrence strictly after nowMs
        var future = now.with(TemporalAdjusters.nextOrSame(targetDow))
            .withHour(time.hour).withMinute(time.minute).withSecond(0).withNano(0)
        if (!future.isAfter(now)) future = future.plusWeeks(1)

        val pastMs = past.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val futureMs = future.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        return if (nowMs - pastMs <= futureMs - nowMs) pastMs else futureMs
    }

    // Next occurrence strictly after `after`, respecting skipUntil
    fun nextWeeklyOccurrenceMs(reminder: Reminder, after: Long = System.currentTimeMillis()): Long {
        val day = reminder.day ?: 0   // 0=Sun..6=Sat
        val time = LocalTime.parse(reminder.time)
        val zonedAfter = LocalDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(after), ZoneId.systemDefault()
        )
        // Map 0=Sun..6=Sat to java.time DayOfWeek (SUNDAY=7 in ISO, but we handle manually)
        val targetDow = intToDayOfWeek(day)

        var candidate = zonedAfter.with(TemporalAdjusters.nextOrSame(targetDow))
            .withHour(time.hour).withMinute(time.minute).withSecond(0).withNano(0)

        if (!candidate.isAfter(zonedAfter)) {
            candidate = candidate.with(TemporalAdjusters.next(targetDow))
        }

        // Advance past skipUntil if needed
        reminder.skipUntil?.let { skipUntil ->
            val skipDt = LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(skipUntil), ZoneId.systemDefault())
            while (!candidate.isAfter(skipDt)) {
                candidate = candidate.with(TemporalAdjusters.next(targetDow))
            }
        }

        return candidate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    private fun latestWeeklyOccurrenceAtOrBefore(reminder: Reminder, before: Long): Long {
        val day = reminder.day ?: 0
        val time = LocalTime.parse(reminder.time)
        val zonedBefore = LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(before), ZoneId.systemDefault())
        val targetDow = intToDayOfWeek(day)

        var candidate = zonedBefore.with(TemporalAdjusters.previousOrSame(targetDow))
            .withHour(time.hour).withMinute(time.minute).withSecond(0).withNano(0)

        if (candidate.isAfter(zonedBefore)) {
            candidate = candidate.with(TemporalAdjusters.previous(targetDow))
        }

        return candidate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    private fun intToDayOfWeek(day: Int): DayOfWeek = when (day) {
        0 -> DayOfWeek.SUNDAY
        1 -> DayOfWeek.MONDAY
        2 -> DayOfWeek.TUESDAY
        3 -> DayOfWeek.WEDNESDAY
        4 -> DayOfWeek.THURSDAY
        5 -> DayOfWeek.FRIDAY
        6 -> DayOfWeek.SATURDAY
        else -> DayOfWeek.SUNDAY
    }

    fun parseDateTimeMs(date: String, time: String): Long {
        val ld = LocalDate.parse(date)
        val lt = LocalTime.parse(time)
        return LocalDateTime.of(ld, lt).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    // Relative countdown string in Hebrew (e.g. "בעוד 2 ש׳ ו-10 דק׳")
    fun relativeCountdown(fireTimeMs: Long): String {
        val diffMs = fireTimeMs - System.currentTimeMillis()
        if (diffMs <= 0) return "עכשיו"
        val totalMins = (diffMs / 60_000).toInt()
        val hours = totalMins / 60
        val mins = totalMins % 60
        return when {
            hours == 0 -> "בעוד $mins דק׳"
            mins == 0 -> "בעוד $hours ש׳"
            else -> "בעוד $hours ש׳ ו-$mins דק׳"
        }
    }

    // Hebrew day label for a list row (e.g. "מחר", "ה׳ 15.10", "כל ד׳")
    fun hebrewDayLabel(reminder: Reminder, nowMs: Long = System.currentTimeMillis()): String {
        if (reminder.type == ReminderType.WEEKLY) {
            return "כל ${shortHebrewDow(reminder.day ?: 0)}׳"
        }
        val fireMs = nextFireTimeMs(reminder)
        val fireDate = LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(fireMs), ZoneId.systemDefault()).toLocalDate()
        val today = LocalDate.now()
        return when {
            fireDate == today -> "היום"
            fireDate == today.plusDays(1) -> "מחר"
            else -> {
                val dow = shortHebrewDow(fireDate.dayOfWeek.value % 7) // convert ISO to 0=Sun
                val d = fireDate.dayOfMonth
                val m = fireDate.monthValue
                "$dow׳ $d.$m"
            }
        }
    }

    fun hebrewDateFull(dateMs: Long): String {
        val dt = LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(dateMs), ZoneId.systemDefault())
        val dayName = hebrewDayOfWeek(dt.dayOfWeek)
        val monthName = hebrewMonth(dt.monthValue)
        return "יום $dayName, ${dt.dayOfMonth} ב$monthName"
    }

    fun formatTime(timeMs: Long): String {
        val dt = LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(timeMs), ZoneId.systemDefault())
        return "%02d:%02d".format(dt.hour, dt.minute)
    }

    fun todayDateString(): String = LocalDate.now().toString()
    fun tomorrowDateString(): String = LocalDate.now().plusDays(1).toString()

    fun isInPast(date: String, time: String): Boolean {
        val dt = LocalDateTime.of(LocalDate.parse(date), LocalTime.parse(time))
        return dt.isBefore(LocalDateTime.now())
    }

    fun nextRoundHour(after: LocalDateTime = LocalDateTime.now()): LocalDateTime =
        after.truncatedTo(ChronoUnit.HOURS).plusHours(1)

    fun nextRoundHourDateString(after: LocalDateTime = LocalDateTime.now()): String =
        nextRoundHour(after).toLocalDate().toString()

    fun nextRoundHourTimeString(after: LocalDateTime = LocalDateTime.now()): String =
        "%02d:00".format(nextRoundHour(after).hour)

    private fun shortHebrewDow(day: Int) = when (day) { // 0=Sun
        0 -> "א"; 1 -> "ב"; 2 -> "ג"; 3 -> "ד"; 4 -> "ה"; 5 -> "ו"; else -> "ש"
    }

    private fun hebrewDayOfWeek(dow: DayOfWeek) = when (dow) {
        DayOfWeek.SUNDAY -> "ראשון"
        DayOfWeek.MONDAY -> "שני"
        DayOfWeek.TUESDAY -> "שלישי"
        DayOfWeek.WEDNESDAY -> "רביעי"
        DayOfWeek.THURSDAY -> "חמישי"
        DayOfWeek.FRIDAY -> "שישי"
        DayOfWeek.SATURDAY -> "שבת"
    }

    fun hebrewDayOfWeekFull(day: Int) = when (day) { // 0=Sun
        0 -> "ראשון"; 1 -> "שני"; 2 -> "שלישי"; 3 -> "רביעי"
        4 -> "חמישי"; 5 -> "שישי"; else -> "שבת"
    }

    private fun hebrewMonth(m: Int) = when (m) {
        1 -> "ינואר"; 2 -> "פברואר"; 3 -> "מרץ"; 4 -> "אפריל"
        5 -> "מאי"; 6 -> "יוני"; 7 -> "יולי"; 8 -> "אוגוסט"
        9 -> "ספטמבר"; 10 -> "אוקטובר"; 11 -> "נובמבר"; else -> "דצמבר"
    }

    // Returns (label, dayIndex) pairs in the order they should appear in the WeekdayPicker.
    // Index 0 renders on the right in RTL, so Sunday (א, index 0) must come first.
    fun weekdayPickerOrder(): List<Pair<String, Int>> = listOf(
        "א׳" to 0, "ב׳" to 1, "ג׳" to 2, "ד׳" to 3, "ה׳" to 4, "ו׳" to 5, "ש׳" to 6
    )

    fun summaryPill(type: ReminderType, date: String?, day: Int?, time: String): String {
        return when (type) {
            ReminderType.ONCE -> {
                if (date == null) return ""
                val ld = LocalDate.parse(date)
                val dow = hebrewDayOfWeek(ld.dayOfWeek)
                val month = hebrewMonth(ld.monthValue)
                "יום $dow, ${ld.dayOfMonth} ב$month ב-$time"
            }
            ReminderType.WEEKLY -> "כל יום ${hebrewDayOfWeekFull(day ?: 0)} ב-$time"
        }
    }
}
