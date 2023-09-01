package com.nextuple.nsf.service

import com.nextuple.nsf.CoroutineRule
import com.nextuple.nsf.retrofit.api.AppApi
import com.nextuple.nsf.retrofit.dto.ApiResponse
import com.nextuple.nsf.retrofit.dto.response.StoreOverviewResponse
import com.nextuple.nsf.service.dto.Brand
import com.nextuple.nsf.service.dto.Result
import com.nextuple.nsf.service.dto.Store
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppServiceTest {

	@get:Rule
	val coroutineRule = CoroutineRule()

	@InjectMockKs
	private lateinit var service: AppService

	@MockK
	private lateinit var appApi: AppApi

	@MockK
	private lateinit var deviceService: DeviceService

	@Before
	fun setUp() {
		MockKAnnotations.init(this)
	}

	@Test
	fun `getStoreOverview should call api with the expected values`() = runTest {
		val id = "456"
		val store = Store(id = id, brand = Brand.DSG)
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
				tasksUnassigned = 0,
				prepTasks = emptyList()
			)
		)

		every { runBlocking { appApi.getStoreOverview(store.id, id) } } returns ApiResponse.Success(
			storeOverview
		)
		every { deviceService.getStore() } returns store

		service.getStoreOverview("456")
		advanceUntilIdle()

		verify {
			runBlocking { appApi.getStoreOverview(store.id, id) }
		}
	}

	@Test
	fun `getStoreOverview should return general error on api success response having null data`() = runTest {
		val store = Store(id = "456", brand = Brand.DSG)
		every { runBlocking { appApi.getStoreOverview(any(), any()) } } returns ApiResponse.Success()
		every { deviceService.getStore() } returns store

		val res = service.getStoreOverview(dks = "anyDks")
		advanceUntilIdle()

		Assert.assertEquals(Result.generalError(), res)
	}
}
