package com.nextuple.nsf.ui.state

import androidx.lifecycle.SavedStateHandle
import com.nextuple.nsf.CoroutineRule
import com.nextuple.nsf.retrofit.dto.response.StoreOverviewResponse
import com.nextuple.nsf.retrofit.dto.response.StoreOverviewResponse.PrepOverview
import com.nextuple.nsf.service.AppService
import com.nextuple.nsf.service.dto.Result
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
class AppViewModelTest {
	@get:Rule
	val coroutineRule = CoroutineRule()

	@InjectMockKs
	private lateinit var vm: AppViewModel

	@Suppress("unused")
	@MockK
	private lateinit var savedStateHandle: SavedStateHandle

	@MockK
	private lateinit var appService: AppService

	@Before
	fun setUp() {
		MockKAnnotations.init(this)
	}

	@Test
	fun `storeOverview, on error, should enter error state`() = runTest {
		val errMsg = "error message"
		every { runBlocking { appService.getStoreOverview(any()) } } returns Result.Error(msg = errMsg)
		vm.getStoreOverview("dks") { _, _ -> run {} }
		advanceUntilIdle()

		Assert.assertEquals(errMsg, vm.errMsg)
	}

	@Test
	fun `storeOverview for empty dks`() = runTest {
		val errMsg = "Invalid DKS number."
		every { runBlocking { appService.getStoreOverview(any()) } } returns Result.Error(msg = errMsg)
		vm.getStoreOverview("") { _, _ -> run {} }
		advanceUntilIdle()

		Assert.assertEquals(errMsg, vm.errMsg)
	}

	@Test
	fun `storeOverview, on success, should populate userSummary and enter success state`() =
		runTest {
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
				prepOverview = PrepOverview(
					tasksInProgress = 0,
					tasksUnassigned = 0,
					prepTasks = emptyList()
				)
			)
			every { runBlocking { appService.getStoreOverview(any()) } } returns Result.Success(
				storeOverview
			)
			vm.getStoreOverview("dks") { _, _ -> run {} }
			advanceUntilIdle()

			Assert.assertNull(vm.errMsg)
		}
}
