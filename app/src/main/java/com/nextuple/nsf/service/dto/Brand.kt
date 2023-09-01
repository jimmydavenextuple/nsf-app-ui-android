package com.nextuple.nsf.service.dto

enum class Brand(val chainName: String) {
	NEXTUPLE(chainName = "NEXTUPLE"),
	UNKNOWN(chainName = "UNKNOWN");

	companion object {

		fun toBrand(chainName: String): Brand = when (chainName.uppercase()) {
			NEXTUPLE.chainName.uppercase() -> NEXTUPLE
			else -> UNKNOWN
		}
	}
}
