package com.nextuple.nsf

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineRule : TestWatcher() {

	lateinit var scheduler: TestCoroutineScheduler

	override fun starting(description: Description) {
		super.starting(description)
		scheduler = TestCoroutineScheduler()
		Dispatchers.setMain(StandardTestDispatcher(scheduler))
	}

	override fun finished(description: Description) {
		super.finished(description)
		Dispatchers.resetMain()
	}
}
