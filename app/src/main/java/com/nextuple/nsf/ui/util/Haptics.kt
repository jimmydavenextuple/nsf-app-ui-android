package com.nextuple.nsf.ui.util

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.VibrationEffect
import android.os.Vibrator

class Haptics(
	private val vibrator: Vibrator
) {
	fun boop() = runCatching {
		ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME).run {
			startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 800)
			release()
		}
	}.getOrDefault(Unit)

	fun beep() = runCatching {
		ToneGenerator(AudioManager.STREAM_NOTIFICATION, ToneGenerator.MAX_VOLUME).run {
			startTone(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_NORMAL, 1000)
			release()
		}
	}.getOrDefault(Unit)

	fun vibrate(duration: Long) {
		vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
	}
}
