package com.nextuple.nsf.ui.state

import androidx.lifecycle.SavedStateHandle
import com.nextuple.nsf.CoroutineRule
import com.nextuple.nsf.retrofit.dto.response.DeclineCode
import com.nextuple.nsf.retrofit.dto.response.GetDeclineCodesResponse
import com.nextuple.nsf.retrofit.dto.response.StoreOverviewResponse
import com.nextuple.nsf.retrofit.dto.response.StoreOverviewResponse.PickOverview
import com.nextuple.nsf.retrofit.dto.response.StoreOverviewResponse.PrepOverview
import com.nextuple.nsf.service.InfoService
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
class InfoViewModelTest {
	@get:Rule
	val coroutineRule = CoroutineRule()

	@InjectMockKs
	private lateinit var vm: InfoViewModel

	@Suppress("unused")
	@MockK
	private lateinit var savedStateHandle: SavedStateHandle

	@MockK
	private lateinit var infoService: InfoService

	@Before
	fun setUp() {
		MockKAnnotations.init(this)
	}

	@Test
	fun `storeOverview, on error, should enter error state`() = runTest {
		val errMsg = "error message"
		every { runBlocking { infoService.getStoreOverview() } } returns Result.Error(msg = errMsg)
		vm.getStoreOverview()
		advanceUntilIdle()

		Assert.assertEquals(errMsg, vm.errMsg)
	}

	@Test
	fun `storeOverview for empty userId`() = runTest {
		val errMsg = "Invalid Id."
		every { runBlocking { infoService.getStoreOverview() } } returns Result.Error(msg = errMsg)
		vm.getStoreOverview()
		advanceUntilIdle()

		Assert.assertEquals(errMsg, vm.errMsg)
	}

	@Test
	fun `storeOverview, on success, should populate userSummary and enter success state`() =
		runTest {
			val storeOverview = StoreOverviewResponse(
				pickOverview = PickOverview(
					tasksWorked = 2,
					tasksInProgress = 1,
					tasksUnassigned = 1,

					unitsUnassigned = 1,
					unitsWorked = 1,
					unitsInProgress = 1
				),
				prepOverview = PrepOverview(
					tasksInProgress = 0,
					tasksUnassigned = 0
				)
			)
			every { runBlocking { infoService.getStoreOverview() } } returns Result.Success(
				storeOverview
			)
			vm.getStoreOverview()
			advanceUntilIdle()

			Assert.assertNull(vm.errMsg)
		}

	@Test
	fun `getDeclineCodes, on error, should enter error state`() = runTest {
		val errMsg = "err msg"
		every {
			runBlocking { infoService.getDeclineCodes() }
		} returns Result.Error(msg = errMsg, type = Result.ErrorType.NOT_FOUND)
		vm.getDeclineCodes()
		advanceUntilIdle()

		Assert.assertEquals(GenericViewState.Failure, vm.declineCodesState)
		Assert.assertNull(vm.declineCodes)
		Assert.assertEquals(errMsg, vm.errMsg)
	}

	@Test
	fun `getDeclineCodes, on success, should populate declineCodes and enter success state`() =
		runTest {
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
				),
				pickupDeclineCodes = listOf(
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

			every { runBlocking { infoService.getDeclineCodes() } } returns Result.Success(
				declineCodes
			)
			vm.getDeclineCodes()
			advanceUntilIdle()

			Assert.assertEquals(GenericViewState.Success, vm.declineCodesState)
			Assert.assertEquals(declineCodes, vm.declineCodes)
			Assert.assertNull(vm.errMsg)
		}
}
