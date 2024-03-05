package com.nextuple.nsf.ui.state

import androidx.lifecycle.SavedStateHandle
import com.nextuple.nsf.CoroutineRule
import com.nextuple.nsf.retrofit.dto.FITTask
import com.nextuple.nsf.retrofit.dto.PackTaskItem
import com.nextuple.nsf.retrofit.dto.PrepDetail
import com.nextuple.nsf.retrofit.dto.StageTask
import com.nextuple.nsf.retrofit.dto.StageTaskContainer
import com.nextuple.nsf.retrofit.dto.Status
import com.nextuple.nsf.retrofit.dto.response.StoreOverviewResponse
import com.nextuple.nsf.retrofit.dto.response.StoreOverviewResponse.PickOverview
import com.nextuple.nsf.retrofit.dto.response.StoreOverviewResponse.PrepOverview
import com.nextuple.nsf.retrofit.dto.response.StoreOverviewResponse.UserOverview
import com.nextuple.nsf.service.InfoService
import com.nextuple.nsf.service.PackTaskService
import com.nextuple.nsf.service.StageTaskService
import com.nextuple.nsf.service.dto.Result
import com.nextuple.nsf.ui.screen.prep.PackType.ORDER
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.ui.util.GenericViewState.Failure
import com.nextuple.nsf.ui.util.GenericViewState.Idle
import com.nextuple.nsf.ui.util.GenericViewState.Success
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PrepOrderViewModelTest {
	@get:Rule
	val coroutineRule = CoroutineRule()

	@InjectMockKs
	private lateinit var vm: PrepOrderViewModel

	@Suppress("unused")
	@MockK
	private lateinit var savedStateHandle: SavedStateHandle

	@MockK
	private lateinit var packTaskService: PackTaskService

	@MockK
	private lateinit var stageTaskService: StageTaskService

	@MockK
	private lateinit var infoService: InfoService

	@Before
	fun setUp() {
		MockKAnnotations.init(this)

		every { runBlocking { infoService.getStoreOverview() } } returns Result.Success(
			StoreOverviewResponse(
				pickOverview = PickOverview(
					tasksWorked = 0,
					tasksInProgress = 0,
					tasksUnassigned = 0,
					unitsWorked = 0,
					unitsInProgress = 0,
					unitsUnassigned = 0
				),
				prepOverview = PrepOverview(
					tasksInProgress = 0,
					tasksUnassigned = 0
				),
				userOverview = UserOverview()
			)
		)
	}

	@Test
	fun `prepOrderUiData is set correctly on setUiData success when packType is ORDER`() = runTest {
		val orderNumber = "xx90000000"
		val frNo = "$orderNumber.001"
		val subFulfillmentType = "BOPIS"
		val firstName = "first"
		val lastName = "last"
		val pickedBy = "teammate"

		val prepDetail = PrepDetail(
			fulfillmentRequestNumber = frNo,
			fulfillmentType = "BOPIS",
			subFulfillmentType = subFulfillmentType,
			orderNumber = orderNumber,
			athleteFirstName = firstName,
			athleteLastName = lastName,
			pickedBy = pickedBy,
			items = emptyList(),
			packTask = FITTask(status = Status(code = "1005")) // OpTask ASSIGNED
		)
		every {
			runBlocking { packTaskService.getPrepDetails(frNo) }
		} returns Result.Success(prepDetail)

		vm.setUiData(packType = ORDER, data = frNo)
		advanceUntilIdle()

		verify {
			runBlocking { packTaskService.getPrepDetails(frNo) }
		}

		val expectedPrepOrder = PrepOrder(
			subFulfillmentType = subFulfillmentType,
			athleteName = "$firstName $lastName",
			orderNumber = orderNumber,
			pickedBy = pickedBy
		)

		assertEquals(vm.prepOrderUiData.state, GenericViewState.Success)
		assertEquals(vm.prepOrderUiData.prepOrder, expectedPrepOrder)
	}

	@Test
	fun `prepOrderUiData is set correctly on setUiData failure when packType is ORDER`() = runTest {
		val orderNumber = "xx90000000"
		val frNo = "$orderNumber.001"

		every {
			runBlocking { packTaskService.getPrepDetails(frNo) }
		} returns Result.generalError()

		vm.setUiData(packType = ORDER, data = frNo)
		advanceUntilIdle()

		verify {
			runBlocking { packTaskService.getPrepDetails(frNo) }
		}

		assertEquals(vm.prepOrderUiData.state, GenericViewState.Failure)
		assertNull(vm.prepOrderUiData.prepOrder)
	}

	@Test
	fun `packItem should update the pack item isScanned to TRUE when the scanned upc is found`() =
		runTest {
			val scannedBarcode = "101"
			val item = PackTaskItem(
				id = 1,
				sku = "2345",
				qty = 1,
				packedQty = 0,
				declinedQty = 0,
				productName = "Product1",
				productImageUrls = emptyList(),
				scannedBarcode = scannedBarcode
			)
			val prepDetail = emptyPrepDetail.copy(items = listOf(item))

			every {
				runBlocking { packTaskService.getPrepDetails("frNo") }
			} returns Result.Success(prepDetail)

			vm.setUiData(packType = ORDER, data = "frNo")
			advanceUntilIdle()

			val initialPackItem =
				vm.prepOrderUiData.prepOrder?.packItems?.first { it.scannedBarcode == scannedBarcode }
			assertEquals(false, initialPackItem?.isScanned)

			vm.packItem(scannedBarcode)

			val updatedPackItem =
				vm.prepOrderUiData.prepOrder?.packItems?.first { it.scannedBarcode == scannedBarcode }
			assertEquals(true, updatedPackItem?.isScanned)
		}

	@Test
	fun `packItem should NOT update the pack item isScanned to TRUE when an incorrect barcode is scanned`() =
		runTest {
			val correctScannedBarcode = "101"
			val incorrectScannedBarcode = "000"
			val item = PackTaskItem(
				id = 1,
				sku = "2345",
				qty = 1,
				packedQty = 0,
				declinedQty = 0,
				productName = "Product1",
				productImageUrls = emptyList(),
				scannedBarcode = correctScannedBarcode
			)
			val prepDetail = emptyPrepDetail.copy(items = listOf(item))

			every {
				runBlocking { packTaskService.getPrepDetails("frNo") }
			} returns Result.Success(prepDetail)

			vm.setUiData(packType = ORDER, data = "frNo")
			advanceUntilIdle()

			val initialPackItem =
				vm.prepOrderUiData.prepOrder?.packItems?.first { it.scannedBarcode == correctScannedBarcode }
			assertEquals(false, initialPackItem?.isScanned)

			vm.packItem(incorrectScannedBarcode)

			val updatedPackItem =
				vm.prepOrderUiData.prepOrder?.packItems?.first { it.scannedBarcode == correctScannedBarcode }
			assertEquals(false, updatedPackItem?.isScanned)
		}

	@Test
	fun `packItem should only update one pack item isScanned to true if there are multiple items with the same upc`() =
		runTest {
			val scannedBarcode = "101"
			val item1 = PackTaskItem(
				id = 1,
				sku = "2345",
				qty = 1,
				packedQty = 0,
				declinedQty = 0,
				productName = "Product1",
				productImageUrls = emptyList(),
				scannedBarcode = scannedBarcode
			)

			val item2 = PackTaskItem(
				id = 2,
				sku = "2345",
				qty = 1,
				packedQty = 0,
				declinedQty = 0,
				productName = "Product1",
				productImageUrls = emptyList(),
				scannedBarcode = scannedBarcode
			)
			val prepDetail = emptyPrepDetail.copy(items = listOf(item1, item2))

			every {
				runBlocking { packTaskService.getPrepDetails("frNo") }
			} returns Result.Success(prepDetail)

			vm.setUiData(packType = ORDER, data = "frNo")
			advanceUntilIdle()

			val initialTotalScanned =
				vm.prepOrderUiData.prepOrder?.packItems?.filter { it.isScanned }?.size
			assertEquals(0, initialTotalScanned)

			vm.packItem(scannedBarcode)

			val updatedTotalScanned =
				vm.prepOrderUiData.prepOrder?.packItems?.filter { it.isScanned }?.size
			assertEquals(1, updatedTotalScanned)
		}

	@Test
	fun `recordHoldingLocation, on task not found error  should enter error state`() = runTest {
		val errMsg = "error message"
		every {
			runBlocking { stageTaskService.recordHoldingLocation(any()) }
		} returns Result.Error(msg = errMsg, type = Result.ErrorType.NOT_FOUND)
		vm.recordHoldingLocation(0, "location")
		advanceUntilIdle()

		Assert.assertEquals(Failure, vm.holdLocationState)
		Assert.assertNull(vm.stageTask)
		Assert.assertEquals(errMsg, vm.errMsg)
	}

	@Test
	fun `recordHoldingLocation, on success, should populate stageTask and enter success state`() =
		runTest {
			val stageTask = StageTask(
				id = 12,
				status = Status(code = "12", name = "COMPLETED"),
				fulfillmentRequestNumber = "123",
				containers = listOf(
					StageTaskContainer(id = 12, holdingLocation = "Hyderabad")
				)
			)
			every {
				runBlocking {
					stageTaskService.recordHoldingLocation(any())
				}
			} returns Result.Success(stageTask)
			vm.recordHoldingLocation(0, "location")
			advanceUntilIdle()

			Assert.assertEquals(Success, vm.holdLocationState)
			Assert.assertEquals(stageTask, vm.stageTask)
			Assert.assertNull(vm.errMsg)
		}

	@Test
	fun `resetHoldSlipState should reset holdSlipState`() = runTest {
		vm.resetHoldSlipState()

		Assert.assertEquals(Idle, vm.holdSlipState)
	}

	@Test
	fun `resetHoldLocationState should reset holdLocationState`() = runTest {
		vm.resetHoldLocationState()

		Assert.assertEquals(Idle, vm.holdLocationState)
	}

	private val emptyPrepDetail = PrepDetail(
		fulfillmentRequestNumber = "",
		fulfillmentType = "",
		subFulfillmentType = "",
		orderNumber = "",
		athleteFirstName = "",
		athleteLastName = "",
		pickedBy = "",
		items = emptyList(),
		packTask = FITTask(status = Status(code = "1005")) // OpTask Assigned
	)
}
