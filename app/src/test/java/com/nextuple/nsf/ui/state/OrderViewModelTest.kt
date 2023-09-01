package com.nextuple.nsf.ui.state

import androidx.lifecycle.SavedStateHandle
import com.nextuple.nsf.CoroutineRule
import com.nextuple.nsf.retrofit.dto.Status
import com.nextuple.nsf.retrofit.dto.response.FulfillmentRequestDetail
import com.nextuple.nsf.retrofit.dto.response.OrderDetailsResponse
import com.nextuple.nsf.service.OrderService
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
class OrderViewModelTest {

	@get:Rule
	val coroutineRule = CoroutineRule()

	@InjectMockKs
	private lateinit var vm: OrderViewModel

	@Suppress("unused")
	@MockK
	private lateinit var savedStateHandle: SavedStateHandle

	@MockK
	private lateinit var orderService: OrderService

	@Before
	fun setUp() {
		MockKAnnotations.init(this)
	}

	@Test
	fun `getOrderDetails, on error, should enter error state`() = runTest {
		val errMsg = "error message"
		every { runBlocking { orderService.getOrderDetails(any(), any()) } } returns Result.Error(msg = errMsg)
		vm.getOrderDetails(
			fulfillmentRequestNumber = "fulfillmentRequestNumber",
			dks = "dks"
		)
		advanceUntilIdle()

		Assert.assertEquals(GenericViewState.Failure, vm.orderDetailsState)
		Assert.assertNull(vm.orderDetailResponse)
		Assert.assertEquals(errMsg, vm.errMsg)
	}

	@Test
	fun `getOrderDetails, on success, should populate userSummary and enter success state`() = runTest {
		val orderDetailsResponse = OrderDetailsResponse(fulfillmentRequestDetail = FulfillmentRequestDetail(fulfillmentRequestNumber = "123"))
		every { runBlocking { orderService.getOrderDetails(any(), any()) } } returns Result.Success(orderDetailsResponse)
		vm.getOrderDetails(
			fulfillmentRequestNumber = "fulfillmentRequestNumber",
			dks = "dks"
		)
		advanceUntilIdle()

		Assert.assertEquals(GenericViewState.Success, vm.orderDetailsState)
		Assert.assertEquals(orderDetailsResponse, vm.orderDetailResponse)
		Assert.assertNull(vm.errMsg)
	}

	@Test
	fun `getOrders, on error, should enter error state`() = runTest {
		val errMsg = "error message"
		every {
			runBlocking {
				orderService.getOrders(
					any(),
					any(),
					any()
				)
			}
		} returns Result.Error(msg = errMsg)
		vm.getOrders(dks = "dks")
		advanceUntilIdle()

		Assert.assertEquals(GenericViewState.Failure, vm.viewState)
		Assert.assertNull(vm.orderList)
		Assert.assertEquals(errMsg, vm.errMsg)
	}

	@Test
	fun `getOrders, on success, should populate orderList and enter success state`() = runTest {
		val orderList = listOf(OrderDetailsResponse(fulfillmentRequestDetail = FulfillmentRequestDetail(fulfillmentRequestNumber = "123")))
		every { runBlocking { orderService.getOrders(any(), any(), any()) } } returns Result.Success(orderList)
		vm.getOrders(dks = "dks")
		advanceUntilIdle()

		Assert.assertEquals(GenericViewState.Success, vm.viewState)
		Assert.assertEquals(orderList, vm.orderList)
		Assert.assertNull(vm.errMsg)
	}

	@Test
	fun `getOrders, on success with pickupOrderList should populate orderList and enter success state`() = runTest {
		val orderList = listOf(
			OrderDetailsResponse(
				fulfillmentRequestDetail = FulfillmentRequestDetail(
					fulfillmentRequestNumber = "123",
					fulfillmentRequestStatus = Status(code = "1075", name = "")
				)
			),
			OrderDetailsResponse(
				fulfillmentRequestDetail = FulfillmentRequestDetail(
					fulfillmentRequestNumber = "123",
					fulfillmentRequestStatus = Status(code = "1090", name = "")
				)
			)
		)
		every { runBlocking { orderService.getOrders(any(), any(), any()) } } returns Result.Success(orderList)
		vm.getOrders(dks = "dks")
		advanceUntilIdle()

		Assert.assertEquals(GenericViewState.Success, vm.viewState)
		Assert.assertEquals(orderList, vm.orderList)
		Assert.assertEquals(vm.pickUpOrderList.value?.size, 2)
		Assert.assertNull(vm.errMsg)
	}

	@Test
	fun `pickupExtend, on error, should enter error state`() = runTest {
		val errMsg = "error message"
		every {
			runBlocking {
				orderService.pickupExtend(
					any(),
					any()
				)
			}
		} returns Result.Error(msg = errMsg)
		vm.pickupExtend(
			fulfillmentRequestNumber = "fulfillmentRequestNumber",
			dks = "dks"
		)
		advanceUntilIdle()

		Assert.assertEquals(GenericViewState.Failure, vm.viewState)
		Assert.assertNull(vm.orderDetailResponse)
		Assert.assertEquals(errMsg, vm.errMsg)
	}

