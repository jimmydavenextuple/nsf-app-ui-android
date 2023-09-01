package com.nextuple.nsf.ui.util

sealed class GenericViewState {
	/**
	 * Default ViewState.
	 */
	object Idle : GenericViewState()

	/**
	 * The API has success.
	 */
	object Success : GenericViewState()

	/**
	 * The API has processing .
	 */
	object Loading : GenericViewState()

	/**
	 * The API has failed.
	 */
	object Failure : GenericViewState()
}
