package com.nextuple.nsf.service

import com.nextuple.nsf.CoroutineRule
import com.nextuple.nsf.retrofit.api.UserApi
import com.nextuple.nsf.retrofit.dto.ApiResponse
import com.nextuple.nsf.retrofit.dto.LoginData
import com.nextuple.nsf.retrofit.dto.LoginRequest
import com.nextuple.nsf.service.dto.Brand.DSG
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

	@Before
	fun setUp() {
		MockKAnnotations.init(this)
	}

	@Test
	fun `login should call user api with the expected dks and store`() = runTest {
		val dks = "dks123"
		val store = Store(id = "456", brand = DSG)

		every { runBlocking { userApi.login(any()) } } returns ApiResponse.Success(
			data = LoginData(
				firstName = "",
				lastName = ""
			)
		)
		every { deviceService.getStore() } returns store

		service.login(dks = dks)
		advanceUntilIdle()

		verify {
			runBlocking { userApi.login(req = LoginRequest(dks = dks, store = store.id)) }
		}
	}

	@Test
	fun `login should return general error on api success response having null data`() = runTest {
		every { runBlocking { userApi.login(any()) } } returns ApiResponse.Success()
		every { deviceService.getStore() } returns Store(id = "0", brand = DSG)

		val res = service.login(dks = "anyDks")
		advanceUntilIdle()

		assertEquals(Result.generalError(), res)
	}
}
