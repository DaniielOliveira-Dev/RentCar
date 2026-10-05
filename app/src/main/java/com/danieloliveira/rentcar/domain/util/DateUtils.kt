package com.danieloliveira.rentcar.domain.util

import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DateUtils {
    private val displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun format(date: LocalDate): String = date.format(displayFormatter)

    fun formatIsoDate(isoDate: String): String =
        runCatching { format(LocalDate.parse(isoDate)) }.getOrDefault(isoDate)

    fun daysBetween(startDate: LocalDate, endDate: LocalDate): Int =
        ChronoUnit.DAYS.between(startDate, endDate).toInt()

    fun daysUntil(isoDate: String): Int =
        runCatching {
            ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(isoDate)).toInt()
        }.getOrDefault(0)

    fun toUtcMillis(date: LocalDate): Long =
        date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    fun fromUtcMillis(value: Long): LocalDate =
        java.time.Instant.ofEpochMilli(value).atZone(ZoneOffset.UTC).toLocalDate()
}
