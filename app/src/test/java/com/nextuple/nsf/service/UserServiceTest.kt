package com.nextuple.nsf.service

import com.nextuple.nsf.CoroutineRule
import com.nextuple.nsf.datastore.UserRepository
import com.nextuple.nsf.retrofit.api.UserApi
import com.nextuple.nsf.retrofit.dto.ApiResponse
import com.nextuple.nsf.retrofit.dto.LoginData
import com.nextuple.nsf.retrofit.dto.LoginRequest
import com.nextuple.nsf.service.LogService.Companion.EVENT_LOGOUT
import com.nextuple.nsf.service.dto.Brand.NT_BRAND_A
import com.nextuple.nsf.service.dto.Result
import com.nextuple.nsf.service.dto.Store
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.justRun
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserServiceTest {

	@get:Rule
	val coroutineRule = CoroutineRule()

	@InjectMockKs
	private lateinit var service: UserService

	@MockK
	private lateinit var userApi: UserApi

	@MockK(relaxed = true)
	private lateinit var logService: LogService

	@MockK
	private lateinit var deviceService: DeviceService

	@MockK
	private lateinit var userRepository: UserRepository

	@Before
	fun setUp() {
		MockKAnnotations.init(this)
	}

	@Test
	fun `login should call user api with the expected dks and store and update userRepo on success`() =
		runTest {
			val dks = "dks123"
			val firstName = "firstname"
			val lastName = "lastName"
			val store = Store(id = "456", brand = NT_BRAND_A)

			every { runBlocking { userApi.login(any()) } } returns ApiResponse.Success(
				data = LoginData(firstName = firstName, lastName = lastName)
			)
			every { deviceService.getStore() } returns store
			justRun { runBlocking { userRepository.updateUser(any(), any(), any()) } }

			service.login(dks = dks)
			advanceUntilIdle()

			verify {
				runBlocking { userApi.login(req = LoginRequest(dks = dks, store = store.id)) }
				runBlocking { userRepository.updateUser(firstName, lastName, dks) }
			}
		}

	@Test
	fun `login should return general error on api success response having null data`() = runTest {
		every { runBlocking { userApi.login(any()) } } returns ApiResponse.Success()
		every { deviceService.getStore() } returns Store(id = "0", brand = NT_BRAND_A)

		val res = service.login(dks = "anyDks")
		advanceUntilIdle()

		assertEquals(Result.generalError(), res)
	}

	@Test
	fun `login should return general error if getStore is null`() = runTest {
		every { deviceService.getStore() } returns null

		val res = service.login(dks = "anyDks")
		advanceUntilIdle()

		assertEquals(Result.generalError(), res)
	}

	@Test
	fun `logout should clearUser from userRepo`() = runTest {
		justRun { runBlocking { userRepository.clearUser() } }
		justRun { logService.trackEvent(any()) }
		justRun { logService.setUser(any()) }

		service.logout()

		verify {
			runBlocking { userRepository.clearUser() }
			logService.trackEvent(EVENT_LOGOUT)
			logService.setUser(null)
		}
	}
}
