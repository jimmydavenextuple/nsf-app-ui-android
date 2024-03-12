package com.nextuple.nsf.retrofit.dto

data class PrepDetail(
    val fulfillmentType: String,
    val subFulfillmentType: String,
    val fulfillmentRequestNumber: String,
    val orderNumber: String? = null,
    val athleteFirstName: String? = null,
    val athleteLastName: String? = null,

    var pickedBy: String? = null,
    val items: List<PackTaskItem>,

    val assembleTask: FITTask? = null,
    val packTask: FITTask? = null,
    val stageTask: FITTask? = null
) {
	fun isInProgress() = packTask?.isInProgress() == true || stageTask?.isInProgress() == true
}

data class FITTask(
	val taskId: Long? = null,
	val status: Status
) {
	fun isInProgress() = status.code in setOf("1005", "1010")
}
