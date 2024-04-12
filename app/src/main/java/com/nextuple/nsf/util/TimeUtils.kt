package com.nextuple.nsf.util

import com.google.protobuf.Timestamp
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

object TimeUtils {

	private const val OUTPUT_FORMAT = "MMMM d, yyyy"

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

	fun Long.formatTime(): String {
		val hours = this / 3600
		val minutes = (this % 3600) / 60
		val remainingSeconds = this % 60
		if (hours == 0L) {
			return "%02d:%02d".format(minutes, remainingSeconds)
		}
		return "%02d:%02d:%02d".format(hours, minutes, remainingSeconds)
	}

	fun formatTimeStamp(
		timeStamp: Instant,
		outputPattern: String = OUTPUT_FORMAT
	): String {
		val outputFormatter = DateTimeFormatter.ofPattern(outputPattern, Locale.ENGLISH)
		val dateTime = LocalDateTime.ofInstant(timeStamp, ZoneId.systemDefault())
		return dateTime.format(outputFormatter)
	}
	fun getProtoTimestamp(instant: Instant = Instant.now()): Timestamp =
		Timestamp.newBuilder().setSeconds(instant.epochSecond).setNanos(instant.nano).build()
}
