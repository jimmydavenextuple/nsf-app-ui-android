package com.nextuple.nsf.retrofit.dto.response

import com.nextuple.nsf.retrofit.dto.StageTaskContainer
import com.nextuple.nsf.retrofit.dto.Status

data class OrderDetailsResponse(
	/*
		TODO: Revisit mapping once FIT 2.0 is fully ready
	 */
	val orderNumber: String? = null,
	val orderStatusText: String? = null,

	val orderDate: String? = null,
	val expectedDeliveryDate: String? = null,
	val receivedDate: String? = null,

	val pickedByUserId: String? = null,
	val packedOnDate: String? = null,
	val packedByUserId: String? = null,
	val stagedByUserId: String? = null,
	val dispensedByUserId: String? = null,

	val pickupByDate: String? = null,
	// If count <=0 show EXTEND_PICKUP
	val pickupExtendedCount: Int? = null,
	val pickedUpDate: String? = null,

	val athleteDetail: AthleteDetail? = null,
	val athleteCheckInDetail: AthleteCheckInDetail? = null,
	val driverDetail: DriverDetail? = null,

	val fulfillmentRequestDetail: FulfillmentRequestDetail
)

data class AthleteDetail(
	val athleteFirstName: String? = null,
	val athleteLastName: String? = null,
	val athleteProxyFirstName: String? = null,
	val athleteProxyLastName: String? = null,
	val athletePhoneNumber: String? = null
)

data class DriverDetail(
	val driverFirstName: String? = null,
	val driverLastName: String? = null,
	val batchId: String? = null
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

	/**
	 * TODO: Extract this or containers since GetOrders API fills this field, but GetOrderDetails API does not and uses containers.
	 */
	val holdingLocation: String? = null
)

fun AthleteDetail.athleteFullName() =
	"${athleteFirstName.orEmpty()} ${athleteLastName.orEmpty()}".trim()

fun AthleteDetail.athleteShortName() =
	"${athleteLastName.orEmpty()}, ${athleteFirstName?.firstOrNull() ?: ""}."

fun AthleteDetail.sddShortName() =
	"${athleteFirstName.orEmpty()} ${athleteLastName?.firstOrNull() ?: ""}."

fun DriverDetail.sddShortName() =
	"${driverFirstName.orEmpty()} ${driverLastName?.firstOrNull() ?: ""}."

fun AthleteDetail.athleteProxyFullName() =
	"${athleteProxyFirstName.orEmpty()} ${athleteProxyLastName.orEmpty()}".trim()
