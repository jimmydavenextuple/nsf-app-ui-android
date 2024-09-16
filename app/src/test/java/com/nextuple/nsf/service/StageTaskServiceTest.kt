package com.nextuple.nsf.service

import com.nextuple.nsf.CoroutineRule
import com.nextuple.nsf.datastore.UserRepository
import com.nextuple.nsf.retrofit.api.StageTaskApi
import com.nextuple.nsf.retrofit.dto.ApiResponse
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
class StageTaskServiceTest {

	@get:Rule
	val coroutineRule = CoroutineRule()

	@InjectMockKs
	private lateinit var service: StageTaskService

	@MockK
	private lateinit var stageTaskApi: StageTaskApi

	@MockK
	private lateinit var userRepository: UserRepository

	@MockK(relaxed = true)
	private lateinit var logService: LogService

	@Before
	fun setUp() {
		MockKAnnotations.init(this)
	}

	@Test
	fun `recordHoldingLocation should call api with the expected values`() = runTest {
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

		every { runBlocking { userRepository.getUserId() } } returns "userId"
		every {
			runBlocking {
				stageTaskApi.recordHoldingLocation(any(), any())
			}
		} returns ApiResponse.Success(
			data = stageTask
		)

		service.recordHoldingLocation(req = recordHoldingLocationRequest)
		advanceUntilIdle()

		verify {
			runBlocking { stageTaskApi.recordHoldingLocation(any(), any()) }
		}
	}

	@Test
	fun `recordHoldingLocation should return general error on api success response having null data`() =
		runTest {
			val recordHoldingLocationRequest = RecordHoldingLocationRequest(
				containerId = 0,
				holdingLocation = ""
			)
			every { runBlocking { userRepository.getUserId() } } returns "userId"
			every {
				runBlocking {
					stageTaskApi.recordHoldingLocation(any(), any())
				}
			} returns ApiResponse.Success()

			val res = service.recordHoldingLocation(req = recordHoldingLocationRequest)
			advanceUntilIdle()

			Assert.assertEquals(Result.generalError(), res)
		}

	@Test
	fun `recordHoldingLocation should return general error if an exception is thrown`() = runTest {
		val recordHoldingLocationRequest = RecordHoldingLocationRequest(
			containerId = 0,
			holdingLocation = ""
		)
		every { runBlocking { userRepository.getUserId() } } returns "userId"
		every {
			runBlocking {
				stageTaskApi.recordHoldingLocation(any(), any())
			}
		} returns ApiResponse.Success()

		val res = service.recordHoldingLocation(req = recordHoldingLocationRequest)
		advanceUntilIdle()

		verify(exactly = 1) { logService.trackError(any(), any(), any()) }
		Assert.assertEquals(Result.generalError(), res)
	}

	@Test
	fun `recordHoldingLocation should return general error if getUserId is null`() = runTest {
		every { runBlocking { userRepository.getUserId() } } returns null

		val res = service.recordHoldingLocation(
			req = RecordHoldingLocationRequest(
				containerId = 0,
				holdingLocation = ""
			)
		)
		advanceUntilIdle()

		Assert.assertEquals(Result.generalError(), res)
	}

	@Test
	fun `getHoldSlip should call api with the expected values`() = runTest {
		val fulfillmentRequestNumber = "123"
		val stageTask = StageTask(
			id = 12,
			status = Status(code = "12", name = "COMPLETED"),
			fulfillmentRequestNumber = "123",
			containers = listOf(
				StageTaskContainer(id = 12, holdingLocation = "Hyderabad")
			)
		)
		every { runBlocking { userRepository.getUserId() } } returns "userId"
		every {
			runBlocking {
				stageTaskApi.getHoldSlip(any(), any())
			}
		} returns ApiResponse.Success(
			data = stageTask
		)

		service.getHoldSlip(fulfillmentRequestNumber = fulfillmentRequestNumber)
		advanceUntilIdle()

		verify {
			runBlocking { stageTaskApi.getHoldSlip(any(), any()) }
		}
	}

	@Test
	fun `getHoldSlip should return general error on api success response having null data`() =
		runTest {
			val fulfillmentRequestNumber = "123"
			every { runBlocking { userRepository.getUserId() } } returns "userId"
			every {
				runBlocking {
					stageTaskApi.getHoldSlip(any(), any())
				}
			} returns ApiResponse.Success()

			val res = service.getHoldSlip(fulfillmentRequestNumber = fulfillmentRequestNumber)
			advanceUntilIdle()

			Assert.assertEquals(Result.generalError(), res)
		}

	@Test
	fun `getHoldSlip should return general error if an exception is thrown`() = runTest {
		val fulfillmentRequestNumber = "123"
		every { runBlocking { userRepository.getUserId() } } returns "userId"
		every { runBlocking { stageTaskApi.getHoldSlip(any(), any()) } } throws Exception()

		val res = service.getHoldSlip(fulfillmentRequestNumber = fulfillmentRequestNumber)

		advanceUntilIdle()

		verify(exactly = 1) { logService.trackError(any(), any(), any()) }
		Assert.assertEquals(Result.generalError(), res)
	}

	@Test
	fun `getStoreConfig should return general error if getUserId is null`() = runTest {
		every { runBlocking { userRepository.getUserId() } } returns null

		val res = service.getHoldSlip(fulfillmentRequestNumber = "123")
		advanceUntilIdle()

		Assert.assertEquals(Result.generalError(), res)
	}
}
