package com.nextuple.nsf.retrofit.dto.response

data class StoreOverviewResponse(
	val pickOverview: PickOverview,
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

	data class PrepOverview(
		val tasksInProgress: Int,
		val tasksUnassigned: Int
	)
}
