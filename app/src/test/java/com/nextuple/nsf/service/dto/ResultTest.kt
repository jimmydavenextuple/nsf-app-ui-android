package com.nextuple.nsf.service.dto

import com.nextuple.nsf.retrofit.dto.ApiResponse
import com.nextuple.nsf.service.dto.Result.Companion.fromApiResponse
import com.nextuple.nsf.service.dto.Result.ErrorType.GENERAL
import com.nextuple.nsf.service.dto.Result.ErrorType.NETWORK
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class ResultTest {
	@Test
	fun `fromApiResponse, when converting success, should transform the data`() {
		val transform: (Int?) -> String = { it!!.toString() }

		when (val res = fromApiResponse(ApiResponse.Success(data = 7), transform)) {
			is Result.Success -> {
				assertEquals("7", res.data)
			}

			else -> fail("unexpected result")
		}
	}

	@Test
	fun `fromApiResponse, when converting error, should return the expected result`() {
		val msg = "test error"
		val type = GENERAL
		val status = 123456
		when (val res = fromApiResponse(ApiResponse.Error(msg = msg, status = status)) { it }) {
			is Result.Error -> {
				assertEquals(msg, res.msg)
				assertEquals(type, res.type)
			}

			else -> fail("unexpected result")
		}
	}

	@Test
	fun `fromApiResponse, when converting network error, should return the expected result`() {
		val res = fromApiResponse(ApiResponse.NetworkError(msg = "testError")) { it }
		assertEquals(Result.Error(msg = "testError", type = NETWORK), res)
	}
}
