package com.nextuple.nsf.datastore

import androidx.datastore.core.DataStore
import com.nextuple.nsf.Printer
import com.nextuple.nsf.Printers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PrintersRepository @Inject constructor(
	private val printersDataStore: DataStore<Printers>
) {

	fun getPrinters(): Flow<Printers> {
		return printersDataStore.data
	}

	suspend fun savePrinters(printers: List<Printer>) {
		printersDataStore.updateData {
			it.toBuilder().clear().build()
		}
		printersDataStore.updateData { printerList: Printers ->
			printerList.toBuilder().addAllPrinterList(printers).build()
		}
	}
}
