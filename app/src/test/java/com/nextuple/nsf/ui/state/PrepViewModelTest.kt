package com.nextuple.nsf.ui.state

import androidx.lifecycle.SavedStateHandle
import com.nextuple.nsf.CoroutineRule
import com.nextuple.nsf.retrofit.dto.FITTask
import com.nextuple.nsf.retrofit.dto.PackTask
import com.nextuple.nsf.retrofit.dto.PackTaskItem
import com.nextuple.nsf.retrofit.dto.PackedItem
import com.nextuple.nsf.retrofit.dto.PrepDetail
import com.nextuple.nsf.retrofit.dto.StageTask
import com.nextuple.nsf.retrofit.dto.StageTaskContainer
import com.nextuple.nsf.retrofit.dto.Status
import com.nextuple.nsf.retrofit.dto.response.GetUserPrepTasksResponse
import com.nextuple.nsf.service.InfoService
import com.nextuple.nsf.service.PackTaskService
import com.nextuple.nsf.service.StageTaskService
import com.nextuple.nsf.service.dto.Result
import com.nextuple.nsf.ui.screen.prep.PackType.ORDER
import com.nextuple.nsf.ui.state.PrepViewModel.PrepOrder
import com.nextuple.nsf.ui.state.PrepViewModel.Step.Assemble
import com.nextuple.nsf.ui.state.PrepViewModel.Step.Landing
import com.nextuple.nsf.ui.state.PrepViewModel.Step.Pack
import com.nextuple.nsf.ui.state.PrepViewModel.Step.Stage
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.util.FulfillmentType
import com.nextuple.nsf.util.SubFulfillmentType
import io.mockk.Called
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PrepViewModelTest {
	companion object {
		private const val FR_NO = "10100100100.001"

		private val PACK_TASK = PackTask(
			id = 1,
			status = Status(code = "1005"),
			fulfillmentRequestNumber = FR_NO
		)
		private val STAGE_TASK = StageTask(
			id = 1,
			status = Status(code = "1005"),
			fulfillmentRequestNumber = FR_NO,
			holdSlipZPL = mutableListOf("page1", "page2", "page3"),
			containers = listOf(
				StageTaskContainer(
					id = 11111,
					packedItems = listOf(
						PackedItem(
							sku = "",
							productName = "",
							productImageUrls = listOf("url1", "url2", "url3"),
							qty = 1
						)
					)
				)
			)
		)

		private val EXT_PACK_TASK = FITTask(
			taskId = 1,
			status = Status(code = "1005")
		)

		private val EXT_STAGE_TASK = FITTask(
			status = Status(code = "1005")
		)
	}

	@get:Rule
	val coroutineRule = CoroutineRule()

	@InjectMockKs
	private lateinit var vm: PrepViewModel

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

		every { runBlocking { infoService.getUserPrepTasks() } } returns Result.Success(
			GetUserPrepTasksResponse(
				packTask = null,
				stageTask = null
			)
		)
	}

	@Test
	fun `fetchPrepStage should enter error state when failing to fetch user tasks`() = runTest {
		every { runBlocking { infoService.getUserPrepTasks() } } returns Result.Error()

		vm.fetchCurrentStep()
		advanceUntilIdle()

		assertNull(vm.stageTask)
		assertNull(vm.packTask)
		assertEquals(Landing, vm.currentStep)

		assertEquals(GenericViewState.Failure, vm.viewState)
		verify { packTaskService wasNot Called }
		verify { stageTaskService wasNot Called }
	}

	@Test
	fun `fetchPrepStage should enter landing step when there are no user tasks`() = runTest {
		every { runBlocking { infoService.getUserPrepTasks() } } returns Result.Success(
			GetUserPrepTasksResponse(
				packTask = null,
				stageTask = null
			)
		)

		vm.fetchCurrentStep()
		advanceUntilIdle()

		assertNull(vm.stageTask)
		assertNull(vm.packTask)
		assertEquals(Landing, vm.currentStep)

		assertEquals(GenericViewState.Success, vm.viewState)
		verify { packTaskService wasNot Called }
		verify { stageTaskService wasNot Called }
	}

	@Test
	fun `fetchPrepStage should enter landing step when there's a completed stage task`() = runTest {
		val completedStageTask = STAGE_TASK.copy(status = Status(name = "COMPLETED"))

		every { runBlocking { infoService.getUserPrepTasks() } } returns Result.Success(
			GetUserPrepTasksResponse(
				packTask = null,
				stageTask = completedStageTask
			)
		)

		vm.fetchCurrentStep()
		advanceUntilIdle()

		assertEquals(completedStageTask, vm.stageTask)
		assertNull(vm.packTask)
		assertEquals(Landing, vm.currentStep)

		assertEquals(GenericViewState.Success, vm.viewState)
		verify { packTaskService wasNot Called }
		verify { stageTaskService wasNot Called }
	}

	@Test
	fun `fetchPrepStage should enter stage step when there is a stage task`() = runTest {
		every { runBlocking { infoService.getUserPrepTasks() } } returns Result.Success(
			GetUserPrepTasksResponse(
				packTask = null,
				stageTask = STAGE_TASK
			)
		)
		every { runBlocking { packTaskService.getPrepDetails(frNo = FR_NO) } } returns Result.Success(
			PrepDetail(
				fulfillmentType = FulfillmentType.BOPIS.name,
				subFulfillmentType = SubFulfillmentType.BOPIS.name,
				fulfillmentRequestNumber = FR_NO,
				items = emptyList(),
				stageTask = EXT_STAGE_TASK
			)
		)

		vm.fetchCurrentStep()
		advanceUntilIdle()

		assertEquals(STAGE_TASK, vm.stageTask)
		assertNull(vm.packTask)
		assertEquals(Stage, vm.currentStep)

		assertEquals(GenericViewState.Success, vm.viewState)
		verify { runBlocking { packTaskService.getPrepDetails(frNo = FR_NO) } }
		verify { stageTaskService wasNot Called }
	}

	@Test
	fun `fetchPrepStage should enter pack step when there is an in-progress pack task`() = runTest {
		every { runBlocking { infoService.getUserPrepTasks() } } returns Result.Success(
			GetUserPrepTasksResponse(
				packTask = PACK_TASK,
				stageTask = null
			)
		)
		every { runBlocking { packTaskService.packByOrder(frNo = FR_NO) } } returns Result.Success(
			PrepDetail(
				fulfillmentType = FulfillmentType.BOPIS.name,
				subFulfillmentType = SubFulfillmentType.BOPIS.name,
				fulfillmentRequestNumber = FR_NO,
				items = emptyList(),
				packTask = EXT_PACK_TASK
			)
		)

		vm.fetchCurrentStep()
		advanceUntilIdle()

		assertNull(vm.stageTask)
		assertEquals(PACK_TASK, vm.packTask)
		assertEquals(Pack, vm.currentStep)

		assertEquals(GenericViewState.Success, vm.viewState)
		verify { runBlocking { packTaskService.packByOrder(frNo = FR_NO) } }
		verify { stageTaskService wasNot Called }
	}

	@Test
	fun `fetchPrepStage should enter assemble step when there is a created pack task`() = runTest {
		every { runBlocking { infoService.getUserPrepTasks() } } returns Result.Success(
			GetUserPrepTasksResponse(
				packTask = PACK_TASK.copy(status = null),
				stageTask = null
			)
		)
		every { runBlocking { packTaskService.packByOrder(frNo = FR_NO) } } returns Result.Success(
			PrepDetail(
				fulfillmentType = FulfillmentType.BOPIS.name,
				subFulfillmentType = SubFulfillmentType.BOPIS.name,
				fulfillmentRequestNumber = FR_NO,
				items = emptyList(),
				packTask = EXT_PACK_TASK.copy(status = Status())
			)
		)

		vm.fetchCurrentStep()
		advanceUntilIdle()

		assertNull(vm.stageTask)
		assertEquals(PACK_TASK.copy(status = Status()), vm.packTask)
		assertEquals(Assemble, vm.currentStep)

		assertEquals(GenericViewState.Success, vm.viewState)
		verify { runBlocking { packTaskService.packByOrder(frNo = FR_NO) } }
		verify { stageTaskService wasNot Called }
	}

	@Test
	fun `fetchPrepOrder is set correctly on success when packType is ORDER`() =
		runTest {
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
				runBlocking { packTaskService.packByOrder(frNo) }
			} returns Result.Success(prepDetail)

			vm.fetchPrepOrder(packType = ORDER, upcOrFrNo = frNo)
			advanceUntilIdle()

			verify {
				runBlocking { packTaskService.packByOrder(frNo) }
			}

			val expectedPrepOrder = PrepOrder(
				subFulfillmentType = subFulfillmentType,
				athleteName = "$firstName $lastName",
				orderNumber = orderNumber,
				pickedBy = pickedBy
			)

			assertEquals(GenericViewState.Success, vm.viewState)
			assertEquals(expectedPrepOrder, vm.prepOrder)
		}

	@Test
	fun `fetchPrepOrder is set correctly on failure when packType is ORDER`() =
		runTest {
			val orderNumber = "xx90000000"
			val frNo = "$orderNumber.001"

			every {
				runBlocking { packTaskService.packByOrder(frNo) }
			} returns Result.generalError()

			vm.fetchPrepOrder(packType = ORDER, upcOrFrNo = frNo)
			advanceUntilIdle()

			verify {
				runBlocking { packTaskService.packByOrder(frNo) }
			}

			assertEquals(GenericViewState.Failure, vm.viewState)
			assertNull(vm.prepOrder)
		}

	@Test
	fun `packItem should update the pack item isScanned to TRUE when the scanned upc is found`() =
		runTest {
			val scannedBarcode = "101"
			val item = PackTaskItem(
				sku = "2345",
				qty = 1,
				productName = "Product1",
				productImageUrls = emptyList(),
				scannedBarcode = scannedBarcode
			)
			val prepDetail = emptyPrepDetail.copy(items = listOf(item))

			every {
				runBlocking { packTaskService.packByOrder(frNo = FR_NO) }
			} returns Result.Success(prepDetail)

			vm.fetchPrepOrder(packType = ORDER, upcOrFrNo = FR_NO)
			advanceUntilIdle()

			val initialPackItem = vm.prepOrder?.packItems?.first {
				it.scannedBarcode == scannedBarcode
			}
			assertEquals(false, initialPackItem?.isScanned)

			vm.packItem(scannedBarcode)

			val updatedPackItem = vm.prepOrder?.packItems?.first {
				it.scannedBarcode == scannedBarcode
			}
			assertEquals(true, updatedPackItem?.isScanned)
		}

	@Test
	fun `packItem should NOT update the pack item isScanned to TRUE when an incorrect barcode is scanned`() =
		runTest {
			val correctScannedBarcode = "101"
			val incorrectScannedBarcode = "000"
			val item = PackTaskItem(
				sku = "2345",
				qty = 1,
				productName = "Product1",
				productImageUrls = emptyList(),
				scannedBarcode = correctScannedBarcode
			)
			val prepDetail = emptyPrepDetail.copy(items = listOf(item))

			every {
				runBlocking { packTaskService.packByOrder(frNo = FR_NO) }
			} returns Result.Success(prepDetail)

			vm.fetchPrepOrder(packType = ORDER, upcOrFrNo = FR_NO)
			advanceUntilIdle()

			val initialPackItem = vm.prepOrder?.packItems?.first {
				it.scannedBarcode == correctScannedBarcode
			}
			assertEquals(false, initialPackItem?.isScanned)

			vm.packItem(incorrectScannedBarcode)

			val updatedPackItem = vm.prepOrder?.packItems?.first {
				it.scannedBarcode == correctScannedBarcode
			}
			assertEquals(false, updatedPackItem?.isScanned)
		}

	@Test
	fun `packItem should only update one pack item isScanned to true if there are multiple items with the same upc`() =
		runTest {
			val scannedBarcode = "101"
			val item1 = PackTaskItem(
				sku = "2345",
				qty = 1,
				productName = "Product1",
				productImageUrls = emptyList(),
				scannedBarcode = scannedBarcode
			)

			val item2 = PackTaskItem(
				sku = "2345",
				qty = 1,
				productName = "Product1",
				productImageUrls = emptyList(),
				scannedBarcode = scannedBarcode
			)
			val prepDetail = emptyPrepDetail.copy(items = listOf(item1, item2))

			every {
				runBlocking { packTaskService.packByOrder(frNo = FR_NO) }
			} returns Result.Success(prepDetail)

			vm.fetchPrepOrder(packType = ORDER, upcOrFrNo = FR_NO)
			advanceUntilIdle()

			val initialTotalScanned = vm.prepOrder?.packItems?.filter { it.isScanned }?.size
			assertEquals(0, initialTotalScanned)

			vm.packItem(scannedBarcode)

			val updatedTotalScanned = vm.prepOrder?.packItems?.filter { it.isScanned }?.size
			assertEquals(1, updatedTotalScanned)
		}

	@Test
	fun `recordHoldingLocation, on task not found error, should enter error state`() = runTest {
		every {
			runBlocking { stageTaskService.recordHoldingLocation(any()) }
		} returns Result.Error(msg = "anyError", type = Result.ErrorType.NOT_FOUND)

		vm.recordHoldingLocation(0, "location")
		advanceUntilIdle()

		assertEquals(GenericViewState.Failure, vm.holdLocationState)
		assertNull(vm.stageTask)
	}

	@Test
	fun `recordHoldingLocation, on success, should enter success state`() =
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

			assertEquals(GenericViewState.Success, vm.holdLocationState)
		}

	@Test
	fun `resetHoldSlipState should reset holdSlipState`() = runTest {
		vm.resetGetHoldSlipState()

		assertEquals(GenericViewState.Idle, vm.getHoldSlipState)
	}

	@Test
	fun `resetHoldLocationState should reset holdLocationState`() = runTest {
		vm.resetHoldLocationState()

		assertEquals(GenericViewState.Idle, vm.holdLocationState)
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
