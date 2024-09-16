package com.nextuple.nsf.retrofit.dto.response

import com.nextuple.nsf.retrofit.dto.PackTask
import com.nextuple.nsf.retrofit.dto.StageTask

data class GetUserPrepTasksResponse(
	val packTask: PackTask? = null,
	val stageTask: StageTask? = null
)
