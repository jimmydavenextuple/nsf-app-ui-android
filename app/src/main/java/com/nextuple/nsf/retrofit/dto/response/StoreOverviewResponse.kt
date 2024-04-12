package com.nextuple.nsf.retrofit.dto.response

import com.nextuple.nsf.retrofit.dto.PickTask

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
		val currentPickTask: PickTask? = null
	)

	data class PrepOverview(
		val tasksInProgress: Int,
		val tasksUnassigned: Int
	)


}
