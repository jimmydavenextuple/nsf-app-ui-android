package com.nextuple.nsf.service

import com.nextuple.nsf.CoroutineRule
import com.nextuple.nsf.datastore.UserRepository
import com.nextuple.nsf.retrofit.api.PickApi
import com.nextuple.nsf.retrofit.dto.ApiResponse
import com.nextuple.nsf.retrofit.dto.DeclineItemRequest
import com.nextuple.nsf.retrofit.dto.PickItemRequest
import com.nextuple.nsf.retrofit.dto.PickTask
import com.nextuple.nsf.service.dto.Brand
import com.nextuple.nsf.service.dto.Brand.NT_BRAND_A
import com.nextuple.nsf.service.dto.Result
import com.nextuple.nsf.service.dto.Store
import com.nextuple.nsf.util.FulfillmentType.BOPIS
import com.nextuple.nsf.util.SubFulfillmentType
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
	private lateinit var userRepository: UserRepository

	@MockK(relaxed = true)
	private lateinit var logService: LogService

	@Before
	fun setUp() {
		MockKAnnotations.init(this)
	}

	@Test
	fun `startPick should call pick api with the expected userId and store`() = runTest {
		val userId = "userId123"
		val store = Store(id = "456", brand = NT_BRAND_A)

		every { runBlocking { pickApi.startPick(any(), any()) } } returns ApiResponse.Success(
			data = PickTask(
				id = 1,
				fulfillmentType = BOPIS,
				subFulfillmentType = SubFulfillmentType.BOPIS,
				totalQty = 2,
				totalRemainingQty = 2,
				totalWorkedQty = 0,
				items = emptyList()
			)
		)
		every { runBlocking { userRepository.getStore() } } returns store
		every { runBlocking { userRepository.getUserId() } } returns userId

		service.startPick()
		advanceUntilIdle()

		verify {
			runBlocking { pickApi.startPick(store = store.id, userId = userId) }
		}
	}

	@Test
	fun `startPick should return general error on api success response having null data`() =
		runTest {
			every { runBlocking { pickApi.startPick(any(), any()) } } returns ApiResponse.Success()
			every { runBlocking { userRepository.getStore() } } returns Store(id = "0", brand = NT_BRAND_A)
			every { runBlocking { userRepository.getUserId() } } returns "userId"

			val res = service.startPick()
			advanceUntilIdle()

			assertEquals(Result.generalError(), res)
		}

	@Test
	fun `startPick should return general error if getStore is null`() = runTest {
		every { runBlocking { userRepository.getStore() } } returns null

		val res = service.startPick()
		advanceUntilIdle()

		assertEquals(Result.generalError(), res)
	}

	@Test
	fun `startPick should return general error if getUserId is null`() = runTest {
		every { runBlocking { userRepository.getStore() } } returns Store(id = "456", brand = Brand.NT_BRAND_A)
		every { runBlocking { userRepository.getUserId() } } returns null

		val res = service.startPick()
		advanceUntilIdle()

		assertEquals(Result.generalError(), res)
	}

	@Test
	fun `declinePick should call decline pick api with the expected values`() = runTest {
		val declineItemRequest = DeclineItemRequest(
			taskId = 0,
			sku = "",
			declinedQty = 0,
			declineReason = "",
			declineReasonText = ""
		)

		every { runBlocking { userRepository.getUserId() } } returns "userId"
		every { runBlocking { pickApi.pickDecline(any(), any()) } } returns ApiResponse.Success(
			data = PickTask(
				id = 1,
				fulfillmentType = BOPIS,
				subFulfillmentType = SubFulfillmentType.BOPIS,
				totalQty = 2,
				totalRemainingQty = 2,
				totalWorkedQty = 0,
				items = emptyList()
			)
		)

		service.declinePick(declineItemRequest)
		advanceUntilIdle()

		verify {
			runBlocking { pickApi.pickDecline(any(), any()) }
		}
	}

	@Test
	fun `declinePick should return general error on api success response having null data`() =
		runTest {
			val declineItemRequest = DeclineItemRequest(
				taskId = 0,
				sku = "",
				declinedQty = 0,
				declineReason = "",
				declineReasonText = ""
			)
			every {
				runBlocking { pickApi.pickDecline(any(), any()) }
			} returns ApiResponse.Success()
			every { runBlocking { userRepository.getUserId() } } returns "userId"

			val res = service.declinePick(declineItemRequest)
			advanceUntilIdle()

			assertEquals(Result.generalError(), res)
		}

	@Test
	fun `declinePick should return general error if getUserId is null`() = runTest {
		every { runBlocking { userRepository.getUserId() } } returns null

		val res = service.declinePick(
			DeclineItemRequest(
				taskId = 0,
				sku = "",
				declinedQty = 0,
				declineReason = "",
				declineReasonText = ""
			)
		)
		advanceUntilIdle()

		assertEquals(Result.generalError(), res)
	}

	@Test
	fun `pickItem should call pickItem api with the expected values`() = runTest {
		val pickItemRequest = PickItemRequest(
			taskId = 0,
			sku = "",
			pickedQty = 1,
			scannedUpc = "upc"
		)
		every { runBlocking { userRepository.getUserId() } } returns "userId"
		every { runBlocking { pickApi.recordPick(any(), any()) } } returns ApiResponse.Success(
			data = PickTask(
				id = 1,
				fulfillmentType = BOPIS,
				subFulfillmentType = SubFulfillmentType.BOPIS,
				totalQty = 2,
				totalRemainingQty = 2,
				totalWorkedQty = 0,
				items = emptyList()
			)
		)

		service.pickItem(pickItemRequest)
		advanceUntilIdle()

		verify {
			runBlocking { pickApi.recordPick(any(), any()) }
		}
	}

	@Test
	fun `pickItem should return general error on api success response having null data`() =
		runTest {
			val pickItemRequest = PickItemRequest(
				taskId = 0,
				sku = "",
				pickedQty = 1,
				scannedUpc = "upc"
			)
			every { runBlocking { pickApi.recordPick(any(), any()) } } returns ApiResponse.Success()

			val res = service.pickItem(pickItemRequest)
			advanceUntilIdle()

			assertEquals(Result.generalError(), res)
		}

	@Test
	fun `pickItem should return general error if getUserId is null`() = runTest {
		every { runBlocking { userRepository.getUserId() } } returns null

		val res = service.pickItem(
			PickItemRequest(
				taskId = 0,
				sku = "",
				pickedQty = 1,
				scannedUpc = "upc"
			)
		)
		advanceUntilIdle()

		assertEquals(Result.generalError(), res)
	}
}
