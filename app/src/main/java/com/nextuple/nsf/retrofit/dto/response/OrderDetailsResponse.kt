package com.nextuple.nsf.retrofit.dto.response

import com.nextuple.nsf.retrofit.dto.StageTaskContainer
import com.nextuple.nsf.retrofit.dto.Status

data class OrderDetailsResponse(
	/*
		TODO: Revisit mapping once FIT 2.0 is fully ready
	 */
	val orderNumber: String? = null,
	val orderStatusText: String? = null,

	val packedOnDate: String? = null,
	val packedByUserId: String? = null,

	val pickupByDate: String? = null,
	// If count <=0 show EXTEND_PICKUP
	val pickupExtendedCount: Int? = null,
	val pickedUpDate: String? = null,

	val athleteDetail: AthleteDetail? = null,
	val athleteCheckInDetail: AthleteCheckInDetail? = null,

	val fulfillmentRequestDetail: FulfillmentRequestDetail
)

data class AthleteDetail(
	val athleteFirstName: String? = null,
	val athleteLastName: String? = null,
	val athleteProxyFirstName: String? = null,
	val athleteProxyLastName: String? = null,
	val athletePhoneNumber: String? = null
)

data class AthleteCheckInDetail(
	// If pickupTaskId >0 we need to show REMOVE_CHECK_IN
	val pickupTaskId: Long? = null,
	val checkInType: String? = null,
	val checkInBy: String? = null,
	val athleteVehicle: String? = null,
	val athleteLocation: String? = null,
	val handOffPlacement: String? = null,
	val checkInTime: String? = null
)

data class FulfillmentRequestDetail(
	val fulfillmentRequestNumber: String,
	val fulfillmentRequestId: Long? = null,
	var fulfillmentRequestStatus: Status? = null,
	val brand: String? = null,
	val store: String? = null,
	val fulfillmentType: String? = null,
	val subFulfillmentType: String? = null,

	var totalContainerCount: Int? = null,
	val containers: List<StageTaskContainer> = emptyList(),

	val holdingLocation: String? = null
)

fun AthleteDetail.athleteFullName() = "$athleteFirstName $athleteLastName"

fun AthleteDetail.athleteShortName() = "$athleteLastName, ${athleteFirstName?.firstOrNull() ?: ""}."

fun AthleteDetail.athleteProxyFullName() = "$athleteProxyFirstName $athleteProxyLastName"
