package com.nextuple.nsf.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextuple.nsf.service.UserService
import com.nextuple.nsf.service.dto.Brand
import com.nextuple.nsf.service.dto.Result
import com.nextuple.nsf.service.dto.Store
import com.nextuple.nsf.service.dto.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
	@Suppress("UNUSED_PARAMETER")
	handler: SavedStateHandle?,
	private val userService: UserService?
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

	var user: User? by mutableStateOf(null)
		private set

	var errMsg: String? by mutableStateOf(null)
		private set

	constructor(
		user: User? = null,
		errMsg: String? = null,
		handler: SavedStateHandle?,
		userService: UserService?
	) : this(handler, userService) {
		this.viewState = ViewState.LoggedIn
		this.user = user
		this.errMsg = errMsg
	}

	fun login(dks: String) = viewModelScope.launch {
		if (dks.isEmpty()) {
			errMsg = "Invalid DKS number."
			viewState = ViewState.LoginError
			return@launch
		}

		viewState = ViewState.LoggingIn

		errMsg = null
		viewState = ViewState.LoggedIn
		user =
		User(
			firstName = "FN",
			lastName = "LN",
			dks = "ID000001",
			store = Store(id = "1234", brand = Brand.DSG)
		)



		/*user = when (val res = userService.login(dks)) {
			is Result.Success -> {
				errMsg = null
				viewState = ViewState.LoggedIn
				res.data
			}

			is Result.Error -> {
				errMsg = res.msg
				viewState = ViewState.LoginError
				null
			}
		}*/
	}

	fun resetFromError() = logout()

	fun logout() {
		userService?.logout()
		viewState = ViewState.LoggedOut
		user = null
		errMsg = null
	}
}
