package com.nextuple.nsf.service

import com.nextuple.nsf.CoroutineRule
import com.nextuple.nsf.datastore.UserRepository
import com.nextuple.nsf.retrofit.api.InfoApi
import com.nextuple.nsf.retrofit.dto.ApiResponse
import com.nextuple.nsf.retrofit.dto.response.DeclineCode
import com.nextuple.nsf.retrofit.dto.response.GetDeclineCodesResponse
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
class InfoServiceTest {

	@get:Rule
	val coroutineRule = CoroutineRule()

	@InjectMockKs
	private lateinit var service: InfoService

	@MockK
	private lateinit var infoApi: InfoApi

	@MockK
	private lateinit var deviceService: DeviceService

	@MockK
	private lateinit var userRepository: UserRepository

	@Before
	fun setUp() {
		MockKAnnotations.init(this)
	}

	@Test
	fun `getStoreOverview should call api with the expected values`() = runTest {
		val dks = "dks123"
		val store = Store(id = "456", brand = Brand.NT_BRAND_A)
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

		every { runBlocking { infoApi.getStoreOverview(store.id, dks) } } returns ApiResponse.Success(
			storeOverview
		)
		every { deviceService.getStore() } returns store
		every { runBlocking { userRepository.getDks() } } returns dks

		service.getStoreOverview()
		advanceUntilIdle()

		verify {
			runBlocking { infoApi.getStoreOverview(store.id, dks) }
		}
	}

	@Test
	fun `getStoreOverview should return general error on api success response having null data`() = runTest {
		every { runBlocking { infoApi.getStoreOverview(any(), any()) } } returns ApiResponse.Success()
		every { deviceService.getStore() } returns Store(id = "456", brand = Brand.NT_BRAND_A)
		every { runBlocking { userRepository.getDks() } } returns "dks"

		val res = service.getStoreOverview()
		advanceUntilIdle()

		Assert.assertEquals(Result.generalError(), res)
	}

	@Test
	fun `getStoreOverview should return general error if getStore is null`() = runTest {
		every { deviceService.getStore() } returns null

		val res = service.getStoreOverview()
		advanceUntilIdle()

		Assert.assertEquals(Result.generalError(), res)
	}

	@Test
	fun `getStoreOverview should return general error if getDks is null`() = runTest {
		every { deviceService.getStore() } returns Store(id = "456", brand = Brand.NT_BRAND_A)
		every { runBlocking { userRepository.getDks() } } returns null

		val res = service.getStoreOverview()
		advanceUntilIdle()

		Assert.assertEquals(Result.generalError(), res)
	}

	@Test
	fun `getDeclineCodes should return general error on api success response having null data`() =
		runTest {
			val dks = "dks123"
			val store = Store(id = "456", brand = Brand.NT_BRAND_A)
			every {
				runBlocking {
					infoApi.getDeclineCodes(
						userId = dks,
						brand = Brand.NT_BRAND_A.name,
						store = "456",
						fulfillmentType = "BOPIS",
						subFulfillmentType = "BOPIS"
					)
				}
			} returns ApiResponse.Success()
			every { deviceService.getStore() } returns store
			every { runBlocking { userRepository.getDks() } } returns dks

			val res = service.getDeclineCodes()
			advanceUntilIdle()

			Assert.assertEquals(Result.generalError(), res)
		}

	@Test
	fun `getDeclineCodes should call get decline codes api with the expected values`() = runTest {
		val dks = "dks123"
		val store = Store(id = "456", brand = Brand.NT_BRAND_A)
		val declineCodes = GetDeclineCodesResponse(
			pickDeclineCodes = listOf(
				DeclineCode(id = "", displayName = ""),
				DeclineCode(id = "", displayName = ""),
				DeclineCode(id = "", displayName = "")
			),
			pickupDeclineCodes = listOf(
				DeclineCode(id = "", displayName = ""),
				DeclineCode(id = "", displayName = ""),
				DeclineCode(id = "", displayName = "")
			)
		)

		every {
			runBlocking {
				infoApi.getDeclineCodes(
					userId = dks,
					brand = Brand.NT_BRAND_A.name,
					store = "456",
					fulfillmentType = "BOPIS",
					subFulfillmentType = "BOPIS"
				)
			}
		} returns ApiResponse.Success(
			data = declineCodes
		)

		every { deviceService.getStore() } returns store
		every { runBlocking { userRepository.getDks() } } returns dks

		service.getDeclineCodes()
		advanceUntilIdle()

		verify {
			runBlocking {
				infoApi.getDeclineCodes(
					userId = dks,
					brand = Brand.NT_BRAND_A.name,
					store = "456",
					fulfillmentType = "BOPIS",
					subFulfillmentType = "BOPIS"
				)
			}
		}
	}

	@Test
	fun `getDeclineCodes should return general error if getStore is null`() = runTest {
		every { deviceService.getStore() } returns null

		val res = service.getDeclineCodes()
		advanceUntilIdle()

		Assert.assertEquals(Result.generalError(), res)
	}

	@Test
	fun `getDeclineCodes should return general error if getDks is null`() = runTest {
		every { deviceService.getStore() } returns Store(id = "456", brand = Brand.NT_BRAND_A)
		every { runBlocking { userRepository.getDks() } } returns null

		val res = service.getDeclineCodes()
		advanceUntilIdle()

		Assert.assertEquals(Result.generalError(), res)
	}
}
