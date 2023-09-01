package com.nextuple.nsf.util

import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object TimeUtils {

	fun calculateTimeDifferenceInSeconds(timestamp: String): Long? {
		return try {
			val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
			val parsedTimestamp = LocalDateTime.parse(timestamp, formatter).atZone(ZoneOffset.UTC).toInstant()
			val currentTimestamp = Instant.now()

			val duration = Duration.between(parsedTimestamp, currentTimestamp)
			duration.seconds
		} catch (e: Exception) {
			null
		}
	}

	fun formatTime(seconds: Long?): String {
		if (seconds == null) {
			return "-- : --"
		}
		val hours = seconds / 3600
		val minutes = (seconds % 3600) / 60
		val remainingSeconds = seconds % 60
		if (hours == 0L) {
			return "%02d:%02d".format(minutes, remainingSeconds)
		}
		return "%02d:%02d:%02d".format(hours, minutes, remainingSeconds)
	}
}
