package com.nextuple.nsf.service

import com.nextuple.nsf.CoroutineRule
import com.nextuple.nsf.datastore.UserRepository
import com.nextuple.nsf.retrofit.api.ConfigApi
import com.nextuple.nsf.retrofit.api.StoreConfig
import com.nextuple.nsf.retrofit.dto.ApiResponse
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
class ConfigServiceTest {
	@get:Rule
	val coroutineRule = CoroutineRule()

	@InjectMockKs
	private lateinit var service: ConfigService

	@MockK
	private lateinit var configApi: ConfigApi

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
	fun `getStoreConfig should call config api with the expected dks and store`() = runTest {
		val dks = "dks123"
		val store = Store(id = "456", brand = Brand.NT)

		every {
			runBlocking {
				configApi.getStoreConfig(any(), any())
			}
		} returns ApiResponse.Success(
			data = StoreConfig()
		)
		every { deviceService.getStore() } returns store
		every { runBlocking { userRepository.getDks() } } returns dks

		service.getStoreConfig()
		advanceUntilIdle()

		verify {
			runBlocking { configApi.getStoreConfig(userId = dks, store = store.id) }
		}
	}

	@Test
	fun `getStoreConfig should only call configApi once if config is not reset`() = runTest {
		val dks = "dks123"
		val store = Store(id = "456", brand = Brand.NT)

		every {
			runBlocking {
				configApi.getStoreConfig(any(), any())
			}
		} returns ApiResponse.Success(
			data = StoreConfig()
		)
		every { deviceService.getStore() } returns store
		every { runBlocking { userRepository.getDks() } } returns dks

		service.getStoreConfig()
		advanceUntilIdle()

		service.getStoreConfig()
		advanceUntilIdle()

		verify(exactly = 1) {
			runBlocking { configApi.getStoreConfig(userId = dks, store = store.id) }
		}
	}

	@Test
	fun `getStoreConfig should only call configApi a second time if config is reset`() = runTest {
		val dks = "dks123"
		val store = Store(id = "456", brand = Brand.NT)

		every {
			runBlocking {
				configApi.getStoreConfig(any(), any())
			}
		} returns ApiResponse.Success(
			data = StoreConfig()
		)
		every { deviceService.getStore() } returns store
		every { runBlocking { userRepository.getDks() } } returns dks

		service.getStoreConfig()
		advanceUntilIdle()

		service.clearStoreConfig()

		service.getStoreConfig()
		advanceUntilIdle()

		verify(exactly = 2) {
			runBlocking { configApi.getStoreConfig(userId = dks, store = store.id) }
		}
	}

	@Test
	fun `getStoreConfig should return general error if getStore is null`() = runTest {
		every { deviceService.getStore() } returns null

		val res = service.getStoreConfig()
		advanceUntilIdle()

		Assert.assertEquals(Result.generalError(), res)
	}

	@Test
	fun `getStoreConfig should return general error if getDks is null`() = runTest {
		every { deviceService.getStore() } returns Store(id = "456", brand = Brand.NT)
		every { runBlocking { userRepository.getDks() } } returns null

		val res = service.getStoreConfig()
		advanceUntilIdle()

		Assert.assertEquals(Result.generalError(), res)
	}

	@Test
	fun `getStoreConfig should return general error if response is null`() = runTest {
		every {
			runBlocking {
				configApi.getStoreConfig(any(), any())
			}
		} returns ApiResponse.Success()
		every { deviceService.getStore() } returns Store(id = "456", brand = Brand.NT)
		every { runBlocking { userRepository.getDks() } } returns "dks"

		val res = service.getStoreConfig()
		advanceUntilIdle()

		Assert.assertEquals(Result.generalError(), res)
	}
}
