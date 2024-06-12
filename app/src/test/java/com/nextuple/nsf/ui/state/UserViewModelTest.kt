package com.nextuple.nsf.ui.state

import androidx.lifecycle.SavedStateHandle
import com.nextuple.nsf.CoroutineRule
import com.nextuple.nsf.datastore.UserRepository
import com.nextuple.nsf.service.ConfigService
import com.nextuple.nsf.service.UserService
import com.nextuple.nsf.service.dto.Brand
import com.nextuple.nsf.service.dto.Result
import com.nextuple.nsf.service.dto.Store
import com.nextuple.nsf.service.dto.User
import com.nextuple.nsf.ui.state.UserViewModel.ViewState
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.spyk
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
class UserViewModelTest {

	@get:Rule
	val coroutineRule = CoroutineRule()

	@InjectMockKs
	private lateinit var vm: UserViewModel

	@Suppress("unused")
	@MockK
	private lateinit var savedStateHandle: SavedStateHandle

	@MockK
	private lateinit var userService: UserService

	@MockK
	private lateinit var configService: ConfigService

	@MockK(relaxed = true)
	private lateinit var userRepository: UserRepository

	@Before
	fun setUp() {
		MockKAnnotations.init(this)
	}

	@Test
	fun `login should not call API when userId is empty and should enter error state`() = runTest {
		vm.login(nodeNo = "anyNodeNo", userId = "")
		advanceUntilIdle()

		assertEquals(ViewState.LoginFormValidationError, vm.viewState)

		verify(exactly = 0) {
			runBlocking { userService.login(any(), any()) }
		}
	}

	@Test
	fun `login should call API with the provided userId`() = runTest {
		val userId = "userId123"
		val store = Store(id = "456", brand = Brand.NT_BRAND_A)

		every { runBlocking { userService.login(any(), any()) } } returns mockk()
		vm.login(nodeNo = store.id, userId = userId)
		advanceUntilIdle()

		verify(exactly = 1) {
			runBlocking { userService.login(nodeNo = store.id, userId = userId) }
		}
	}

	@Test
	fun `login, on success, should populate user and enter success state`() = runTest {
		val userId = "userId123"
		val store = Store(id = "456", brand = Brand.NT_BRAND_A)
		val user = User(firstName = "first", lastName = "last", userId = "userId123")

		every { runBlocking { userService.login(nodeNo = store.id, userId = userId) } } returns Result.Success(user)
		vm.login(nodeNo = store.id, userId = userId)
		advanceUntilIdle()

		assertEquals(ViewState.LoggedIn, vm.viewState)
		assertNull(vm.errMsg)
	}

	@Test
	fun `login, on error, should enter error state`() = runTest {
		val errMsg = "error message"
		every { runBlocking { userService.login(any(), any()) } } returns Result.Error(msg = errMsg)
		vm.login(nodeNo = "anyNodeNo", userId = "anyUserId")
		advanceUntilIdle()

		assertEquals(ViewState.LoginError, vm.viewState)
		assertEquals(errMsg, vm.errMsg)
	}

	@Test
	fun `resetFromError should just call logout`() = runTest {
		justRun { runBlocking { userService.logout() } }
		justRun { configService.clearStoreConfig() }

		val spy = spyk(vm)
		spy.resetFromError()
		verify { spy.logout() }
	}

	@Test
	fun `logout, from logged-in state, should reset all fields to default values`() = runTest {
		every { runBlocking { userService.login(any(), any()) } } returns Result.Success(
			User(
				firstName = "first",
				lastName = "last",
				userId = "userId123"
			)
		)
		justRun { runBlocking { userService.logout() } }
		justRun { configService.clearStoreConfig() }

		vm.login(nodeNo = "anyNodeNo", userId = "anyUserId")
		advanceUntilIdle()

		assertEquals(ViewState.LoggedIn, vm.viewState)
		assertNull(vm.errMsg)

		vm.logout()
		advanceUntilIdle()

		assertEquals(ViewState.LoggedOut, vm.viewState)
		assertNull(vm.errMsg)

		verify {
			runBlocking { userService.logout() }
			configService.clearStoreConfig()
		}
	}

	@Test
	fun `logout, from error state, should reset all fields to default values`() = runTest {
		val errMsg = "error message"

		every { runBlocking { userService.login(any(), any()) } } returns Result.Error(errMsg)
		justRun { runBlocking { userService.logout() } }
		justRun { configService.clearStoreConfig() }

		vm.login(nodeNo = "anyNodeNo", userId = "anyUserId")
		advanceUntilIdle()

		assertEquals(ViewState.LoginError, vm.viewState)
		assertEquals(errMsg, vm.errMsg)

		vm.logout()
		advanceUntilIdle()

		assertEquals(ViewState.LoggedOut, vm.viewState)
		assertNull(vm.errMsg)

		verify {
			runBlocking { userService.logout() }
			configService.clearStoreConfig()
		}
	}

	@Test
	fun `updateUserActivity should call updateLastActiveTime when isLoggedIn is true`() = runTest {
		vm.updateViewState(ViewState.LoggedIn)
		vm.updateUserActivity()
		advanceUntilIdle()

		verify {
			runBlocking { userRepository.updateLastActiveTime() }
		}
	}

	@Test
	fun `updateUserActivity should NOT call updateLastActiveTime when isLoggedIn is false`() =
		runTest {
			vm.updateViewState(ViewState.LoggedOut)
			vm.updateUserActivity()
			advanceUntilIdle()

			verify(exactly = 0) {
				runBlocking { userRepository.updateLastActiveTime() }
			}
		}

	@Test
	fun `handleTimeoutLogout should NOT call logout when isLoggedIn is false`() = runTest {
		val spy = spyk(vm)

		spy.updateViewState(ViewState.LoggedOut)
		spy.handleTimeoutLogout(0L)
		advanceUntilIdle()

		verify(exactly = 0) {
			spy.logout()
		}
	}
}
