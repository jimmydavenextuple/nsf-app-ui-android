package com.nextuple.nsf.service.dto

enum class Brand(val chainName: String) {
	NT_BRAND_A(chainName = "NT-BRAND_A"),
	UNKNOWN(chainName = "UNKNOWN");

	companion object {

		fun toBrand(chainName: String): Brand = when (chainName.uppercase()) {
			NT_BRAND_A.chainName.uppercase() -> NT_BRAND_A
			else -> UNKNOWN
		}
	}
}
