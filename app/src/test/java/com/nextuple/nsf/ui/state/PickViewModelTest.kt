package com.nextuple.nsf.ui.state

import androidx.lifecycle.SavedStateHandle
import com.nextuple.nsf.CoroutineRule
import com.nextuple.nsf.retrofit.dto.DeclineItemRequest
import com.nextuple.nsf.retrofit.dto.PickItemRequest
import com.nextuple.nsf.retrofit.dto.PickTask
import com.nextuple.nsf.retrofit.dto.PickTaskItem
import com.nextuple.nsf.retrofit.dto.response.GetUserPickTasksResponse
import com.nextuple.nsf.service.InfoService
import com.nextuple.nsf.service.LogService
import com.nextuple.nsf.service.PickService
import com.nextuple.nsf.service.dto.Result
import com.nextuple.nsf.service.dto.Result.ErrorType.NOT_FOUND
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.util.FulfillmentType.BOPIS
import com.nextuple.nsf.util.SubFulfillmentType
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PickViewModelTest {
	@get:Rule
	val coroutineRule = CoroutineRule()

	@InjectMockKs
	private lateinit var vm: PickViewModel

	@Suppress("unused")
	@MockK
	private lateinit var savedStateHandle: SavedStateHandle

	@MockK
	private lateinit var pickService: PickService

	@MockK
	private lateinit var infoService: InfoService

	@MockK
	private lateinit var logService: LogService

	@Before
	fun setUp() {
		MockKAnnotations.init(this)
	}

	@Test
	fun `fetchCurrentStep should set the user's current pick task`() = runTest {
		val expectedTask = PickTask(
			id = 123L,
			fulfillmentType = BOPIS,
			subFulfillmentType = SubFulfillmentType.BOPIS,
			items = listOf(
				PickTaskItem(
					sku = "anySku",
					upcs = emptyList(),
					onHandQty = 9001,
					qty = 2,
					pickedQty = 1,
					declinedQty = 0,
					productBrand = "anyBrand",
					productName = "anyName",
					style = null
				)
			),
			totalWorkedQty = 1,
			totalQty = 2,
			totalRemainingQty = 1
		)

		every { runBlocking { infoService.getUserPickTasks() } } returns Result.Success(
			GetUserPickTasksResponse(
				pickTask = expectedTask
			)
		)

		vm.fetchCurrentStep()
		advanceUntilIdle()

		assertEquals(GenericViewState.Success, vm.viewState)
		assertEquals(expectedTask, vm.pickTask)
		assertNotNull(vm.currentPickItem)
	}

	@Test
	fun `fetchCurrentStep should set null when user has no current pick task`() = runTest {
		every { runBlocking { infoService.getUserPickTasks() } } returns Result.Success(null)

		vm.fetchCurrentStep()
		advanceUntilIdle()

		assertEquals(GenericViewState.Success, vm.viewState)
		assertNull(vm.pickTask)
		assertNull(vm.currentPickItem)
	}

	@Test
	fun `fetchCurrentStep should set null when failed to retrieve user's current pick task`() =
		runTest {
			every { runBlocking { infoService.getUserPickTasks() } } returns Result.Error()

			vm.fetchCurrentStep()
			advanceUntilIdle()

			assertEquals(GenericViewState.Failure, vm.viewState)
			assertNull(vm.pickTask)
			assertNull(vm.currentPickItem)
		}

	@Test
	fun `startPick, on error, should enter error state`() = runTest {
		val errMsg = "error message"
		every { runBlocking { pickService.startPick() } } returns Result.Error(msg = errMsg)
		vm.startPick()
		advanceUntilIdle()

		assertEquals(GenericViewState.Idle, vm.startPickState)
		assertNull(vm.pickTask)
	}

	@Test
	fun `startPick, on task not found error  should enter error state`() = runTest {
		val errMsg = "Task not found for user."
		every {
			runBlocking { pickService.startPick() }
		} returns Result.Error(msg = errMsg, type = NOT_FOUND)
		vm.startPick()
		advanceUntilIdle()

		assertEquals(GenericViewState.Failure, vm.startPickState)
		assertNull(vm.pickTask)
	}

	@Test
	fun `startPick, on success, should populate pickTask and enter success state`() = runTest {
		val pickTask = PickTask(
			id = 1,
			fulfillmentType = BOPIS,
			subFulfillmentType = SubFulfillmentType.BOPIS,
			totalQty = 2,
			totalRemainingQty = 2,
			totalWorkedQty = 0,
			items = emptyList()
		)
		every { runBlocking { pickService.startPick() } } returns Result.Success(pickTask)
		vm.startPick()
		advanceUntilIdle()

		assertEquals(GenericViewState.Success, vm.startPickState)
		assertEquals(pickTask, vm.pickTask)
	}

	@Test
	fun `startPick, on success, should populate pickTask and enter success state with currentPickItem`() =
		runTest {
			val pickTask = PickTask(
				id = 1,
				fulfillmentType = BOPIS,
				subFulfillmentType = SubFulfillmentType.BOPIS,
				totalQty = 2,
				totalRemainingQty = 2,
				totalWorkedQty = 0,
				items = listOf(
					PickTaskItem(
						pickedQty = 2,
						declinedQty = 3,
						qty = 10,
						onHandQty = 1,
						productBrand = "",
						productName = "",
						sku = "",
						upcs = emptyList(),
						style = ""
					)
				)
			)
			every { runBlocking { pickService.startPick() } } returns Result.Success(pickTask)
			vm.startPick()
			advanceUntilIdle()

			assertEquals(GenericViewState.Success, vm.startPickState)
			assertEquals(pickTask, vm.pickTask)
			assertTrue(vm.currentPickItem?.qty == 10)
		}

	@Test
	fun `declinePick, on error, should enter error state`() = runTest {
		val errMsg = "error message"
		every {
			runBlocking {
				pickService.declinePick(any())
			}
		} returns Result.Error(msg = errMsg)
		vm.declinePick("any", "any text")
		advanceUntilIdle()

		assertEquals(GenericViewState.Failure, vm.pickDeclineState)
		assertNull(vm.pickTask)
	}

	@Test
	fun `declinePick, on success, should populate pickTask and enter success state`() = runTest {
		val pickTask = PickTask(
			id = 1,
			fulfillmentType = BOPIS,
			subFulfillmentType = SubFulfillmentType.BOPIS,
			totalQty = 2,
			totalRemainingQty = 2,
			totalWorkedQty = 0,
			items = emptyList()
		)
		every { runBlocking { pickService.declinePick(any()) } } returns Result.Success(
			pickTask
		)
		vm.declinePick("any", "any text")
		advanceUntilIdle()

		assertEquals(GenericViewState.Success, vm.pickDeclineState)
		assertEquals(pickTask, vm.pickTask)
	}

	@Test
	fun `declinePick should send the correct declineQty for full decline`() = runTest {
		val task = PickTask(
			id = 1,
			fulfillmentType = BOPIS,
			subFulfillmentType = SubFulfillmentType.BOPIS,
			totalQty = 2,
			totalRemainingQty = 2,
			totalWorkedQty = 0,
			items = listOf(
				PickTaskItem(
					sku = "anySku",
					upcs = listOf("anyUpc"),
					onHandQty = 3,
					qty = 2,
					pickedQty = 0,
					declinedQty = 0,
					productName = "anyProduct",
					productBrand = "anyBrand",
					style = "style"
				)
			)
		)
		every { runBlocking { pickService.startPick() } } returns Result.Success(task)

		val slot = slot<DeclineItemRequest>()
		every {
			runBlocking {
				pickService.declinePick(capture(slot))
			}
		} returns mockk(relaxed = true)

		vm.startPick()
		advanceUntilIdle()
		vm.declinePick(declineReason = "justWantTo", declineReasonText = "decline text")
		advanceUntilIdle()

		assertEquals(2, slot.captured.declinedQty)
	}

	@Test
	fun `declinePick should send the correct declineQty for partial decline`() = runTest {
		val task = PickTask(
			id = 1,
			fulfillmentType = BOPIS,
			subFulfillmentType = SubFulfillmentType.BOPIS,
			totalQty = 2,
			totalRemainingQty = 1,
			totalWorkedQty = 1,
			items = listOf(
				PickTaskItem(
					sku = "anySku",
					upcs = listOf("anyUpc"),
					onHandQty = 3,
					qty = 2,
					pickedQty = 1,
					declinedQty = 0,
					productName = "anyProduct",
					productBrand = "anyBrand",
					style = "style"
				)
			)
		)
		every { runBlocking { pickService.startPick() } } returns Result.Success(task)

		val slot = slot<DeclineItemRequest>()
		every {
			runBlocking {
				pickService.declinePick(capture(slot))
			}
		} returns mockk(relaxed = true)

		vm.startPick()
		advanceUntilIdle()
		vm.declinePick(declineReason = "justWantTo", declineReasonText = "decline text")
		advanceUntilIdle()

		assertEquals(1, slot.captured.declinedQty)
	}

	@Test
	fun `pickItem, on id null, should enter error state`() = runTest {
		val errMsg = "task should not be Null"
		val pickItemRequest = PickItemRequest(
			taskId = 0,
			sku = "",
			scannedUpc = "",
			pickedQty = 0,
			pickedLocation = ""
		)
		every {
			runBlocking { pickService.pickItem(pickItemRequest) }
		} returns Result.Error(msg = errMsg, type = NOT_FOUND)
		vm.pickItem(upc = "upc", pickLocation = null)
		advanceUntilIdle()

		assertEquals(GenericViewState.Failure, vm.recordPickState)
		assertNull(vm.pickTask)
	}

	@Test
	fun `pickItem, on upc null, should enter error state`() = runTest {
		val errMsg = "Upc should not be Null or Blank"
		val pickItemRequest = PickItemRequest(
			taskId = 0,
			sku = "",
			scannedUpc = "",
			pickedQty = 0,
			pickedLocation = ""
		)
		every {
			runBlocking { pickService.pickItem(pickItemRequest) }
		} returns Result.Error(msg = errMsg, type = NOT_FOUND)
		vm.pickItem(upc = "", pickLocation = null)
		advanceUntilIdle()

		assertEquals(GenericViewState.Failure, vm.recordPickState)
		assertNull(vm.pickTask)
	}

	@Test
	fun `pickItem, on upc mis-match, should return false`() = runTest {
		val upc1 = "correctUpc1"
		val upc2 = "correctUpc2"
		val pickTask = PickTask(
			id = 1,
			fulfillmentType = BOPIS,
			subFulfillmentType = SubFulfillmentType.BOPIS,
			totalQty = 2,
			totalRemainingQty = 2,
			totalWorkedQty = 0,
			items = listOf(
				PickTaskItem(
					pickedQty = 2,
					declinedQty = 3,
					qty = 10,
					onHandQty = 1,
					productBrand = "",
					productName = "",
					sku = "",
					upcs = listOf(upc1, upc2),
					style = ""
				)
			)
		)
		every { runBlocking { pickService.startPick() } } returns Result.Success(pickTask)

		vm.startPick()
		advanceUntilIdle()
		val res = vm.pickItem(upc = "wrongUpc", pickLocation = null)
		advanceUntilIdle()

		assertFalse(res)
		verify(exactly = 0) {
			runBlocking { pickService.pickItem(any()) }
		}
	}

	@Test
	fun `pickItem, on upc match and successful API call, should return true`() = runTest {
		val upc1 = "correctUpc1"
		val upc2 = "correctUpc2"
		val pickTask = PickTask(
			id = 1,
			fulfillmentType = BOPIS,
			subFulfillmentType = SubFulfillmentType.BOPIS,
			totalQty = 2,
			totalRemainingQty = 2,
			totalWorkedQty = 0,
			items = listOf(
				PickTaskItem(
					pickedQty = 1,
					declinedQty = 0,
					qty = 10,
					onHandQty = 1,
					productBrand = "",
					productName = "",
					sku = "sku",
					upcs = listOf(upc1, upc2),
					style = ""
				)
			)
		)
		val req = PickItemRequest(
			taskId = 1,
			sku = "sku",
			scannedUpc = upc2,
			pickedQty = 1,
			pickedLocation = null
		)
		every { runBlocking { pickService.startPick() } } returns Result.Success(pickTask)
		every { runBlocking { pickService.pickItem(req) } } returns Result.Success(pickTask)

		vm.startPick()
		advanceUntilIdle()
		val res = vm.pickItem(upc = upc2, pickLocation = null)
		advanceUntilIdle()

		assertTrue(res)
		verify(exactly = 1) {
			runBlocking { pickService.pickItem(req) }
		}
	}
}
