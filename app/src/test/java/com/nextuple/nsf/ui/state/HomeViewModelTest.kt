package com.nextuple.nsf.ui.state

import androidx.lifecycle.SavedStateHandle
import com.nextuple.nsf.CoroutineRule
import com.nextuple.nsf.retrofit.dto.response.MetricsSummaryResponse
import com.nextuple.nsf.service.MetricsService
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
class HomeViewModelTest {
	@get:Rule
	val coroutineRule = CoroutineRule()

	@InjectMockKs
	private lateinit var vm: HomeViewModel

	@Suppress("unused")
	@MockK
	private lateinit var savedStateHandle: SavedStateHandle

	@MockK
	private lateinit var metricsService: MetricsService

	@Before
	fun setUp() {
		MockKAnnotations.init(this)
	}

	@Test
	fun `metricsSummaryData should be set correctly initial`() {
		Assert.assertEquals(vm.metricsSummaryData.state, GenericViewState.Idle)
		Assert.assertNull(vm.metricsSummaryData.data)
	}

	@Test
	fun `metricsSummaryData should set metricsSummaryData correctly on metricService success`() =
		runTest {
			every { runBlocking { metricsService.getMetricsSummary() } } returns Result.Success(
				MetricsSummaryResponse()
			)

			vm.setMetricsSummary()
			advanceUntilIdle()

			Assert.assertEquals(vm.metricsSummaryData.state, GenericViewState.Success)
			Assert.assertEquals(vm.metricsSummaryData.data, MetricsSummaryResponse())
		}

	@Test
	fun `metricsSummaryData should set storeConfigData correctly on metricService failure`() =
		runTest {
			every { runBlocking { metricsService.getMetricsSummary() } } returns Result.Error()

			vm.setMetricsSummary()
			advanceUntilIdle()

			Assert.assertEquals(vm.metricsSummaryData.state, GenericViewState.Failure)
			Assert.assertNull(vm.metricsSummaryData.data)
		}
}
