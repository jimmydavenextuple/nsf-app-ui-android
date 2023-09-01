package com.nextuple.nsf.ui.state

import androidx.lifecycle.SavedStateHandle
import com.nextuple.nsf.CoroutineRule
import com.nextuple.nsf.retrofit.dto.PackTask
import com.nextuple.nsf.retrofit.dto.StageTask
import com.nextuple.nsf.retrofit.dto.StageTaskContainer
import com.nextuple.nsf.retrofit.dto.Status
import com.nextuple.nsf.service.PrepService
import com.nextuple.nsf.service.dto.Result
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
class PrepViewModelTest {
	@get:Rule
	val coroutineRule = CoroutineRule()

	@InjectMockKs
	private lateinit var vm: PrepViewModel

	@Suppress("unused")
	@MockK
	private lateinit var savedStateHandle: SavedStateHandle

	@MockK
	private lateinit var prepService: PrepService

	@Before
	fun setUp() {
		MockKAnnotations.init(this)
	}

	@Test
	fun `startPack, on task not found error  should enter error state`() = runTest {
		val errMsg = "Task not found for user."
		every {
			runBlocking { prepService.startPack(any(), any()) }
		} returns Result.Error(msg = errMsg, type = Result.ErrorType.NOT_FOUND)
		vm.startPack("taskId", "dks")
		advanceUntilIdle()

		Assert.assertEquals(GenericViewState.Failure, vm.startPackState)
		Assert.assertNull(vm.packTask)
		Assert.assertEquals(errMsg, vm.errMsg)
	}

	@Test
	fun `startPack, on success, should populate pickTask and enter success state`() = runTest {
		val packTask = PackTask(
			id = 1,
			assignedUserId = "user1",
			fulfillmentRequestNumber = "demo123",
			totalQty = 2,
			totalRemainingQty = 2,
			totalWorkedQty = 0,
			items = emptyList()
		)
		every { runBlocking { prepService.startPack(any(), any()) } } returns Result.Success(packTask)
		vm.startPack("1", "dks")
		advanceUntilIdle()

		Assert.assertEquals(GenericViewState.Success, vm.startPackState)
		Assert.assertEquals(packTask, vm.packTask)
		Assert.assertNull(vm.errMsg)
	}

	@Test
	fun `recordHoldingLocation, on task not found error  should enter error state`() = runTest {
		val errMsg = "error message"
		every {
			runBlocking { prepService.recordHoldingLocation(any(), any()) }
		} returns Result.Error(msg = errMsg, type = Result.ErrorType.NOT_FOUND)
		vm.recordHoldingLocation(0, "location", "dks")
		advanceUntilIdle()

		Assert.assertEquals(GenericViewState.Failure, vm.holdLocationState)
		Assert.assertNull(vm.stageTask)
		Assert.assertEquals(errMsg, vm.errMsg)
	}

	@Test
	fun `recordHoldingLocation, on success, should populate stageTask and enter success state`() = runTest {
		val stageTask = StageTask(
			id = 12,
			status = Status(code = "12", name = "COMPLETED"),
			fulfillmentRequestNumber = "123",
			containers = listOf(
				StageTaskContainer(id = 12, holdingLocation = "Hyderabad")
			)
		)
		every { runBlocking { prepService.recordHoldingLocation(any(), any()) } } returns Result.Success(stageTask)
		vm.recordHoldingLocation(0, "location", "dks")
		advanceUntilIdle()

		Assert.assertEquals(GenericViewState.Success, vm.holdLocationState)
		Assert.assertEquals(stageTask, vm.stageTask)
		Assert.assertNull(vm.errMsg)
	}

	@Test
	fun `onStoreOverViewCompletion storeOverview, packTask and stageTask values should be assigned`() = runTest {
		vm.onStoreOverViewCompletion(null)

		Assert.assertNull(vm.storeOverview)
		Assert.assertNull(vm.packTask)
		Assert.assertNull(vm.stageTask)
	}

	@Test
	fun `resetPackState should reset startPackState`() = runTest {
		vm.resetPackState()

		Assert.assertEquals(GenericViewState.Idle, vm.startPackState)
	}

	@Test
	fun `resetHoldSlipState should reset holdSlipState`() = runTest {
		vm.resetHoldSlipState()

		Assert.assertEquals(GenericViewState.Idle, vm.holdSlipState)
	}

	@Test
	fun `resetHoldLocationState should reset holdLocationState`() = runTest {
		vm.resetHoldLocationState()

		Assert.assertEquals(GenericViewState.Idle, vm.holdLocationState)
	}
}
