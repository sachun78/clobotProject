package com.lge.support.second.application.managers.robot.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.MainApplication
import com.lge.support.second.application.data.robot.MoveState
import kotlinx.coroutines.*
import kotlin.random.Random


class ScheduleWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    //    val mNavigationManager = NavigationManagerInstance.instance.getNavigationManager()

    override suspend fun doWork(): Result {
        return try {
            doActualWork()
            Result.success()

        } catch (e: Throwable) {
            Result.failure()
        }
    }

    suspend fun doActualWork() {
        val mainViewModel = MainActivity.viewModel
        val roboRepo = mainViewModel.getApplication<MainApplication>().mRobotRepo
        val roboViewModel = MainActivity.robotViewModel

        try {
            // TODO(0. EMERGENCY 인경우 작업 취소)
            // TODO(1. 스케줄링 시간 체크)
//            if (true) {
//                return Result.success()
//            }

            // TODO(2. 현재 대기 상태인지 체크)

            // Promote_n 요청
            withContext(Dispatchers.IO) {
                MainActivity.viewModel.getResponse("promote_n")

                println(MainActivity.viewModel.queryResult.value)

                // random poi 중 이동
                if (roboViewModel.pois != null && roboViewModel.pois.size > 6) {
                    roboViewModel.pois.get(Random.nextInt(7)).let { roboRepo.moveWithPoi(it) }
                }

                println("MOVE_DONE CHECK")

                while (true) {
                    if (roboViewModel.moveState.value == MoveState.MOVE_DONE) {
                        break
                    }
                    if (roboViewModel.moveState.value == MoveState.MOVE_FAIL) {
                        Result.failure()
                    }
                    delay(1000)
                    break
                }

                delay(1000 * 30)
//            val outputCount = suspendCoroutine <Any> { continuation ->
//                roboRepo.monitoringMangerCallback.onEach {
//                    continuation.resume(it!!)
//                }
//            }
            }
        } catch (error: Throwable) {
            Log.e("ScheduleWorker", "Error Scheulde DoWork")
        }

        val refreshWork = OneTimeWorkRequestBuilder<ScheduleWorker>().build()
        WorkManager.getInstance(MainActivity.mainContext()).enqueueUniqueWork(
            "schedule",
            ExistingWorkPolicy.REPLACE,
            refreshWork
        )
    }
}