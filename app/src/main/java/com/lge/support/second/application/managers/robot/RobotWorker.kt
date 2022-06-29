package com.lge.support.second.application.managers.robot

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class RobotWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        TODO("Not yet implemented")
    }

}