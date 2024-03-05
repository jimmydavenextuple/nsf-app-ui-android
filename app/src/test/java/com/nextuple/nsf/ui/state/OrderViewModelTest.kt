package com.nextuple.nsf.ui.state

import androidx.lifecycle.SavedStateHandle
import com.nextuple.nsf.CoroutineRule
import com.nextuple.nsf.retrofit.dto.RecordDeclineResponse
import com.nextuple.nsf.retrofit.dto.StageTask
import com.nextuple.nsf.retrofit.dto.Status
import com.nextuple.nsf.retrofit.dto.response.AthleteCheckInDetail
import com.nextuple.nsf.retrofit.dto.response.DeclineCode
import com.nextuple.nsf.retrofit.dto.response.FulfillmentRequestDetail
import com.nextuple.nsf.retrofit.dto.response.OrderDetailsResponse
import com.nextuple.nsf.service.LogService
import com.nextuple.nsf.service.OrderService
import com.nextuple.nsf.service.StageTaskService
import com.nextuple.nsf.service.dto.Result
import com.nextuple.nsf.ui.component.filter.Filter
import com.nextuple.nsf.ui.util.GenericViewState.Failure
import com.nextuple.nsf.ui.util.GenericViewState.Idle
import com.nextuple.nsf.ui.util.GenericViewState.Success
import com.nextuple.nsf.util.OrderStatus
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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

	@MockK
	private lateinit var stageTaskService: StageTaskService

	@Suppress("unused")
	@MockK
	private lateinit var logService: LogService

	@Before
	fun setUp() {
		MockKAnnotations.init(this)

		justRun { logService.trackError(any(), any(), any()) }
	}

	@Test
	fun `getOrderDetails, on error, should enter error state`() = runTest {
		val errMsg = "error message"
		every { runBlocking { orderService.getOrderDetails(any()) } } returns Result.Error(msg = errMsg)
		vm.getOrderDetails(
			fulfillmentRequestNumber = "fulfillmentRequestNumber"
		)
		advanceUntilIdle()

		assertEquals(Failure, vm.orderDetailsState)
		assertNull(vm.orderDetailResponse)
		assertEquals(errMsg, vm.errMsg)
	}

	@Test
	fun `getOrderDetails, on success, should populate userSummary and enter success state`() = runTest {
		val orderDetailsResponse = OrderDetailsResponse(fulfillmentRequestDetail = FulfillmentRequestDetail(fulfillmentRequestNumber = "123"))
		every { runBlocking { orderService.getOrderDetails(any()) } } returns Result.Success(orderDetailsResponse)
		vm.getOrderDetails(
			fulfillmentRequestNumber = "fulfillmentRequestNumber"
		)
		advanceUntilIdle()

		assertEquals(Success, vm.orderDetailsState)
		assertEquals(orderDetailsResponse, vm.orderDetailResponse)
		assertNull(vm.errMsg)
	}

	@Test
	fun `getOrders, on error, should enter error state`() = runTest {
		val errMsg = "error message"
		every {
			runBlocking {
				orderService.getOrders(any(), any(), any(), any())
			}
		} returns Result.Error(msg = errMsg)
		vm.getOrders()
		advanceUntilIdle()

		assertEquals(Failure, vm.viewState)
		assertTrue(vm.readyOrders.isEmpty())
		assertTrue(vm.inProgressOrders.isEmpty())
		assertEquals(errMsg, vm.errMsg)
	}

	@Test
	fun `getOrders, on success, should populate orderList and enter success state`() = runTest {
		val orderList = listOf(OrderDetailsResponse(fulfillmentRequestDetail = FulfillmentRequestDetail(fulfillmentRequestNumber = "123")))
		every { runBlocking { orderService.getOrders(any(), any(), any(), any()) } } returns Result.Success(orderList)
		vm.getOrders()
		advanceUntilIdle()

		assertEquals(Success, vm.viewState)
		assertTrue(orderList.containsAll(vm.readyOrders))
		assertTrue(orderList.containsAll(vm.inProgressOrders))
		assertNull(vm.errMsg)
	}

	@Test
	fun `getOrders, on success with pickupOrderList should populate orderList and enter success state`() =
		runTest {
			val orderList = listOf(
				OrderDetailsResponse(
					fulfillmentRequestDetail = FulfillmentRequestDetail(
						fulfillmentRequestNumber = "123",
						fulfillmentRequestStatus = Status(code = "1075", name = "")
					),
					athleteCheckInDetail = AthleteCheckInDetail(checkInTime = "testTime")
				),
				OrderDetailsResponse(
					fulfillmentRequestDetail = FulfillmentRequestDetail(
						fulfillmentRequestNumber = "123",
						fulfillmentRequestStatus = Status(code = "1090", name = "")
					)
				)
			)
			every { runBlocking { orderService.getOrders(any(), any(), any(), any()) } } returns Result.Success(orderList)
			vm.getOrders()
			advanceUntilIdle()

			assertEquals(Success, vm.viewState)
			assertTrue(orderList.containsAll(vm.readyOrders))
			assertTrue(orderList.containsAll(vm.inProgressOrders))
			assertNull(vm.errMsg)
		}

	@Test
	fun `getOrders should supply minOrderStatus of PACK`() = runTest {
		every {
			runBlocking {
				orderService.getOrders(
					query = any(),
					orderTypeFilter = any(),
					orderStatusFilter = any(),
					minOrderStatus = any()
				)
			}
		} returns Result.Success(emptyList())

		vm.getOrders()
		advanceUntilIdle()

		verify {
			runBlocking {
				orderService.getOrders(
					query = any(),
					orderTypeFilter = any(),
					orderStatusFilter = any(),
					minOrderStatus = OrderStatus.PACK.statusText
				)
			}
		}
	}

	@Test
	fun `getOrders, after fresh init, should have no selected filters`() = runTest {
		every {
			runBlocking {
				orderService.getOrders(
					query = any(),
					orderTypeFilter = any(),
					orderStatusFilter = any(),
					minOrderStatus = any()
				)
			}
		} returns Result.Success(emptyList())

		vm.getOrders()
		advanceUntilIdle()

		verify {
			runBlocking {
				orderService.getOrders(
					query = any(),
					orderTypeFilter = emptyList(),
					orderStatusFilter = emptyList(),
					minOrderStatus = any()
				)
			}
		}
	}

	@Test
	fun `getOrders should supply selected filters if any`() = runTest {
		every {
			runBlocking {
				orderService.getOrders(
					query = any(),
					orderTypeFilter = any(),
					orderStatusFilter = any(),
					minOrderStatus = any()
				)
			}
		} returns Result.Success(emptyList())

		val orderTypes = listOf(Filter("a", false), Filter("b", true))
		val orderStatuses = listOf(Filter("hungry", true), Filter("thirsty", true))

		vm.setFilters(orderTypes, orderStatuses)
		vm.getOrders()
		advanceUntilIdle()

		verify {
			runBlocking {
				orderService.getOrders(
					query = any(),
					orderTypeFilter = listOf("b"),
					orderStatusFilter = listOf("hungry", "thirsty"),
					minOrderStatus = any()
				)
			}
		}
	}

	@Test
	fun `pickupExtend, on error, should enter error state`() = runTest {
		val errMsg = "error message"
		every {
			runBlocking {
				orderService.pickupExtend(any())
			}
		} returns Result.Error(msg = errMsg)
		vm.pickupExtend(
			fulfillmentRequestNumber = "fulfillmentRequestNumber"
		)
		advanceUntilIdle()

		assertEquals(Failure, vm.viewState)
		assertNull(vm.orderDetailResponse)
		assertEquals(errMsg, vm.errMsg)
	}

	@Test
	fun `pickupExtend, on success, should populate userSummary and enter success state`() =
		runTest {
			val orderDetailsResponse = OrderDetailsResponse(
				fulfillmentRequestDetail = FulfillmentRequestDetail(fulfillmentRequestNumber = "123")
			)
			every {
				runBlocking {
					orderService.pickupExtend(any())
				}
			} returns Result.Success(orderDetailsResponse)
			vm.pickupExtend(
				fulfillmentRequestNumber = "fulfillmentRequestNumber"
			)
			advanceUntilIdle()

			assertEquals(Success, vm.viewState)
			assertEquals(orderDetailsResponse, vm.orderDetailResponse)
			assertNull(vm.errMsg)
		}

	@Test
	fun `pickupRemoveCheckIn, on error, should enter error state`() = runTest {
		val errMsg = "error message"
		every {
			runBlocking {
				orderService.pickupRemoveCheckIn(any())
			}
		} returns Result.Error(msg = errMsg)
		vm.pickupRemoveCheckIn(
			taskId = "taskId"
		)
		advanceUntilIdle()

		assertEquals(Failure, vm.viewState)
		assertNull(vm.orderDetailResponse)
		assertEquals(errMsg, vm.errMsg)
	}

	@Test
	fun `pickupRemoveCheckIn, on success, should populate userSummary and enter success state`() =
		runTest {
			val orderDetailsResponse = OrderDetailsResponse(
				fulfillmentRequestDetail = FulfillmentRequestDetail(fulfillmentRequestNumber = "123")
			)
			every {
				runBlocking {
					orderService.pickupRemoveCheckIn(any())
				}
			} returns Result.Success(orderDetailsResponse)
			vm.pickupRemoveCheckIn(
				taskId = "taskId"
			)
			advanceUntilIdle()

			assertEquals(Success, vm.viewState)
			assertEquals(orderDetailsResponse, vm.orderDetailResponse)
			assertNull(vm.errMsg)
		}

	@Test
	fun `startPickupTask, on error, should enter error state`() = runTest {
		val errMsg = "error message"
		every {
			runBlocking {
				orderService.startPickupTask(any())
			}
		} returns Result.Error(msg = errMsg)
		vm.startPickup(
			fulfillmentRequestNumber = "fulfillmentRequestNumber"
		)
		advanceUntilIdle()

		assertEquals(Failure, vm.startPickupState)
		assertNull(vm.orderDetailResponse)
		assertEquals(errMsg, vm.errMsg)
	}

	@Test
	fun `startPickupTask, on success, should populate userSummary and enter success state`() =
		runTest {
			val orderDetailsResponse = OrderDetailsResponse(
				fulfillmentRequestDetail = FulfillmentRequestDetail(fulfillmentRequestNumber = "123")
			)
			every {
				runBlocking {
					orderService.startPickupTask(any())
				}
			} returns Result.Success(orderDetailsResponse)
			vm.startPickup(
				fulfillmentRequestNumber = "fulfillmentRequestNumber"
			)
			advanceUntilIdle()

			assertEquals(Success, vm.startPickupState)
			assertEquals(orderDetailsResponse, vm.orderDetailResponse)
			assertNull(vm.errMsg)
		}

	@Test
	fun `completePickupTask, on error, should enter error state`() = runTest {
		val errMsg = "error message"
		every {
			runBlocking {
				orderService.completePickupTask(any())
			}
		} returns Result.Error(msg = errMsg)
		vm.completePickupTask(
			taskId = "taskId"
		)
		advanceUntilIdle()

		assertEquals(Failure, vm.completeOrderPickupState)
		assertNull(vm.orderDetailResponse)
		assertEquals(errMsg, vm.errMsg)
	}

	@Test
	fun `completePickupTask, on success, should populate userSummary and enter success state`() =
		runTest {
			val orderDetailsResponse = OrderDetailsResponse(
				fulfillmentRequestDetail = FulfillmentRequestDetail(fulfillmentRequestNumber = "123")
			)
			every {
				runBlocking {
					orderService.completePickupTask(any())
				}
			} returns Result.Success(orderDetailsResponse)
			vm.completePickupTask(
				taskId = "fulfillmentRequestNumber"
			)
			advanceUntilIdle()

			assertEquals(Success, vm.completeOrderPickupState)
			assertEquals(orderDetailsResponse, vm.orderDetailResponse)
			assertNull(vm.errMsg)
		}

	@Test
	fun `getHoldSlip should set holdSlipZpl on success`() = runTest {
		val holdSlipZpl = mutableListOf("zpl")
		val stageTask = StageTask(
			id = 12,
			fulfillmentRequestNumber = "123",
			containers = listOf(),
			holdSlipZPL = holdSlipZpl
		)
		every { runBlocking { stageTaskService.getHoldSlip(any()) } } returns Result.Success(
			data = stageTask
		)

		vm.getHoldSlip("123")
		advanceUntilIdle()

		assertEquals(Success, vm.holdSlipState)
		assertEquals(holdSlipZpl, vm.holdSlipZpl)
		assertNull(vm.errMsg)
	}

	@Test
	fun `getHoldSlip should set holdSlipZpl to null on failure`() = runTest {
		val errorMsg = "error"

		every { runBlocking { stageTaskService.getHoldSlip(any()) } } returns Result.Error(msg = errorMsg)

		vm.getHoldSlip("123")
		advanceUntilIdle()

		assertEquals(Failure, vm.holdSlipState)
		assertEquals(errorMsg, vm.errMsg)
		assertNull(vm.holdSlipZpl)
	}

	@Test
	fun `cancelOrder should set cancelReasonData on success when the declined reason is damaged`() = runTest {
		val recordDeclineResponse = RecordDeclineResponse(true, "")
		every { runBlocking { orderService.getOrderDetails(any()) } } returns mockk()
		every { runBlocking { orderService.recordDecline(any(), any(), any(), any()) } } returns Result.Success(
			data = recordDeclineResponse
		)

		vm.cancelOrder("123", DeclineCode(id = "DAMAGE", displayName = "DAMAGE"), false)
		advanceUntilIdle()

		assertEquals(Success, vm.cancelReasonData.state)
		assertEquals(true, vm.cancelReasonData.isDamaged)
	}

	@Test
	fun `cancelOrder should set cancelReasonData on success when the declined reason is not damaged`() = runTest {
		val recordDeclineResponse = RecordDeclineResponse(true, "")
		every { runBlocking { orderService.getOrderDetails(any()) } } returns mockk()
		every { runBlocking { orderService.recordDecline(any(), any(), any(), any()) } } returns Result.Success(
			data = recordDeclineResponse
		)

		vm.cancelOrder("123", DeclineCode(id = "CUSTOMER_REQUEST", displayName = "CUSTOMER REQUEST"), false)
		advanceUntilIdle()

		assertEquals(Success, vm.cancelReasonData.state)
		assertEquals(false, vm.cancelReasonData.isDamaged)
	}

	@Test
	fun `cancelOrder should set cancelReasonData on failure`() = runTest {
		every { runBlocking { orderService.recordDecline(any(), any(), any(), any()) } } returns Result.Error()

		vm.cancelOrder("123", DeclineCode(id = "CUSTOMER_REQUEST", displayName = "CUSTOMER REQUEST"), false)
		advanceUntilIdle()

		assertEquals(Failure, vm.cancelReasonData.state)
	}

	@Test
	fun `resetCompleteOrderPickState, should reset completeOrderPickupState`() = runTest {
		vm.resetCompleteOrderPickState()
		assertEquals(Idle, vm.completeOrderPickupState)
	}

	@Test
	fun `resetOrderDetailsState, should reset orderDetailsState`() = runTest {
		vm.resetOrderDetailsState()
		assertEquals(Idle, vm.orderDetailsState)
	}

	@Test
	fun `resetStarPickupState, should reset startPickupState`() = runTest {
		vm.resetStarPickupState()
		assertEquals(Idle, vm.startPickupState)
	}

	@Test
	fun `resetHoldSlipState, should reset holdSlipState`() = runTest {
		vm.resetHoldSlipState()
		assertEquals(Idle, vm.holdSlipState)
	}

	@Test
	fun `resetCancelReasonData, should reset cancelReasonData`() = runTest {
		vm.resetCancelReasonData()
		assertEquals(Idle, vm.cancelReasonData.state)
		assertEquals(false, vm.cancelReasonData.isDamaged)
	}
}
