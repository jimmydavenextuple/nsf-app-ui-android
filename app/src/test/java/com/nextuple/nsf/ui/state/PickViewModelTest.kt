package com.nextuple.nsf.ui.state

import androidx.lifecycle.SavedStateHandle
import com.nextuple.nsf.CoroutineRule
import com.nextuple.nsf.retrofit.dto.PickItemRequest
import com.nextuple.nsf.retrofit.dto.PickTask
import com.nextuple.nsf.retrofit.dto.PickTaskItem
import com.nextuple.nsf.retrofit.dto.response.DeclineCode
import com.nextuple.nsf.retrofit.dto.response.GetDeclineCodesResponse
import com.nextuple.nsf.retrofit.dto.response.StoreOverviewResponse
import com.nextuple.nsf.service.PickService
import com.nextuple.nsf.service.dto.Result
import com.nextuple.nsf.service.dto.Result.ErrorType.NOT_FOUND
import com.nextuple.nsf.ui.util.GenericViewState
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
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

	@Before
	fun setUp() {
		MockKAnnotations.init(this)
	}

	@Test
	fun `startPick, on error, should enter error state`() = runTest {
		val errMsg = "error message"
		every { runBlocking { pickService.startPick(any()) } } returns Result.Error(msg = errMsg)
		vm.startPick("dks")
		advanceUntilIdle()

		Assert.assertEquals(GenericViewState.Idle, vm.startPickState)
		Assert.assertNull(vm.pickTask)
		Assert.assertEquals(errMsg, vm.errMsg)
	}

	@Test
	fun `startPick, on task not found error  should enter error state`() = runTest {
		val errMsg = "Task not found for user."
		every {
			runBlocking { pickService.startPick(any()) }
		} returns Result.Error(msg = errMsg, type = NOT_FOUND)
		vm.startPick("dks")
		advanceUntilIdle()

		Assert.assertEquals(GenericViewState.Failure, vm.startPickState)
		Assert.assertNull(vm.pickTask)
		Assert.assertEquals(errMsg, vm.errMsg)
	}

	@Test
	fun `startPick, on success, should populate pickTask and enter success state`() = runTest {
		val pickTask = PickTask(
			id = 1,
			totalQty = 2,
			totalRemainingQty = 2,
			totalWorkedQty = 0,
			items = emptyList()
		)
		every { runBlocking { pickService.startPick("1") } } returns Result.Success(pickTask)
		vm.startPick("1")
		advanceUntilIdle()

		Assert.assertEquals(GenericViewState.Success, vm.startPickState)
		Assert.assertEquals(pickTask, vm.pickTask)
		Assert.assertNull(vm.errMsg)
	}

	@Test
	fun `startPick, on success, should populate pickTask and enter success state with currentPickItem`() = runTest {
		val pickTask = PickTask(
			id = 1,
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
					upcs = emptyList()
				)
			)
		)
		every { runBlocking { pickService.startPick("1") } } returns Result.Success(pickTask)
		vm.startPick("1")
		advanceUntilIdle()

		Assert.assertEquals(GenericViewState.Success, vm.startPickState)
		Assert.assertEquals(pickTask, vm.pickTask)
		Assert.assertTrue(vm.currentPickItem.value?.qty == 10)
		Assert.assertNull(vm.errMsg)
	}

	@Test
	fun `storeOverview, on success, should populate pickTask and enter success state with currentPickItem`() = runTest {
		val storeOverview = StoreOverviewResponse(
			userOverview = StoreOverviewResponse.UserOverview(),
			pickOverview = StoreOverviewResponse.PickOverview(
				tasksWorked = 2,
				tasksInProgress = 1,
				tasksUnassigned = 1,

				unitsUnassigned = 1,
				unitsWorked = 1,
				unitsInProgress = 1
			),
			prepOverview = StoreOverviewResponse.PrepOverview(
				tasksInProgress = 0,
				tasksUnassigned = 2,
				prepTasks = emptyList()
			)
		)
		vm.onStoreOverViewCompletion(GenericViewState.Success, storeOverviewResponse = storeOverview)
		advanceUntilIdle()

		Assert.assertEquals(GenericViewState.Success, vm.storeOverviewState)
		Assert.assertTrue(vm.totalStoreUnits.value == 3)
		Assert.assertTrue(vm.pickTasksUnassigned.value == 1)
		Assert.assertTrue(vm.prepTasksUnassigned.value == 2)
	}

	@Test
	fun `declinePick, on error, should enter error state`() = runTest {
		val errMsg = "error message"
		every {
			runBlocking {
				pickService.declinePick(
					any(),
					any()
				)
			}
		} returns Result.Error(msg = errMsg)
		vm.declinePick("any", "dks")
		advanceUntilIdle()

		Assert.assertEquals(GenericViewState.Failure, vm.pickDeclineState)
		Assert.assertNull(vm.pickTask)
		Assert.assertEquals(errMsg, vm.errMsg)
	}

	@Test
	fun `declinePick, on success, should populate pickTask and enter success state`() = runTest {
		val pickTask = PickTask(
			id = 1,
			totalQty = 2,
			totalRemainingQty = 2,
			totalWorkedQty = 0,
			items = emptyList()
		)
		every { runBlocking { pickService.declinePick(any(), any()) } } returns Result.Success(
			pickTask
		)
		vm.declinePick("any", "dks")
		advanceUntilIdle()

		Assert.assertEquals(GenericViewState.Success, vm.pickDeclineState)
		Assert.assertEquals(pickTask, vm.pickTask)
		Assert.assertNull(vm.errMsg)
	}

	@Test
	fun `getDeclineCodes, on error, should enter error state`() = runTest {
		val errMsg = "err msg"
		every {
			runBlocking { pickService.getDeclineCodes("dks") }
		} returns Result.Error(msg = errMsg, type = NOT_FOUND)
		vm.getDeclineCodes("dks")
		advanceUntilIdle()

		Assert.assertEquals(GenericViewState.Failure, vm.declineCodesState)
		Assert.assertNull(vm.declineCodes)
		Assert.assertEquals(errMsg, vm.errMsg)
	}

	@Test
	fun `getDeclineCodes, on success, should populate declineCodes and enter success state`() = runTest {
		val declineCodes = GetDeclineCodesResponse(
			pickDeclineCodes = listOf(
				DeclineCode(
					id = "",
					displayName = ""
				),
				DeclineCode(
					id = "",
					displayName = ""
				),
				DeclineCode(
					id = "",
					displayName = ""
				)
			)
		)

		every { runBlocking { pickService.getDeclineCodes("dks") } } returns Result.Success(
			declineCodes
		)
		vm.getDeclineCodes("dks")
		advanceUntilIdle()

		Assert.assertEquals(GenericViewState.Success, vm.declineCodesState)
		Assert.assertEquals(declineCodes, vm.declineCodes)
		Assert.assertNull(vm.errMsg)
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
			runBlocking { pickService.pickItem(pickItemRequest, "dks") }
		} returns Result.Error(msg = errMsg, type = NOT_FOUND)
		vm.pickItem("upc", "dks")
		advanceUntilIdle()

		Assert.assertEquals(GenericViewState.Failure, vm.recordPickState)
		Assert.assertNull(vm.pickTask)
		Assert.assertEquals(errMsg, vm.errMsg)
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
			runBlocking { pickService.pickItem(pickItemRequest, "dks") }
		} returns Result.Error(msg = errMsg, type = NOT_FOUND)
		vm.pickItem("", "dks")
		advanceUntilIdle()

		Assert.assertEquals(GenericViewState.Failure, vm.recordPickState)
		Assert.assertNull(vm.pickTask)
		Assert.assertEquals(errMsg, vm.errMsg)
	}
}
