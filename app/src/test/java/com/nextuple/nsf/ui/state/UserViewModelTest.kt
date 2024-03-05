package com.nextuple.nsf.ui.state

import androidx.lifecycle.SavedStateHandle
import com.nextuple.nsf.CoroutineRule
import com.nextuple.nsf.datastore.UserRepository
import com.nextuple.nsf.service.ConfigService
import com.nextuple.nsf.service.UserService
import com.nextuple.nsf.service.dto.Result
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
	fun `login should not call API when DKS is empty and should enter error state`() = runTest {
		vm.login(dks = "")
		advanceUntilIdle()

		assertEquals(ViewState.LoginError, vm.viewState)
		assert(!vm.errMsg.isNullOrEmpty())

		verify(exactly = 0) {
			runBlocking { userService.login(any()) }
		}
	}

	@Test
	fun `login should call API with the provided DKS`() = runTest {
		val dks = "testDks123"

		every { runBlocking { userService.login(any()) } } returns mockk()
		vm.login(dks = dks)
		advanceUntilIdle()

		verify(exactly = 1) {
			runBlocking { userService.login(dks) }
		}
	}

	@Test
	fun `login, on success, should populate user and enter success state`() = runTest {
		val user = User(firstName = "first", lastName = "last", dks = "dks123")

		every { runBlocking { userService.login(dks = user.dks) } } returns Result.Success(user)
		vm.login(dks = user.dks)
		advanceUntilIdle()

		assertEquals(ViewState.LoggedIn, vm.viewState)
		assertNull(vm.errMsg)
	}

	@Test
	fun `login, on error, should enter error state`() = runTest {
		val errMsg = "error message"
		every { runBlocking { userService.login(any()) } } returns Result.Error(msg = errMsg)
		vm.login(dks = "any")
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
		every { runBlocking { userService.login(any()) } } returns Result.Success(
			User(
				firstName = "first",
				lastName = "last",
				dks = "dks123"
			)
		)
		justRun { runBlocking { userService.logout() } }
		justRun { configService.clearStoreConfig() }

		vm.login(dks = "any")
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

		every { runBlocking { userService.login(any()) } } returns Result.Error(errMsg)
		justRun { runBlocking { userService.logout() } }
		justRun { configService.clearStoreConfig() }

		vm.login(dks = "any")
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
}
