package com.wawakaka.jst.base.utils

import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import java.util.*

/**
 * Created by wawakaka on 11/25/2017.
 */
object DateUtils {


    // valid format datetime 2014-01-01T10:00:00+07:00 to save
    private val indonesiaLocale = Locale("in")

    private const val DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
    private val dateTimeFormatter = DateTimeFormat
        .forPattern(DATE_TIME_FORMAT_PATTERN)
        .withLocale(indonesiaLocale)

    private const val DATE_FORMAT_PATTERN = "EEEE, dd MMMM yyyy"
    private val dateFormatter = DateTimeFormat
        .forPattern(DATE_FORMAT_PATTERN)
        .withLocale(indonesiaLocale)

    private const val SHORT_DATE_FORMAT_PATTERN = "dd MMMM yyyy"
    private val shortDateFormatter = DateTimeFormat
        .forPattern(SHORT_DATE_FORMAT_PATTERN)
        .withLocale(indonesiaLocale)

    private const val DATE_OF_WEEK_FORMAT_PATTERN = "EEEE"
    private val dateOfWeekFormatPattern = DateTimeFormat
        .forPattern(DATE_OF_WEEK_FORMAT_PATTERN)
        .withLocale(indonesiaLocale)

    private const val SHORT_TIME_FORMAT_PATTERN = "HH:mm"
    private val shortTransactionTimeFormatter = DateTimeFormat
        .forPattern(SHORT_TIME_FORMAT_PATTERN)
        .withLocale(indonesiaLocale)

    fun getFormattedDate(iso8601time: String): String {
        if (iso8601time.isEmpty()) {
            return ""
        }
        return dateFormatter.print(getDateFromIso8601Time(iso8601time))
    }

    fun getFormattedDate(iso8601time: DateTime): String = dateFormatter.print(iso8601time)

    fun getDateFromIso8601Time(iso8601time: String): DateTime =
        dateTimeFormatter.parseDateTime(iso8601time)

    fun getCurrentDateInIso8601String(): String = dateTimeFormatter.print(DateTime())

    fun isToday(dateIso8601: String): Boolean {
        if (dateIso8601.isBlank()) {
            return false
        }
        return getDateFromIso8601Time(dateIso8601).toLocalDate() == LocalDate()
    }

    fun isCurrentHour(dateIso8601: String): Boolean {
        if (dateIso8601.isBlank()) {
            return false
        }

        val currentTime = LocalDateTime()
        val time = getDateFromIso8601Time(dateIso8601)

        return time.hourOfDay == currentTime.hourOfDay
    }

    fun isYesterday(dateIso8601: String): Boolean {
        if (dateIso8601.isBlank()) {
            return false
        }
        return getDateFromIso8601Time(dateIso8601).toLocalDate() == LocalDate.now().minusDays(1)
    }

    fun isPast(dateIso8601: String): Boolean {
        if (dateIso8601.isBlank()) {
            return false
        }
        return getDateFromIso8601Time(dateIso8601).toLocalDate() < LocalDate.now()
    }

    fun getDayName(dateIso8601: String): String = getDayOfWeek(getDateFromIso8601Time(dateIso8601).dayOfWeek)

    private fun getDayOfWeek(date: Int): String = when (date) {
        1 -> Hari.SENIN
        2 -> Hari.SELASA
        3 -> Hari.RABU
        4 -> Hari.KAMIS
        5 -> Hari.JUMAT
        6 -> Hari.SABTU
        7 -> Hari.MINGGU
        else -> ""
    }

    fun getShortDate(dateIso8601: String): String =
        shortDateFormatter.print(getDateFromIso8601Time(dateIso8601))

    fun getCurrentShortDate(): String = shortDateFormatter.print(DateTime())

    fun getIso8601String(dateTime: DateTime): String = dateTimeFormatter.print(dateTime)

    fun getIso8601Time(dateIso8601: String) = shortTransactionTimeFormatter.print(getDateFromIso8601Time(dateIso8601))

    fun isFirstCalendarMoreThanSecond(first: Calendar, second: Calendar): Boolean = when {
        first.get(Calendar.YEAR) < second.get(Calendar.YEAR) -> false
        first.get(Calendar.YEAR) == second.get(Calendar.YEAR) -> when {
            first.get(Calendar.MONTH) < second.get(Calendar.MONTH) -> false
            first.get(Calendar.MONTH) == second.get(Calendar.MONTH) -> {
                first.get(Calendar.DAY_OF_MONTH) >= second.get(Calendar.DAY_OF_MONTH)
            }
            else -> true
        }
        else -> true
    }
}