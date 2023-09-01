package com.nextuple.nsf.service

import com.nextuple.nsf.CoroutineRule
import com.nextuple.nsf.retrofit.api.OrderApi
import com.nextuple.nsf.retrofit.dto.ApiResponse
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
import org.junit.Assert
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

	@Before
	fun setUp() {
		MockKAnnotations.init(this)
	}

	@Test
	fun `getOrders should return general error on api success response having null data`() = runTest {
		every { runBlocking { orderApi.getOrders(any(), any(), any(), any()) } } returns ApiResponse.Success()
		every { deviceService.getStore() } returns Store(id = "0", brand = Brand.DSG)

		val res = service.getOrders(query = null, dks = "anyDks", pastDays = null)
		advanceUntilIdle()

		Assert.assertEquals(Result.generalError(), res)
	}

	@Test
	fun `getOrders should call getOrders api with the expected values`() = runTest {
		val dks = "dks123"

		every { runBlocking { orderApi.getOrders(any(), any(), any(), any()) } } returns ApiResponse.Success(
			data = listOf(OrderDetailsResponse(fulfillmentRequestDetail = FulfillmentRequestDetail(fulfillmentRequestNumber = "123")))
		)
		every { deviceService.getStore() } returns Store(id = "0", brand = Brand.DSG)
		service.getOrders(query = "any", dks = dks, pastDays = null)
		advanceUntilIdle()

		verify {
			runBlocking { orderApi.getOrders(any(), any(), any(), any()) }
		}
	}

	@Test
	fun `getOrderDetails should return general error on api success response having null data`() = runTest {
		every { runBlocking { orderApi.orderDetails(any(), any()) } } returns ApiResponse.Success()
		every { deviceService.getStore() } returns Store(id = "0", brand = Brand.DSG)

		val res = service.getOrderDetails(dks = "anyDks", fulfillmentRequestNumber = "anyNumber")
		advanceUntilIdle()

		Assert.assertEquals(Result.generalError(), res)
	}

	@Test
	fun `getOrderDetails should call getOrderDetails api with the expected values`() = runTest {
		val dks = "dks123"

		every { runBlocking { orderApi.orderDetails(any(), any()) } } returns ApiResponse.Success(
			data = OrderDetailsResponse(fulfillmentRequestDetail = FulfillmentRequestDetail(fulfillmentRequestNumber = "123"))
		)

		service.getOrderDetails(dks = dks, fulfillmentRequestNumber = "any_number")
		advanceUntilIdle()

		verify {
			runBlocking { orderApi.orderDetails(any(), any()) }
		}
	}

	@Test
	fun `startPickup should return general error on api success response having null data`() = runTest {
		every { runBlocking { orderApi.startPickupTask(any(), any()) } } returns ApiResponse.Success()
		every { deviceService.getStore() } returns Store(id = "0", brand = Brand.DSG)

		val res = service.startPickupTask(dks = "anyDks", fulfillmentRequestNumber = "anyNumber")
		advanceUntilIdle()

		Assert.assertEquals(Result.generalError(), res)
	}

	@Test
	fun `startPickup should call startPickup api with the expected values`() = runTest {
		val dks = "dks123"

		every { runBlocking { orderApi.startPickupTask(any(), any()) } } returns ApiResponse.Success(
			data = OrderDetailsResponse(fulfillmentRequestDetail = FulfillmentRequestDetail(fulfillmentRequestNumber = "123"))
		)

		service.startPickupTask(dks = dks, fulfillmentRequestNumber = "any_number")
		advanceUntilIdle()

		verify {
			runBlocking { orderApi.startPickupTask(any(), any()) }
		}
	}

	@Test
	fun `extendPickup should return general error on api success response having null data`() = runTest {
		every { runBlocking { orderApi.pickupExtend(any(), any()) } } returns ApiResponse.Success()
		every { deviceService.getStore() } returns Store(id = "0", brand = Brand.DSG)

		val res = service.pickupExtend(dks = "anyDks", fulfillmentRequestNumber = "anyNumber")
		advanceUntilIdle()

		Assert.assertEquals(Result.generalError(), res)
	}

	@Test
	fun `extendPickup should call extendPickup api with the expected values`() = runTest {
		val dks = "dks123"

		every { runBlocking { orderApi.pickupExtend(any(), any()) } } returns ApiResponse.Success(
			data = OrderDetailsResponse(fulfillmentRequestDetail = FulfillmentRequestDetail(fulfillmentRequestNumber = "123"))
		)

		service.pickupExtend(dks = dks, fulfillmentRequestNumber = "any_number")
		advanceUntilIdle()

		verify {
			runBlocking { orderApi.pickupExtend(any(), any()) }
		}
	}

	@Test
	fun `removeCheckIn should return general error on api success response having null data`() = runTest {
		every { runBlocking { orderApi.pickupRemoveCheckIn(any(), any()) } } returns ApiResponse.Success()
		every { deviceService.getStore() } returns Store(id = "0", brand = Brand.DSG)

		val res = service.pickupRemoveCheckIn(dks = "anyDks", taskId = "anyNumber")
		advanceUntilIdle()

		Assert.assertEquals(Result.generalError(), res)
	}

	@Test
	fun `removeCheckIn should call removeCheckIn api with the expected values`() = runTest {
		val dks = "dks123"

		every { runBlocking { orderApi.pickupRemoveCheckIn(any(), any()) } } returns ApiResponse.Success(
			data = OrderDetailsResponse(fulfillmentRequestDetail = FulfillmentRequestDetail(fulfillmentRequestNumber = "123"))
		)

		service.pickupRemoveCheckIn(dks = dks, taskId = "any_number")
		advanceUntilIdle()

		verify {
			runBlocking { orderApi.pickupRemoveCheckIn(any(), any()) }
		}
	}

	@Test
	fun `completePickupTask should return general error on api success response having null data`() = runTest {
		every { runBlocking { orderApi.completePickupTask(any(), any()) } } returns ApiResponse.Success()
		every { deviceService.getStore() } returns Store(id = "0", brand = Brand.DSG)

		val res = service.completePickupTask(dks = "anyDks", taskId = "anyNumber")
		advanceUntilIdle()

		Assert.assertEquals(Result.generalError(), res)
	}

	@Test
	fun `completePickupTask should call completePickupTask api with the expected values`() = runTest {
		val dks = "dks123"

		every { runBlocking { orderApi.completePickupTask(any(), any()) } } returns ApiResponse.Success(
			data = OrderDetailsResponse(fulfillmentRequestDetail = FulfillmentRequestDetail(fulfillmentRequestNumber = "123"))
		)

		service.completePickupTask(dks = dks, taskId = "any_number")
		advanceUntilIdle()

		verify {
			runBlocking { orderApi.completePickupTask(any(), any()) }
		}
	}
}
