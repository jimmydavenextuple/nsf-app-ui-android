package com.nextuple.nsf.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextuple.nsf.datastore.UserRepository
import com.nextuple.nsf.service.ConfigService
import com.nextuple.nsf.service.UserService
import com.nextuple.nsf.service.dto.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
	@Suppress("UNUSED_PARAMETER")
	handler: SavedStateHandle,
	private val userService: UserService,
	private val userRepository: UserRepository,
	private val configService: ConfigService
) : ViewModel() {

	sealed class ViewState {
		/**
		 * The user is logged out or has yet to log in.
		 */
		object LoggedOut : ViewState()

		/**
		 * The user is currently being logged in by the app.
		 */
		object LoggingIn : ViewState()

		/**
		 * The user is currently logged in.
		 */
		object LoggedIn : ViewState()

		/**
		 * There was an error logging the user in.
		 */
		object LoginError : ViewState()
	}

	var viewState: ViewState by mutableStateOf(ViewState.LoggedOut)
		private set

	var user = userRepository.userFlow
		private set

	var errMsg: String? by mutableStateOf(null)
		private set

	private var onLogoutCallbacks: List<() -> Unit> = emptyList()

	fun setOnLogoutCallbacks(vararg callbacks: () -> Unit) {
		onLogoutCallbacks = callbacks.toList()
	}

	fun login(dks: String) = viewModelScope.launch {
		if (dks.isEmpty()) {
			errMsg = "Invalid Id."
			viewState = ViewState.LoginError
			return@launch
		}

		viewState = ViewState.LoggingIn

		when (val res = userService.login(appendDksPrefixIfNeeded(dks))) {
			is Result.Success -> {
				errMsg = null
				viewState = ViewState.LoggedIn
			}

			is Result.Error -> {
				errMsg = res.msg
				viewState = ViewState.LoginError
			}
		}
	}

	fun resetFromError() = logout()

	fun logout() = viewModelScope.launch {
		userService.logout()
		viewState = ViewState.LoggedOut
		errMsg = null
		configService.clearStoreConfig()
		onLogoutCallbacks.forEach { callback -> callback() }
	}

	fun updateUserActivity() = viewModelScope.launch {
		if (isLoggedIn()) {
			userRepository.updateLastActiveTime()
		}
	}

	fun handleTimeoutLogout(timeoutDuration: Long) = viewModelScope.launch {
		if (!isLoggedIn()) return@launch
		val lastActiveTime = Instant.ofEpochSecond(user.first().lastActiveTime.seconds)
		if (lastActiveTime.plusMillis(timeoutDuration) <= Instant.now()) {
			logout()
		}
	}

	fun updateViewState(viewState: ViewState) {
		this.viewState = viewState
	}

	fun isLoggedIn() = viewState == ViewState.LoggedIn
	private fun appendDksPrefixIfNeeded(dks: String): String =
		if (!dks.lowercase().startsWith("dks") && dks.all { it.isDigit() }) {
			"dks$dks"
		} else {
			dks
		}
}
