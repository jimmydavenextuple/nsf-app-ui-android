package com.nextuple.nsf.retrofit

import com.nextuple.nsf.retrofit.dto.ApiResponse
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiCallTest {

	data class TestData(
		val a: String,
		val b: Int
	)

	@InjectMockKs
	private lateinit var call: ApiCall<TestData>

	@MockK(relaxed = true)
	private lateinit var delegate: Call<TestData>

	@Before
	fun setUp() {
		MockKAnnotations.init(this)
	}

	@Test
	fun `enqueue, when api call is successful, should return the expected response`() {
		val data = TestData(a = "hello 123", b = 123)
		val delegateCallbackSlot = slot<Callback<TestData>>()
		justRun { delegate.enqueue(capture(delegateCallbackSlot)) }

		call.enqueue(object : Callback<ApiResponse<TestData>> {
			override fun onResponse(
				call: Call<ApiResponse<TestData>>,
				response: Response<ApiResponse<TestData>>
			) {
				assertEquals(ApiResponse.Success(data = data), response.body())
			}

			override fun onFailure(call: Call<ApiResponse<TestData>>, t: Throwable) {
				fail("unexpected callback invocation")
			}
		})

		delegateCallbackSlot.captured.onResponse(mockk(), Response.success(data))
	}

	@Test
	fun `enqueue, when api call returns error, should return the expected response`() {
		val errCode = 400
		val errMsg = "bad request!"
		val delegateCallbackSlot = slot<Callback<TestData>>()
		justRun { delegate.enqueue(capture(delegateCallbackSlot)) }

		call.enqueue(object : Callback<ApiResponse<TestData>> {
			override fun onResponse(
				call: Call<ApiResponse<TestData>>,
				response: Response<ApiResponse<TestData>>
			) {
				assertEquals(ApiResponse.Error(msg = errMsg, status = errCode), response.body())
			}

			override fun onFailure(call: Call<ApiResponse<TestData>>, t: Throwable) {
				fail("unexpected callback invocation")
			}
		})

		delegateCallbackSlot.captured.onResponse(
			mockk(),
			Response.error(errCode, errMsg.toResponseBody())
		)
	}

	@Test
	fun `enqueue, when api call fails due to network, should return the expected response`() {
		val ex = Exception("you've run out of data!")
		val delegateCallbackSlot = slot<Callback<TestData>>()
		justRun { delegate.enqueue(capture(delegateCallbackSlot)) }

		call.enqueue(object : Callback<ApiResponse<TestData>> {
			override fun onResponse(
				call: Call<ApiResponse<TestData>>,
				response: Response<ApiResponse<TestData>>
			) {
				assertEquals(ApiResponse.NetworkError(msg = ex.message), response.body())
			}

			override fun onFailure(call: Call<ApiResponse<TestData>>, t: Throwable) {
				fail("unexpected callback invocation")
			}
		})

		delegateCallbackSlot.captured.onFailure(mockk(), ex)
	}

	@Test
	fun `execute should throw since it is unsupported`() {
		assertThrows(UnsupportedOperationException::class.java) {
			call.execute()
		}
	}

	@Test
	fun `isExecuted should delegate`() {
		call.isExecuted
		verify { delegate.isExecuted }
	}

	@Test
	fun `cancel should delegate`() {
		call.cancel()
		verify { delegate.cancel() }
	}

	@Test
	fun `isCanceled should delegate`() {
		call.isCanceled
		verify { delegate.isCanceled }
	}

	@Test
	fun `request should delegate`() {
		call.request()
		verify { delegate.request() }
	}

	@Test
	fun `timeout should delegate`() {
		call.timeout()
		verify { delegate.timeout() }
	}

	@Test
	fun `clone should delegate and result should not be the same instance`() {
		every { delegate.clone() } returns delegate

		val res = call.clone()

		verify { delegate.clone() }
		assert(call !== res)
	}
}
