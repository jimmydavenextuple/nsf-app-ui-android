package com.nextuple.nsf.service

import com.nextuple.nsf.CoroutineRule
import com.nextuple.nsf.datastore.UserRepository
import com.nextuple.nsf.retrofit.api.OrderApi
import com.nextuple.nsf.retrofit.dto.ApiResponse
import com.nextuple.nsf.retrofit.dto.RecordDeclineResponse
import com.nextuple.nsf.retrofit.dto.response.FulfillmentRequestDetail
import com.nextuple.nsf.retrofit.dto.response.OrderDetailsResponse
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
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrderServiceTest {

	@get:Rule
	val coroutineRule = CoroutineRule()

	@InjectMockKs
	private lateinit var service: OrderService

	@MockK
	private lateinit var orderApi: OrderApi

	@MockK
	private lateinit var deviceService: DeviceService

	@MockK
	private lateinit var userRepository: UserRepository

	@Before
	fun setUp() {
		MockKAnnotations.init(this)
	}

	@Test
	fun `getOrders should return general error on api success response having null data`() =
		runTest {
			every {
				runBlocking {
					orderApi.getOrders(
						any(),
						any(),
						any(),
						any(),
						any(),
						any(),
						any()
					)
				}
			} returns ApiResponse.Success()
			every { deviceService.getStore() } returns Store(id = "0", brand = Brand.NT_BRAND_A)
			every { runBlocking { userRepository.getDks() } } returns "dks"

			val res = service.getOrders(query = null)
			advanceUntilIdle()

			assertEquals(Result.generalError(), res)
		}

	@Test
	fun `getOrders should call getOrders api with the expected values`() = runTest {
		every {
			runBlocking {
				orderApi.getOrders(
					any(),
					any(),
					any(),
					any(),
					any(),
					any(),
					any()
				)
			}
		} returns ApiResponse.Success(
			data = listOf(
				OrderDetailsResponse(
					fulfillmentRequestDetail = FulfillmentRequestDetail(
						fulfillmentRequestNumber = "123"
					)
				)
			)
		)
		every { deviceService.getStore() } returns Store(id = "0", brand = Brand.NT_BRAND_A)
		every { runBlocking { userRepository.getDks() } } returns "dks"

		service.getOrders(query = "any")
		advanceUntilIdle()

		verify {
			runBlocking { orderApi.getOrders(any(), any(), any(), any(), any(), any(), any()) }
		}
	}

	@Test
	fun `getOrders should return general error if getStore is null`() = runTest {
		every { deviceService.getStore() } returns null

		val res = service.getOrders(query = "any")
		advanceUntilIdle()

		assertEquals(Result.generalError(), res)
	}

	@Test
	fun `getOrders should return general error if getDks is null`() = runTest {
		every { deviceService.getStore() } returns Store(id = "456", brand = Brand.NT_BRAND_A)
		every { runBlocking { userRepository.getDks() } } returns null

		val res = service.getOrders(query = "any")
		advanceUntilIdle()

		assertEquals(Result.generalError(), res)
	}

	@Test
	fun `getOrderDetails should return general error on api success response having null data`() =
		runTest {
			every {
				runBlocking {
					orderApi.orderDetails(
						any(),
						any()
					)
				}
			} returns ApiResponse.Success()
			every { deviceService.getStore() } returns Store(id = "0", brand = Brand.NT_BRAND_A)
			every { runBlocking { userRepository.getDks() } } returns "dks"

			val res = service.getOrderDetails(fulfillmentRequestNumber = "anyNumber")
			advanceUntilIdle()

			assertEquals(Result.generalError(), res)
		}

	@Test
	fun `getOrderDetails should call getOrderDetails api with the expected values`() = runTest {
		every { runBlocking { userRepository.getDks() } } returns "dks"
		every { runBlocking { orderApi.orderDetails(any(), any()) } } returns ApiResponse.Success(
			data = OrderDetailsResponse(
				fulfillmentRequestDetail = FulfillmentRequestDetail(
					fulfillmentRequestNumber = "123"
				)
			)
		)

		service.getOrderDetails(fulfillmentRequestNumber = "any_number")
		advanceUntilIdle()

		verify {
			runBlocking { orderApi.orderDetails(any(), any()) }
		}
	}

	@Test
	fun `getOrderDetails should return general error if getDks is null`() = runTest {
		every { runBlocking { userRepository.getDks() } } returns null

		val res = service.getOrderDetails(fulfillmentRequestNumber = "any_number")
		advanceUntilIdle()

		assertEquals(Result.generalError(), res)
	}

	@Test
	fun `startPickup should return general error on api success response having null data`() =
		runTest {
			every {
				runBlocking {
					orderApi.startPickupTask(
						any(),
						any()
					)
				}
			} returns ApiResponse.Success()
			every { deviceService.getStore() } returns Store(id = "0", brand = Brand.NT_BRAND_A)
			every { runBlocking { userRepository.getDks() } } returns "dks"

			val res = service.startPickupTask(fulfillmentRequestNumber = "anyNumber")
			advanceUntilIdle()

			assertEquals(Result.generalError(), res)
		}

	@Test
	fun `startPickup should call startPickup api with the expected values`() = runTest {
		every { runBlocking { userRepository.getDks() } } returns "dks"
		every {
			runBlocking {
				orderApi.startPickupTask(
					any(),
					any()
				)
			}
		} returns ApiResponse.Success(
			data = OrderDetailsResponse(
				fulfillmentRequestDetail = FulfillmentRequestDetail(
					fulfillmentRequestNumber = "123"
				)
			)
		)

		service.startPickupTask(fulfillmentRequestNumber = "any_number")
		advanceUntilIdle()

		verify {
			runBlocking { orderApi.startPickupTask(any(), any()) }
		}
	}

	@Test
	fun `startPickup should return general error if getDks is null`() = runTest {
		every { runBlocking { userRepository.getDks() } } returns null

		val res = service.startPickupTask(fulfillmentRequestNumber = "any_number")
		advanceUntilIdle()

		assertEquals(Result.generalError(), res)
	}

	@Test
	fun `extendPickup should return general error on api success response having null data`() =
		runTest {
			every {
				runBlocking {
					orderApi.pickupExtend(
						any(),
						any()
					)
				}
			} returns ApiResponse.Success()
			every { deviceService.getStore() } returns Store(id = "0", brand = Brand.NT_BRAND_A)
			every { runBlocking { userRepository.getDks() } } returns "dks"

			val res = service.pickupExtend(fulfillmentRequestNumber = "anyNumber")
			advanceUntilIdle()

			assertEquals(Result.generalError(), res)
		}

	@Test
	fun `extendPickup should call extendPickup api with the expected values`() = runTest {
		every { runBlocking { orderApi.pickupExtend(any(), any()) } } returns ApiResponse.Success(
			data = OrderDetailsResponse(
				fulfillmentRequestDetail = FulfillmentRequestDetail(
					fulfillmentRequestNumber = "123"
				)
			)
		)
		every { runBlocking { userRepository.getDks() } } returns "dks"

		service.pickupExtend(fulfillmentRequestNumber = "any_number")
		advanceUntilIdle()

		verify {
			runBlocking { orderApi.pickupExtend(any(), any()) }
		}
	}

	@Test
	fun `extendPickup should return general error if getDks is null`() = runTest {
		every { runBlocking { userRepository.getDks() } } returns null

		val res = service.pickupExtend(fulfillmentRequestNumber = "any_number")
		advanceUntilIdle()

		assertEquals(Result.generalError(), res)
	}

	@Test
	fun `removeCheckIn should return general error on api success response having null data`() =
		runTest {
			every {
				runBlocking {
					orderApi.pickupRemoveCheckIn(
						any(),
						any()
					)
				}
			} returns ApiResponse.Success()
			every { deviceService.getStore() } returns Store(id = "0", brand = Brand.NT_BRAND_A)
			every { runBlocking { userRepository.getDks() } } returns "dks"

			val res = service.pickupRemoveCheckIn(taskId = "anyNumber")
			advanceUntilIdle()

			assertEquals(Result.generalError(), res)
		}

	@Test
	fun `removeCheckIn should call removeCheckIn api with the expected values`() = runTest {
		every {
			runBlocking {
				orderApi.pickupRemoveCheckIn(
					any(),
					any()
				)
			}
		} returns ApiResponse.Success(
			data = OrderDetailsResponse(
				fulfillmentRequestDetail = FulfillmentRequestDetail(
					fulfillmentRequestNumber = "123"
				)
			)
		)
		every { runBlocking { userRepository.getDks() } } returns "dks"

		service.pickupRemoveCheckIn(taskId = "any_number")
		advanceUntilIdle()

		verify {
			runBlocking { orderApi.pickupRemoveCheckIn(any(), any()) }
		}
	}

	@Test
	fun `removeCheckIn should return general error if getDks is null`() = runTest {
		every { runBlocking { userRepository.getDks() } } returns null

		val res = service.pickupRemoveCheckIn(taskId = "any_number")
		advanceUntilIdle()

		assertEquals(Result.generalError(), res)
	}

	@Test
	fun `completePickupTask should return general error on api success response having null data`() =
		runTest {
			every {
				runBlocking {
					orderApi.completePickupTask(
						any(),
						any()
					)
				}
			} returns ApiResponse.Success()
			every { deviceService.getStore() } returns Store(id = "0", brand = Brand.NT_BRAND_A)
			every { runBlocking { userRepository.getDks() } } returns "dks"

			val res = service.completePickupTask(taskId = "anyNumber")
			advanceUntilIdle()

			assertEquals(Result.generalError(), res)
		}

	@Test
	fun `completePickupTask should call completePickupTask api with the expected values`() =
		runTest {
			every {
				runBlocking {
					orderApi.completePickupTask(
						any(),
						any()
					)
				}
			} returns ApiResponse.Success(
				data = OrderDetailsResponse(
					fulfillmentRequestDetail = FulfillmentRequestDetail(
						fulfillmentRequestNumber = "123"
					)
				)
			)
			every { runBlocking { userRepository.getDks() } } returns "dks"

			service.completePickupTask(taskId = "any_number")
			advanceUntilIdle()

			verify {
				runBlocking { orderApi.completePickupTask(any(), any()) }
			}
		}

	@Test
	fun `completePickupTask should return general error if getDks is null`() = runTest {
		every { runBlocking { userRepository.getDks() } } returns null

		val res = service.completePickupTask(taskId = "any_number")
		advanceUntilIdle()

		assertEquals(Result.generalError(), res)
	}

	@Test
	fun `recordDecline should call recordDecline api with the expected values`() = runTest {
		every { runBlocking { orderApi.recordDecline(any(), any()) } } returns ApiResponse.Success(
			RecordDeclineResponse(true, "")
		)
		every { runBlocking { userRepository.getDks() } } returns "dks"

		service.recordDecline(
			fulfillmentRequestNumber = "",
			declinedReason = "",
			shouldTranslateReason = false,
			action = ""
		)
		advanceUntilIdle()

		verify {
			runBlocking { orderApi.recordDecline(any(), any()) }
		}
	}

	@Test
	fun `recordDecline should return generalError is the response is null`() = runTest {
		every { runBlocking { orderApi.recordDecline(any(), any()) } } returns ApiResponse.Success()
		every { runBlocking { userRepository.getDks() } } returns "dks"

		val res = service.recordDecline(
			fulfillmentRequestNumber = "",
			declinedReason = "",
			shouldTranslateReason = false,
			action = ""
		)
		advanceUntilIdle()

		assertEquals(Result.generalError(), res)
	}

	@Test
	fun `recordDecline should return general error if getDks is null`() = runTest {
		every { runBlocking { userRepository.getDks() } } returns null

		val res = service.recordDecline(
			fulfillmentRequestNumber = "",
			declinedReason = "",
			shouldTranslateReason = false,
			action = ""
		)
		advanceUntilIdle()

		assertEquals(Result.generalError(), res)
	}
}
