package com.nextuple.nsf.ui.state

import androidx.lifecycle.SavedStateHandle
import com.nextuple.nsf.CoroutineRule
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PrepViewModelTest {
	@get:Rule
	val coroutineRule = CoroutineRule()

	@InjectMockKs
	private lateinit var vm: PrepViewModel

	@Suppress("unused")
	@MockK
	private lateinit var savedStateHandle: SavedStateHandle

	@Before
	fun setUp() {
		MockKAnnotations.init(this)
	}

	@Test
	fun `onStoreOverview, packTask and stageTask values should be assigned`() =
		runTest {
			vm.onStoreOverview(null)

			assertNull(vm.packTask)
			assertNull(vm.stageTask)
		}
}
