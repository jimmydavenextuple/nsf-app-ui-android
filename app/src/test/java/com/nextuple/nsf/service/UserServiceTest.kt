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
	private lateinit var userService: UserService

	@MockK
	private lateinit var userApi: UserApi

	@MockK(relaxed = true)
	private lateinit var logService: LogService

	@MockK
	private lateinit var userRepository: UserRepository

	@Before
	fun setUp() {
		MockKAnnotations.init(this)
	}

	@Test
	fun `login should call user api with the expected userId and store and update userRepo on success`() =
		runTest {
			val userId = "userId123"
			val firstName = "firstname"
			val lastName = "lastName"
			val store = Store(id = "456", brand = NT_BRAND_A)

			every { runBlocking { userApi.login(any()) } } returns ApiResponse.Success(
				data = LoginData(
					firstName = firstName,
					lastName = lastName,
					userId = userId,
					nodeNo = store.id,
					fullName = "$firstName $lastName"
				)
			)
			justRun { runBlocking { userRepository.updateUser(any(), any(), any(), any(), any()) } }

			userService.login(nodeNo = store.id, userId = userId)
			advanceUntilIdle()

			verify {
				runBlocking { userApi.login(req = LoginRequest(nodeNo = store.id, userId = userId)) }
				runBlocking {
					userRepository.updateUser(
						firstName = firstName,
						lastName = lastName,
						userId = userId,
						nodeNo = store.id,
						brand = store.brand.toString()
					)
				}
			}
		}

	@Test
	fun `login should return general error on api success response having null data`() = runTest {
		every { runBlocking { userApi.login(any()) } } returns ApiResponse.Success()

		val res = userService.login(nodeNo = "anyNodeNo", userId = "anyUserId")
		advanceUntilIdle()

		assertEquals(Result.generalError(), res)
	}

	@Test
	fun `logout should clearUser from userRepo`() = runTest {
		justRun { runBlocking { userRepository.clearUser() } }
		justRun { logService.trackEvent(any()) }
		justRun { logService.setUser(any()) }

		userService.logout()

		verify {
			runBlocking { userRepository.clearUser() }
			logService.trackEvent(EVENT_LOGOUT)
			logService.setUser(null)
		}
	}
}
