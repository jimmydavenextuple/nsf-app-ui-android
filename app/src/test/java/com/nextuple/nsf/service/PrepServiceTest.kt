package com.nextuple.nsf.service

import com.nextuple.nsf.CoroutineRule
import com.nextuple.nsf.retrofit.api.PrepApi
import com.nextuple.nsf.retrofit.dto.ApiResponse
import com.nextuple.nsf.retrofit.dto.PackTask
import com.nextuple.nsf.retrofit.dto.RecordHoldingLocationRequest
import com.nextuple.nsf.retrofit.dto.StageTask
import com.nextuple.nsf.retrofit.dto.StageTaskContainer
import com.nextuple.nsf.retrofit.dto.Status
import com.nextuple.nsf.service.dto.Result
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
class PrepServiceTest {

	@get:Rule
	val coroutineRule = CoroutineRule()

	@InjectMockKs
	private lateinit var service: PrepService

	@MockK
	private lateinit var prepApi: PrepApi

	@MockK
	private lateinit var deviceService: DeviceService

	@Before
	fun setUp() {
		MockKAnnotations.init(this)
	}

	@Test
	fun `startPack should call start pack api with the expected values`() = runTest {
		val dks = "dks123"
		val taskId = "taskId"
		val packTask = PackTask(
			id = 1,
			assignedUserId = "user1",
			fulfillmentRequestNumber = "demo123",
			totalQty = 2,
			totalRemainingQty = 2,
			totalWorkedQty = 0,
			items = emptyList()
		)

		every { runBlocking { prepApi.startPack(any(), any()) } } returns ApiResponse.Success(
			data = packTask
		)

		service.startPack(taskId = taskId, dks = dks)
		advanceUntilIdle()

		verify {
			runBlocking { prepApi.startPack(any(), any()) }
		}
	}

	@Test
	fun `startPack should return general error on api success response having null data`() = runTest {
		every { runBlocking { prepApi.startPack(any(), any()) } } returns ApiResponse.Success()

		val res = service.startPack(taskId = "taskId", dks = "anyDks")
		advanceUntilIdle()

		Assert.assertEquals(Result.generalError(), res)
	}

	@Test
	fun `packAndGetHoldSlip should call api with the expected values`() = runTest {
		val dks = "dks123"
		val taskId = "taskId"
		val stageTask = StageTask(
			id = 12,
			status = Status(code = "12", name = "COMPLETED"),
			fulfillmentRequestNumber = "123",
			containers = listOf(
				StageTaskContainer(id = 12, holdingLocation = "Hyderabad")
			)
		)

		every { runBlocking { prepApi.packAndGetHoldSlip(any(), any()) } } returns ApiResponse.Success(
			data = stageTask
		)

		service.packAndGetHoldSlip(taskId = taskId, dks = dks)
		advanceUntilIdle()

		verify {
			runBlocking { prepApi.packAndGetHoldSlip(any(), any()) }
		}
	}

	@Test
	fun `packAndGetHoldSlip should return general error on api success response having null data`() = runTest {
		every { runBlocking { prepApi.packAndGetHoldSlip(any(), any()) } } returns ApiResponse.Success()

		val res = service.packAndGetHoldSlip(taskId = "taskId", dks = "anyDks")
		advanceUntilIdle()

		Assert.assertEquals(Result.generalError(), res)
	}

	@Test
	fun `recordHoldingLocation should call api with the expected values`() = runTest {
		val dks = "dks123"
		val recordHoldingLocationRequest = RecordHoldingLocationRequest(
			containerId = 0,
			holdingLocation = ""
		)
		val stageTask = StageTask(
			id = 12,
			status = Status(code = "12", name = "COMPLETED"),
			fulfillmentRequestNumber = "123",
			containers = listOf(
				StageTaskContainer(id = 12, holdingLocation = "Hyderabad")
			)
		)

		every { runBlocking { prepApi.recordHoldingLocation(any(), any()) } } returns ApiResponse.Success(
			data = stageTask
		)

		service.recordHoldingLocation(recordHoldingLocationRequest = recordHoldingLocationRequest, dks = dks)
		advanceUntilIdle()

		verify {
			runBlocking { prepApi.recordHoldingLocation(any(), any()) }
		}
	}

	@Test
	fun `recordHoldingLocation should return general error on api success response having null data`() = runTest {
		val recordHoldingLocationRequest = RecordHoldingLocationRequest(
			containerId = 0,
			holdingLocation = ""
		)
		every { runBlocking { prepApi.recordHoldingLocation(any(), any()) } } returns ApiResponse.Success()

		val res = service.recordHoldingLocation(recordHoldingLocationRequest = recordHoldingLocationRequest, dks = "anyDks")
		advanceUntilIdle()

		Assert.assertEquals(Result.generalError(), res)
	}
}
