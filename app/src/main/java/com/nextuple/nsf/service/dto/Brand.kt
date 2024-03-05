package com.nextuple.nsf.service.dto

enum class Brand(val chainName: String) {
	NT(chainName = "NEXTUPLE"),
	UNKNOWN(chainName = "UNKNOWN");

	companion object {

		fun toBrand(chainName: String): Brand = when (chainName.uppercase()) {
			NT.chainName.uppercase() -> NT
			else -> UNKNOWN
		}
	}
}
