package com.nextuple.nsf.service

import com.nextuple.nsf.CoroutineRule
import com.nextuple.nsf.datastore.UserRepository
import com.nextuple.nsf.retrofit.api.PackTaskApi
import com.nextuple.nsf.retrofit.dto.ApiResponse
import com.nextuple.nsf.retrofit.dto.PrepDetail
import com.nextuple.nsf.retrofit.dto.StageTask
import com.nextuple.nsf.retrofit.dto.StageTaskContainer
import com.nextuple.nsf.retrofit.dto.Status
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
class PackTaskServiceTest {

	companion object {
		private const val FR_NO = "10100100100.001"
	}

	@get:Rule
	val coroutineRule = CoroutineRule()

	@InjectMockKs
	private lateinit var service: PackTaskService

	@MockK
	private lateinit var packTaskApi: PackTaskApi

	@MockK
	private lateinit var deviceService: DeviceService

	@MockK
	private lateinit var userRepository: UserRepository

	@MockK(relaxed = true)
	private lateinit var logService: LogService

	@Before
	fun setUp() {
		MockKAnnotations.init(this)
	}

	@Test
	fun `packAndGetHoldSlip should call api with the expected values`() = runTest {
		val taskId = "taskId"
		val stageTask = StageTask(
			id = 12,
			status = Status(code = "12", name = "COMPLETED"),
			fulfillmentRequestNumber = "123",
			containers = listOf(
				StageTaskContainer(id = 12, holdingLocation = "Hyderabad")
			)
		)

		every { runBlocking { userRepository.getDks() } } returns "dks"
		every {
			runBlocking {
				packTaskApi.packAndGetHoldSlip(
					any(),
					any()
				)
			}
		} returns ApiResponse.Success(
			data = stageTask
		)

		service.packAndGetHoldSlip(taskId = taskId)
		advanceUntilIdle()

		verify {
			runBlocking { packTaskApi.packAndGetHoldSlip(any(), any()) }
		}
	}

	@Test
	fun `packAndGetHoldSlip should return general error on api success response having null data`() =
		runTest {
			every {
				runBlocking {
					packTaskApi.packAndGetHoldSlip(
						any(),
						any()
					)
				}
			} returns ApiResponse.Success()
			every { runBlocking { userRepository.getDks() } } returns "dks"

			val res = service.packAndGetHoldSlip(taskId = "taskId")
			advanceUntilIdle()

			Assert.assertEquals(Result.generalError(), res)
		}

	@Test
	fun `packAndGetHoldSlip should return general error if getDks is null`() = runTest {
		every { runBlocking { userRepository.getDks() } } returns null

		val res = service.packAndGetHoldSlip(taskId = "taskId")
		advanceUntilIdle()

		Assert.assertEquals(Result.generalError(), res)
	}

	@Test
	fun `getPrepDetails, single fr, should call api with expected values`() = runTest {
		val prepDetail = PrepDetail(
			fulfillmentType = "BOPIS",
			subFulfillmentType = "BOPIS",
			fulfillmentRequestNumber = "xx90000000",
			items = emptyList()
		)
		every { runBlocking { userRepository.getDks() } } returns "dks"
		every {
			runBlocking {
				packTaskApi.getPrepDetails(
					any(),
					any()
				)
			}
		} returns ApiResponse.Success(
			data = listOf(prepDetail)
		)

		service.getPrepDetails(frNo = "xx900000")
		advanceUntilIdle()

		verify {
			runBlocking { packTaskApi.getPrepDetails(any(), any()) }
		}
	}

	@Test
	fun `getPrepDetails, single fr, should return general error on api success response having null data`() =
		runTest {
			every {
				runBlocking {
					packTaskApi.getPrepDetails(
						any(),
						any()
					)
				}
			} returns ApiResponse.Success()
			every { runBlocking { userRepository.getDks() } } returns "dks"

			val res = service.getPrepDetails(frNo = "xx900000")
			advanceUntilIdle()

			Assert.assertEquals(Result.generalError(), res)
		}

	@Test
	fun `getPrepDetails, single fr, should return general error if getDks is null`() = runTest {
		every { runBlocking { userRepository.getDks() } } returns null

		val res = service.getPrepDetails(frNo = "xx900000")
		advanceUntilIdle()

		Assert.assertEquals(Result.generalError(), res)
	}

	@Test
	fun `getPrepDetails, fr list, should call api with expected values`() = runTest {
		val prepDetail = PrepDetail(
			fulfillmentType = "BOPIS",
			subFulfillmentType = "BOPIS",
			fulfillmentRequestNumber = "xx90000000",
			items = emptyList()
		)
		every { runBlocking { userRepository.getDks() } } returns "dks"
		every {
			runBlocking {
				packTaskApi.getPrepDetails(
					any(),
					any()
				)
			}
		} returns ApiResponse.Success(
			data = listOf(prepDetail)
		)

		service.getPrepDetails(frNos = listOf("xx900000"))
		advanceUntilIdle()

		verify {
			runBlocking { packTaskApi.getPrepDetails(any(), any()) }
		}
	}

