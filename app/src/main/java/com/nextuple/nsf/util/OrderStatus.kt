package com.nextuple.nsf.util

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.theme.BrandColor

enum class OrderStatus(
	/**
	 * This value should match the back-end's FRStatus enum.
	 */
	val statusText: String,
	val statusColor: Color,
	@DrawableRes val iconDrawable: Int? = null
) {
	READY(
		statusText = "Ready",
		statusColor = BrandColor.BLUE_300_NT,
		iconDrawable = R.drawable.ic_check
	),
	COMPLETED(
		statusText = "Completed",
		statusColor = BrandColor.BLUE_300_NT,
		iconDrawable = R.drawable.ic_check
	),
	EXTENDED(
		statusText = "Extended",
		statusColor = BrandColor.BLUE_300_NT,
		iconDrawable = R.drawable.ic_extended
	),

	CANCELED(
		statusText = "Canceled",
		statusColor = BrandColor.GRAY_650,
		iconDrawable = R.drawable.ic_canceled
	),
	DECLINED(
		statusText = "Declined",
		statusColor = BrandColor.GRAY_650,
		iconDrawable = R.drawable.ic_declined_cart
	),
	AGED(statusText = "Aged", statusColor = BrandColor.GRAY_650, iconDrawable = R.drawable.ic_aged),

	INCOMING(
		statusText = "Incoming",
		statusColor = BrandColor.BLUE_300_NT,
		iconDrawable = R.drawable.ic_incoming
	),
	LATE(statusText = "Late", statusColor = BrandColor.BLUE_700, iconDrawable = R.drawable.ic_late),

	PACK(statusText = "Pack", statusColor = BrandColor.PINK_NT, iconDrawable = R.drawable.ic_pack),
	BEING_PACKED(
		statusText = "Being Packed",
		statusColor = BrandColor.BLUE_300_NT,
		iconDrawable = R.drawable.ic_pack
	),
	STAGE(
		statusText = "Stage",
		statusColor = BrandColor.PINK_NT,
		iconDrawable = R.drawable.ic_stage
	),
	BEING_STAGED(
		statusText = "Being Staged",
		statusColor = BrandColor.BLUE_300_NT,
		iconDrawable = R.drawable.ic_stage
	),

	IN_PROGRESS(statusText = "In Progress", statusColor = BrandColor.PINK_NT),
	CHECKED_IN(statusText = "Checked In", statusColor = BrandColor.PINK_NT),

	DEFAULT(statusText = "", statusColor = BrandColor.PINK_NT);

	companion object {
		private val COMPLETE_STATUS = listOf(
			COMPLETED,
			DECLINED,
			CANCELED
		)

		private val READY_STATUS = listOf(
			READY,
			EXTENDED,
			AGED,
			CHECKED_IN,
			IN_PROGRESS
		)

		private val READY_STATUS_TEXTS = READY_STATUS.plus(COMPLETE_STATUS).map { it.statusText }

		fun getByStatus(status: String) = entries.firstOrNull { it.statusText == status } ?: DEFAULT

		fun isReadyStatusText(displayName: String?): Boolean {
			displayName ?: return false

			return READY_STATUS_TEXTS.any {
				it.equals(displayName, ignoreCase = true)
			}
		}

		fun isCheckedInStatus(status: String?): Boolean {
			status ?: return false
			return CHECKED_IN.statusText.equals(status, ignoreCase = true)
		}

		fun isReadyStatus(status: OrderStatus): Boolean = status in READY_STATUS

		fun isReadyStatus(status: String): Boolean = READY_STATUS.any {
			it.statusText.equals(status, ignoreCase = true)
		}

		fun isCompleteStatus(status: OrderStatus): Boolean = status in COMPLETE_STATUS

		fun isCompleteStatus(status: String): Boolean = COMPLETE_STATUS.any {
			it.statusText.equals(status, ignoreCase = true)
		}
	}
}
