package com.nextuple.nsf.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import com.nextuple.nsf.ui.util.OnDataScanned

class DataWedgeBroadcastReceiver : BroadcastReceiver() {

	companion object {
		const val INTENT_ACTION = "eomomniops.RECVR"
		const val INTENT_CATEGORY = Intent.CATEGORY_DEFAULT

		const val INTENT_SRC_KEY = "com.motorolasolutions.emdk.datawedge.source"
		const val INTENT_SRC = "scanner"

		const val INTENT_DATA_KEY = "com.motorolasolutions.emdk.datawedge.data_string"
		const val INTENT_LABEL_KEY = "com.motorolasolutions.emdk.datawedge.label_type"
	}

	private var onDataScanned: OnDataScanned = { _, _ -> }

	fun setOnDataScanned(onDataScanned: OnDataScanned) {
		this.onDataScanned = onDataScanned
	}

	/**
	 * Registers this broadcast receiver against the provided context wrapper.
	 */
	fun registerSelf(ctxWrapper: ContextWrapper?) {
		ctxWrapper ?: return
		ctxWrapper.registerReceiver(
			this,
			IntentFilter().apply {
				addAction(INTENT_ACTION)
				addCategory(INTENT_CATEGORY)
			}
		)
	}

	/**
	 * Un-registers this broadcast receiver against the provided context wrapper.
	 */
	fun unregisterSelf(ctxWrapper: ContextWrapper?) {
		ctxWrapper ?: return
		ctxWrapper.unregisterReceiver(this)
	}

	override fun onReceive(ctx: Context?, intent: Intent?) {
		ctx ?: return

		intent?.takeIf { it.action == INTENT_ACTION } ?: return

		val src = intent.getStringExtra(INTENT_SRC_KEY) ?: INTENT_SRC
		if (!src.equals(INTENT_SRC, ignoreCase = true)) {
			return
		}

		val data = intent.getStringExtra(INTENT_DATA_KEY)?.trim { it <= ' ' }.orEmpty()
		if (data.isEmpty()) {
			return
		}

		// Skip the 'LABEL-TYPE-' (11 chars) prefix.
		val label = intent.getStringExtra(INTENT_LABEL_KEY)
		val symbology = runCatching {
			label?.substring(11)?.lowercase()
		}.getOrNull()

		onDataScanned(data, symbology)
	}
}
