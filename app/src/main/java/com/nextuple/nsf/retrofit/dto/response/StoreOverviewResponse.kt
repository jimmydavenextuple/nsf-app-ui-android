package com.nextuple.nsf.retrofit.dto.response

import com.nextuple.nsf.retrofit.dto.PackTask
import com.nextuple.nsf.retrofit.dto.PickTask
import com.nextuple.nsf.retrofit.dto.StageTask

data class StoreOverviewResponse(
	val pickOverview: PickOverview,
	val userOverview: UserOverview,
	val prepOverview: PrepOverview
) {
	data class PickOverview(
		val tasksWorked: Int,
		val tasksInProgress: Int,
		val tasksUnassigned: Int,

		val unitsWorked: Int,
		val unitsInProgress: Int,
		val unitsUnassigned: Int
	)

	data class UserOverview(
		val currentPickTask: PickTask? = null,
		val currentPackTask: PackTask? = null,
		val currentStageTask: StageTask? = null
	)

	data class PrepOverview(
		val tasksInProgress: Int,
		val tasksUnassigned: Int,
		val prepTasks: List<PrepTask>
	)

	data class PrepTask(
		val id: Long,
		val taskType: String,
		val orderNumber: String,
		val athleteFirstName: String? = null,
		val athleteLastName: String? = null,
		val items: List<PrepTaskItem>
	)

	data class PrepTaskItem(
		val id: Long,
		val sku: String,
		val productName: String,
		val productImageUrls: List<String>
	)
}

fun StoreOverviewResponse.PrepTask.prepTaskAthleteShortName() = "$athleteLastName, ${athleteFirstName?.firstOrNull() ?: ""}."
