package com.nextuple.nsf.retrofit

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import java.lang.reflect.Type

class ApiCallAdapterTest {

	data class TestData(
		val a: String,
		val b: Int
	)

	@InjectMockKs
	private lateinit var adapter: ApiCallAdapter<TestData>

	@MockK
	private lateinit var dataType: Type

	@Before
	fun setUp() {
		MockKAnnotations.init(this)
	}

	@Test
	fun `responseType should return the expected data type`() {
		assertEquals(dataType, adapter.responseType())
	}

	@Test
	fun `adapt should return the expected call`() {
		val call = mockk<Call<TestData>>()
		assert(adapter.adapt(call) is ApiCall<TestData>)
	}
}
