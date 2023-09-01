package com.nextuple.nsf.service

import com.nextuple.nsf.CoroutineRule
import com.nextuple.nsf.retrofit.api.PickApi
import com.nextuple.nsf.retrofit.dto.ApiResponse
import com.nextuple.nsf.retrofit.dto.DeclineItemRequest
import com.nextuple.nsf.retrofit.dto.PickItemRequest
import com.nextuple.nsf.retrofit.dto.PickTask
import com.nextuple.nsf.retrofit.dto.response.DeclineCode
import com.nextuple.nsf.retrofit.dto.response.GetDeclineCodesResponse
import com.nextuple.nsf.service.dto.Brand.NEXTUPLE
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
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PickServiceTest {

	@get:Rule
	val coroutineRule = CoroutineRule()

	@InjectMockKs
	private lateinit var service: PickService

	@MockK
	private lateinit var pickApi: PickApi

	@MockK
	private lateinit var deviceService: DeviceService

	@Before
	fun setUp() {
		MockKAnnotations.init(this)
	}

	@Test
	fun `startPick should call pick api with the expected dks and store`() = runTest {
		val dks = "dks123"
		val store = Store(id = "456", brand = NEXTUPLE)

		every { runBlocking { pickApi.startPick(any(), any()) } } returns ApiResponse.Success(
			data = PickTask(
				id = 1,
				totalQty = 2,
				totalRemainingQty = 2,
				totalWorkedQty = 0,
				items = emptyList()
			)
		)
		every { deviceService.getStore() } returns store

		service.startPick(dks = dks)
		advanceUntilIdle()

		verify {
			runBlocking { pickApi.startPick(store = store.id, userId = dks) }
		}
	}

	@Test
	fun `startPick should return general error on api success response having null data`() = runTest {
		every { runBlocking { pickApi.startPick(any(), any()) } } returns ApiResponse.Success()
		every { deviceService.getStore() } returns Store(id = "0", brand = NEXTUPLE)

		val res = service.startPick(dks = "anyDks")
		advanceUntilIdle()

		assertEquals(Result.generalError(), res)
	}

	@Test
	fun `declinePick should call decline pick api with the expected values`() = runTest {
		val dks = "dks123"
		val declineItemRequest = DeclineItemRequest(
			taskId = 0,
			sku = "",
			declinedQty = 0,
			declineReason = ""
		)

		every { runBlocking { pickApi.pickDecline(any(), any()) } } returns ApiResponse.Success(
			data = PickTask(
				id = 1,
				totalQty = 2,
				totalRemainingQty = 2,
				totalWorkedQty = 0,
				items = emptyList()
			)
		)

		service.declinePick(declineItemRequest, dks = dks)
		advanceUntilIdle()

		verify {
			runBlocking { pickApi.pickDecline(any(), any()) }
		}
	}

	@Test
	fun `declinePick should return general error on api success response having null data`() = runTest {
		val dks = "dks123"
		val declineItemRequest = DeclineItemRequest(
			taskId = 0,
			sku = "",
			declinedQty = 0,
			declineReason = ""
		)
		every { runBlocking { pickApi.pickDecline(any(), any()) } } returns ApiResponse.Success()

		val res = service.declinePick(declineItemRequest, dks)
		advanceUntilIdle()

		assertEquals(Result.generalError(), res)
	}

	@Test
	fun `pickItem should call pickItem api with the expected values`() = runTest {
		val dks = "dks123"
		val pickItemRequest = PickItemRequest(
			taskId = 0,
			sku = "",
			pickedQty = 1,
			scannedUpc = "upc"
		)

		every { runBlocking { pickApi.recordPick(any(), any()) } } returns ApiResponse.Success(
			data = PickTask(
				id = 1,
				totalQty = 2,
				totalRemainingQty = 2,
				totalWorkedQty = 0,
				items = emptyList()
			)
		)

		service.pickItem(pickItemRequest, dks = dks)
		advanceUntilIdle()

		verify {
			runBlocking { pickApi.recordPick(any(), any()) }
		}
	}

	@Test
	fun `pickItem should return general error on api success response having null data`() = runTest {
		val dks = "dks123"
		val pickItemRequest = PickItemRequest(
			taskId = 0,
			sku = "",
			pickedQty = 1,
			scannedUpc = "upc"
		)
		every { runBlocking { pickApi.recordPick(any(), any()) } } returns ApiResponse.Success()

		val res = service.pickItem(pickItemRequest, dks)
		advanceUntilIdle()

		assertEquals(Result.generalError(), res)
	}

	@Test
	fun `getDeclineCodes should call get decline codes api with the expected values`() = runTest {
		val dks = "dks123"
		val store = Store(id = "456", brand = NEXTUPLE)
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

		every {
			runBlocking {
				pickApi.getDeclineCodes(
					userId = dks,
					brand = NEXTUPLE.name,
					store = "456",
					fulfillmentType = "BOPIS",
					subFulfillmentType = "BOPIS"
				)
			}
		} returns ApiResponse.Success(
			data = declineCodes
		)

		every { deviceService.getStore() } returns store

		service.getDeclineCodes(dks = dks)
		advanceUntilIdle()

		verify {
			runBlocking {
				pickApi.getDeclineCodes(
					userId = dks,
					brand = NEXTUPLE.name,
					store = "456",
					fulfillmentType = "BOPIS",
					subFulfillmentType = "BOPIS"
				)
			}
		}
	}

	@Test
	fun `getDeclineCodes should return general error on api success response having null data`() =
		runTest {
			val dks = "dks123"
			val store = Store(id = "456", brand = NEXTUPLE)
			every {
				runBlocking {
					pickApi.getDeclineCodes(
						userId = dks,
						brand = NEXTUPLE.name,
						store = "456",
						fulfillmentType = "BOPIS",
						subFulfillmentType = "BOPIS"
					)
				}
			} returns ApiResponse.Success()
			every { deviceService.getStore() } returns store

			val res = service.getDeclineCodes(dks)
			advanceUntilIdle()

			assertEquals(Result.generalError(), res)
		}
}
