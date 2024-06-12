package com.nextuple.nsf.service

import com.nextuple.nsf.CoroutineRule
import com.nextuple.nsf.datastore.UserRepository
import com.nextuple.nsf.retrofit.api.MetricsApi
import com.nextuple.nsf.retrofit.dto.ApiResponse
import com.nextuple.nsf.retrofit.dto.response.MetricsSummaryResponse
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
class MetricsServiceTest {
	@get:Rule
	val coroutineRule = CoroutineRule()

	@InjectMockKs
	private lateinit var service: MetricsService

	@MockK
	private lateinit var metricsApi: MetricsApi

	@MockK
	private lateinit var userRepository: UserRepository

	@Before
	fun setUp() {
		MockKAnnotations.init(this)
	}

	@Test
	fun `getMetricsSummary should call metrics api with the expected store`() = runTest {
		val userId = "userId123"
		val store = Store(id = "456", brand = Brand.NT_BRAND_A)

		every {
			runBlocking {
				metricsApi.getMetricsSummary(any(), any())
			}
		} returns ApiResponse.Success(
			data = MetricsSummaryResponse(
				bopis = MetricsSummaryResponse.OrderMetrics(),
				bopl = MetricsSummaryResponse.OrderMetrics()
			)
		)
		every { runBlocking { userRepository.getStore() } } returns store
		every { runBlocking { userRepository.getUserId() } } returns userId

		service.getMetricsSummary()
		advanceUntilIdle()

		verify {
			runBlocking { metricsApi.getMetricsSummary(store = store.id, userId = userId) }
		}
	}

	@Test
	fun `getMetricsSummary should return general error if get store is null`() = runTest {
		every { runBlocking { userRepository.getStore() } } returns null

		val res = service.getMetricsSummary()
		advanceUntilIdle()

		Assert.assertEquals(Result.generalError(), res)
	}

	@Test
	fun `getMetricsSummary should return general error if getUserId is null`() = runTest {
		every { runBlocking { userRepository.getStore() } } returns Store(id = "456", brand = Brand.NT_BRAND_A)
		every { runBlocking { userRepository.getUserId() } } returns null

		val res = service.getMetricsSummary()
		advanceUntilIdle()

		Assert.assertEquals(Result.generalError(), res)
	}

	@Test
	fun `getMetricsSummary should return general error if response is null`() = runTest {
		every {
			runBlocking {
				metricsApi.getMetricsSummary(any(), any())
			}
		} returns ApiResponse.Success()

		every { runBlocking { userRepository.getStore() } } returns Store(id = "456", brand = Brand.NT_BRAND_A)
		every { runBlocking { userRepository.getUserId() } } returns "userId123"

		val res = service.getMetricsSummary()
		advanceUntilIdle()

		Assert.assertEquals(Result.generalError(), res)
	}
}
