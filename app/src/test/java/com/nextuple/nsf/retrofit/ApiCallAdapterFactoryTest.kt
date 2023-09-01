package com.nextuple.nsf.retrofit

import com.google.gson.reflect.TypeToken
import com.nextuple.nsf.retrofit.dto.ApiResponse
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Retrofit

class ApiCallAdapterFactoryTest {

	@InjectMockKs
	private lateinit var factory: ApiCallAdapterFactory

	@MockK
	private lateinit var retrofit: Retrofit

	@Before
	fun setUp() {
		MockKAnnotations.init(this)
	}

	@Test
	fun `get should return null for non-Call types`() {
		val res = factory.get(
			returnType = String::class.java,
			annotations = emptyArray(),
			retrofit = retrofit
		)

		assertNull(res)
	}

	@Test
	fun `get should return null for non-parameterized Call types`() {
		val res = factory.get(
			returnType = Call::class.java,
			annotations = emptyArray(),
			retrofit = retrofit
		)

		assertNull(res)
	}

	@Test
	fun `get should return null for parameterized, but non-Call types`() {
		val returnType = TypeToken.getParameterized(List::class.java, String::class.java).type

		val res = factory.get(
			returnType = returnType,
			annotations = emptyArray(),
			retrofit = retrofit
		)

		assertNull(res)
	}

	@Test
	fun `get should return null for Call types with non-parameterized inner type`() {
		val returnType = TypeToken.getParameterized(Call::class.java, String::class.java).type

		val res = factory.get(
			returnType = returnType,
			annotations = emptyArray(),
			retrofit = retrofit
		)

		assertNull(res)
	}

	@Test
	fun `get should return null for call types where the inner type is not ApiResponse`() {
		val innerType = TypeToken.getParameterized(List::class.java, String::class.java).type
		val returnType = TypeToken.getParameterized(Call::class.java, innerType).type

		val res = factory.get(
			returnType = returnType,
			annotations = emptyArray(),
			retrofit = retrofit
		)

		assertNull(res)
	}

	@Test
	fun `get should return the adapter when the call type is supported`() {
		val innerType = TypeToken.getParameterized(ApiResponse::class.java, String::class.java).type
		val returnType = TypeToken.getParameterized(Call::class.java, innerType).type

		val res = factory.get(
			returnType = returnType,
			annotations = emptyArray(),
			retrofit = retrofit
		)

		assertNotNull(res)
	}
}
