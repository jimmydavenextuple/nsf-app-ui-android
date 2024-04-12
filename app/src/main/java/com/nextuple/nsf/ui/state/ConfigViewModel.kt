package com.nextuple.nsf.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextuple.nsf.retrofit.api.StoreConfig
import com.nextuple.nsf.retrofit.api.StoreConfig.Companion.DEFAULT_HOLDING_LOCATIONS
import com.nextuple.nsf.retrofit.api.StoreConfig.Companion.PICK_ITEM_SYMBOLOGY_PREFIXES
import com.nextuple.nsf.service.ConfigService
import com.nextuple.nsf.service.LogService
import com.nextuple.nsf.service.dto.Result
import com.nextuple.nsf.ui.util.GenericViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigViewModel @Inject constructor(
	@Suppress("UNUSED_PARAMETER")
	handler: SavedStateHandle,
	private val configService: ConfigService,
	private val logService: LogService
) : ViewModel() {

	var storeConfigData: StoreConfigData by mutableStateOf(StoreConfigData())
		private set

	fun setStoreConfig() = viewModelScope.launch {
		storeConfigData = StoreConfigData(state = GenericViewState.Loading)
		storeConfigData = when (val res = configService.getStoreConfig()) {
			is Result.Success -> {
				StoreConfigData(state = GenericViewState.Success, data = res.data)
			}

			is Result.Error -> {
				StoreConfigData(state = GenericViewState.Failure)
			}
		}
	}

	fun clearStoreConfig() = configService.clearStoreConfig()

	fun getHoldingLocations() = storeConfigData.data?.holdingLocations ?: DEFAULT_HOLDING_LOCATIONS

	fun getPickSymbologyPrefixes(): Set<String> {
		val prefixesOrNull = storeConfigData.data?.pickSymbologyPrefixes?.ifEmpty { null }?.toSet()
		if (prefixesOrNull == null) {
			logService.trackError("getPickSymbologyPrefixes", Throwable("fallback to embedded"))
			return PICK_ITEM_SYMBOLOGY_PREFIXES
		}

		return prefixesOrNull
	}

	fun isInvalidSymbology(symbology: String) = getPickSymbologyPrefixes().none {
		symbology.startsWith(it, ignoreCase = true)
	}
}

data class StoreConfigData(
	val state: GenericViewState = GenericViewState.Idle,
	val data: StoreConfig? = null
)