	@Test
	fun `getPrepDetails, fr list, should return general error on api success response having null data`() =
		runTest {
			every {
				runBlocking {
					packTaskApi.getPrepDetails(
						any(),
						any()
					)
				}
			} returns ApiResponse.Success()
			every { runBlocking { userRepository.getDks() } } returns "dks"

			val res = service.getPrepDetails(frNos = listOf("xx900000"))
			advanceUntilIdle()

			Assert.assertEquals(Result.generalError(), res)
		}

	@Test
	fun `getPrepDetails, fr list, should return general error if getDks is null`() = runTest {
		every { runBlocking { userRepository.getDks() } } returns null

		val res = service.getPrepDetails(frNos = listOf("xx900000"))
		advanceUntilIdle()

		Assert.assertEquals(Result.generalError(), res)
	}

	@Test
	fun `packByGear should call api with expected values`() = runTest {
		val prepDetail = PrepDetail(
			fulfillmentType = "BOPIS",
			subFulfillmentType = "BOPIS",
			fulfillmentRequestNumber = "xx90000000",
			items = emptyList()
		)
		every { deviceService.getStore() } returns Store(id = "0", brand = Brand.NT_BRAND_A)
		every { runBlocking { userRepository.getDks() } } returns "dks"
		every { runBlocking { packTaskApi.packByGear(any(), any()) } } returns ApiResponse.Success(
			data = prepDetail
		)

		service.packByGear(upc = "upc")
		advanceUntilIdle()

		verify {
			runBlocking { packTaskApi.packByGear(any(), any()) }
		}
	}

	@Test
	fun `packByGear should return general error on api success response having null data`() =
		runTest {
			every {
				runBlocking {
					packTaskApi.getPrepDetails(
						any(),
						any()
					)
				}
			} returns ApiResponse.Success()
			every { deviceService.getStore() } returns Store(id = "0", brand = Brand.NT_BRAND_A)
			every { runBlocking { userRepository.getDks() } } returns "dks"

			val res = service.packByGear(upc = "upc")
			advanceUntilIdle()

			Assert.assertEquals(Result.generalError(), res)
		}

	@Test
	fun `packByGear should return general error if getStore is null`() = runTest {
		every { deviceService.getStore() } returns null

		val res = service.packByGear(upc = "upc")
		advanceUntilIdle()

		Assert.assertEquals(Result.generalError(), res)
	}

	@Test
	fun `packByGear should return general error if getDks is null`() = runTest {
		every { deviceService.getStore() } returns Store(id = "0", brand = Brand.NT_BRAND_A)
		every { runBlocking { userRepository.getDks() } } returns null

		val res = service.packByGear(upc = "upc")
		advanceUntilIdle()

		Assert.assertEquals(Result.generalError(), res)
	}

	@Test
	fun `packByOrder should call api with expected values`() = runTest {
		val prepDetail = PrepDetail(
			fulfillmentType = "BOPIS",
			subFulfillmentType = "BOPIS",
			fulfillmentRequestNumber = FR_NO,
			items = emptyList()
		)
		every { deviceService.getStore() } returns Store(id = "0", brand = Brand.NT_BRAND_A)
		every { runBlocking { userRepository.getDks() } } returns "dks"
		every { runBlocking { packTaskApi.packByOrder(any(), any()) } } returns ApiResponse.Success(
			data = prepDetail
		)
		service.packByOrder(frNo = FR_NO)
		advanceUntilIdle()
		verify {
			runBlocking { packTaskApi.packByOrder(any(), any()) }
		}
	}

	@Test
	fun `packByOrder should return general error on api success response having null data`() =
		runTest {
			every {
				runBlocking {
					packTaskApi.getPrepDetails(
						any(),
						any()
					)
				}
			} returns ApiResponse.Success()
			every { deviceService.getStore() } returns Store(id = "0", brand = Brand.NT_BRAND_A)
			every { runBlocking { userRepository.getDks() } } returns "dks"
			val res = service.packByOrder(frNo = FR_NO)
			advanceUntilIdle()
			Assert.assertEquals(Result.generalError(), res)
		}

	@Test
	fun `packByOrder should return general error if getStore is null`() = runTest {
		every { deviceService.getStore() } returns null
		val res = service.packByOrder(frNo = FR_NO)
		advanceUntilIdle()
		Assert.assertEquals(Result.generalError(), res)
	}

	@Test
	fun `packByOrder should return general error if getDks is null`() = runTest {
		every { deviceService.getStore() } returns Store(id = "0", brand = Brand.NT_BRAND_A)
		every { runBlocking { userRepository.getDks() } } returns null
		val res = service.packByOrder(frNo = FR_NO)
		advanceUntilIdle()
		Assert.assertEquals(Result.generalError(), res)
	}
}
