package com.nextuple.nsf.ui.state

import androidx.lifecycle.SavedStateHandle
import com.nextuple.nsf.CoroutineRule
import com.nextuple.nsf.retrofit.api.StoreConfig
import com.nextuple.nsf.retrofit.api.StoreConfig.Companion.PICK_ITEM_SYMBOLOGY_PREFIXES
import com.nextuple.nsf.service.ConfigService
import com.nextuple.nsf.service.LogService
import com.nextuple.nsf.service.dto.Result
import com.nextuple.nsf.ui.util.GenericViewState
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConfigViewModelTest {

	@get:Rule
	val coroutineRule = CoroutineRule()

	@InjectMockKs
	private lateinit var vm: ConfigViewModel

	@Suppress("unused")
	@MockK
	private lateinit var savedStateHandle: SavedStateHandle

	@MockK
	private lateinit var configService: ConfigService

	@MockK(relaxed = true)
	private lateinit var logService: LogService

	@Before
	fun setUp() {
		MockKAnnotations.init(this)
	}

	@Test
	fun `storeConfigData should be set correctly initial`() {
		assertEquals(vm.storeConfigData.state, GenericViewState.Idle)
		assertNull(vm.storeConfigData.data)
	}

	@Test
	fun `setStoreConfig should set storeConfigData correctly on configService success`() = runTest {
		every { runBlocking { configService.getStoreConfig() } } returns Result.Success(
			StoreConfig()
		)

		vm.setStoreConfig()
		advanceUntilIdle()

		assertEquals(vm.storeConfigData.state, GenericViewState.Success)
		assertEquals(vm.storeConfigData.data, StoreConfig())
	}

	@Test
	fun `setStoreConfig should set storeConfigData correctly on configService failure`() = runTest {
		every { runBlocking { configService.getStoreConfig() } } returns Result.Error()

		vm.setStoreConfig()
		advanceUntilIdle()

		assertEquals(vm.storeConfigData.state, GenericViewState.Failure)
		assertNull(vm.storeConfigData.data)
	}

	@Test
	fun `clearStoreConfig should call configService`() {
		every { configService.clearStoreConfig() } just Runs

		vm.clearStoreConfig()

		verify {
			configService.clearStoreConfig()
		}
	}

	@Test
	fun `getPickSymbologyPrefixes should use fallback values when StoreConfig values are empty`() =
		runTest {
			every { runBlocking { configService.getStoreConfig() } } returns Result.Success(
				StoreConfig(pickSymbologyPrefixes = emptyList())
			)

			vm.setStoreConfig()
			advanceUntilIdle()

			assertEquals(PICK_ITEM_SYMBOLOGY_PREFIXES, vm.getPickSymbologyPrefixes())
		}

	@Test
	fun `getPickSymbologyPrefixes should use the values from setStoreConfig when not empty`() =
		runTest {
			val expected = setOf(
				"abc",
				"def",
				"gh123"
			)

			every { runBlocking { configService.getStoreConfig() } } returns Result.Success(
				StoreConfig(pickSymbologyPrefixes = expected.toList())
			)

			vm.setStoreConfig()
			advanceUntilIdle()

			assertEquals(expected, vm.getPickSymbologyPrefixes())
		}
}
