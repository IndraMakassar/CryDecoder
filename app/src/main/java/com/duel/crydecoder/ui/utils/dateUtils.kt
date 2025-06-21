package com.duel.crydecoder.ui.utils

import java.util.*
import java.util.concurrent.TimeUnit

fun getRelativeTime(timestamp: Long): String {
    val now = Date()
    val past = Date(timestamp)
    val diff = now.time - past.time

    val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)

    return when {
        seconds < 60 -> "now"
        minutes < 60 -> "$minutes ${if (minutes == 1L) "minute" else "minutes"} ago"
        hours < 24 -> {
            val calendarNow = Calendar.getInstance()
            val calendarPast = Calendar.getInstance().apply { time = past }

            if (calendarNow.get(Calendar.DAY_OF_YEAR) == calendarPast.get(Calendar.DAY_OF_YEAR)) {
                val hour = calendarPast.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')
                val minute = calendarPast.get(Calendar.MINUTE).toString().padStart(2, '0')
                "Today, $hour:$minute"
            } else {
                "$hours ${if (hours == 1L) "hour" else "hours"} ago"
            }
        }
        days == 1L -> "yesterday"
        days < 7 -> "$days days ago"
        days < 14 -> "a week ago"
        days < 30 -> "${days / 7} weeks ago"
        days < 365 -> "${days / 30} months ago"
        else -> "${days / 365} years ago"
    }
}