	@Test
	fun `pickupExtend, on success, should populate userSummary and enter success state`() =
		runTest {
			val orderDetailsResponse = OrderDetailsResponse(fulfillmentRequestDetail = FulfillmentRequestDetail(fulfillmentRequestNumber = "123"))
			every {
				runBlocking {
					orderService.pickupExtend(
						any(),
						any()
					)
				}
			} returns Result.Success(orderDetailsResponse)
			vm.pickupExtend(
				fulfillmentRequestNumber = "fulfillmentRequestNumber",
				dks = "dks"
			)
			advanceUntilIdle()

			Assert.assertEquals(GenericViewState.Success, vm.viewState)
			Assert.assertEquals(orderDetailsResponse, vm.orderDetailResponse)
			Assert.assertNull(vm.errMsg)
		}

	@Test
	fun `pickupRemoveCheckIn, on error, should enter error state`() = runTest {
		val errMsg = "error message"
		every {
			runBlocking {
				orderService.pickupRemoveCheckIn(
					any(),
					any()
				)
			}
		} returns Result.Error(msg = errMsg)
		vm.pickupRemoveCheckIn(
			taskId = "taskId",
			dks = "dks"
		)
		advanceUntilIdle()

		Assert.assertEquals(GenericViewState.Failure, vm.viewState)
		Assert.assertNull(vm.orderDetailResponse)
		Assert.assertEquals(errMsg, vm.errMsg)
	}

	@Test
	fun `pickupRemoveCheckIn, on success, should populate userSummary and enter success state`() =
		runTest {
			val orderDetailsResponse = OrderDetailsResponse(fulfillmentRequestDetail = FulfillmentRequestDetail(fulfillmentRequestNumber = "123"))
			every {
				runBlocking {
					orderService.pickupRemoveCheckIn(
						any(),
						any()
					)
				}
			} returns Result.Success(orderDetailsResponse)
			vm.pickupRemoveCheckIn(
				taskId = "taskId",
				dks = "dks"
			)
			advanceUntilIdle()

			Assert.assertEquals(GenericViewState.Success, vm.viewState)
			Assert.assertEquals(orderDetailsResponse, vm.orderDetailResponse)
			Assert.assertNull(vm.errMsg)
		}

	@Test
	fun `startPickupTask, on error, should enter error state`() = runTest {
		val errMsg = "error message"
		every {
			runBlocking {
				orderService.startPickupTask(
					any(),
					any()
				)
			}
		} returns Result.Error(msg = errMsg)
		vm.startPickup(
			fulfillmentRequestNumber = "fulfillmentRequestNumber",
			dks = "dks"
		)
		advanceUntilIdle()

		Assert.assertEquals(GenericViewState.Failure, vm.startPickupState)
		Assert.assertNull(vm.orderDetailResponse)
		Assert.assertEquals(errMsg, vm.errMsg)
	}

	@Test
	fun `startPickupTask, on success, should populate userSummary and enter success state`() =
		runTest {
			val orderDetailsResponse = OrderDetailsResponse(fulfillmentRequestDetail = FulfillmentRequestDetail(fulfillmentRequestNumber = "123"))
			every {
				runBlocking {
					orderService.startPickupTask(
						any(),
						any()
					)
				}
			} returns Result.Success(orderDetailsResponse)
			vm.startPickup(
				fulfillmentRequestNumber = "fulfillmentRequestNumber",
				dks = "dks"
			)
			advanceUntilIdle()

			Assert.assertEquals(GenericViewState.Success, vm.startPickupState)
			Assert.assertEquals(orderDetailsResponse, vm.orderDetailResponse)
			Assert.assertNull(vm.errMsg)
		}

	@Test
	fun `completePickupTask, on error, should enter error state`() = runTest {
		val errMsg = "error message"
		every {
			runBlocking {
				orderService.completePickupTask(
					any(),
					any()
				)
			}
		} returns Result.Error(msg = errMsg)
		vm.completePickupTask(
			taskId = "taskId",
			dks = "dks"
		)
		advanceUntilIdle()

		Assert.assertEquals(GenericViewState.Failure, vm.completeOrderPickupState)
		Assert.assertNull(vm.orderDetailResponse)
		Assert.assertEquals(errMsg, vm.errMsg)
	}

	@Test
	fun `completePickupTask, on success, should populate userSummary and enter success state`() =
		runTest {
			val orderDetailsResponse = OrderDetailsResponse(fulfillmentRequestDetail = FulfillmentRequestDetail(fulfillmentRequestNumber = "123"))
			every {
				runBlocking {
					orderService.completePickupTask(
						any(),
						any()
					)
				}
			} returns Result.Success(orderDetailsResponse)
			vm.completePickupTask(
				taskId = "fulfillmentRequestNumber",
				dks = "dks"
			)
			advanceUntilIdle()

			Assert.assertEquals(GenericViewState.Success, vm.completeOrderPickupState)
			Assert.assertEquals(orderDetailsResponse, vm.orderDetailResponse)
			Assert.assertNull(vm.errMsg)
		}

	@Test
	fun `resetCompleteOrderPickState, should reset completeOrderPickupState`() = runTest {
		vm.resetCompleteOrderPickState()
		Assert.assertEquals(GenericViewState.Idle, vm.completeOrderPickupState)
	}

	@Test
	fun `resetOrderDetailsState, should reset orderDetailsState`() = runTest {
		vm.resetOrderDetailsState()
		Assert.assertEquals(GenericViewState.Idle, vm.orderDetailsState)
	}

	@Test
	fun `resetStarPickupState, should reset startPickupState`() = runTest {
		vm.resetStarPickupState()
		Assert.assertEquals(GenericViewState.Idle, vm.startPickupState)
	}
}
