package com.lge.support.second.application.managers.robot.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.MainApplication
import com.lge.support.second.application.data.robot.MoveState
import kotlinx.coroutines.*
import org.apache.log4j.chainsaw.Main

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
//        val mainViewModel = MainActivity.viewModel
        val roboRepo = MainApplication.mRobotRepo
        val chatbotRepo = MainApplication.mChatbotRepo
//        val roboViewModel = MainActivity.robotViewModel

        try {
            // TODO(0. EMERGENCY 인경우 작업 취소)
            // TODO(1. 스케줄링 시간 체크)
//            if (true) {
//                return Result.success()
//            }

            // TODO(2. 현재 대기 상태인지 체크)

            // Promote_n 요청
            coroutineScope {
//                MainActivity.viewModel.breakChat()
                chatbotRepo.breakChat()
                println("promote_n start")
//                MainActivity.viewModel.getResponse("promote_n")
                println("promote_n end")
            }

            // random poi 중 이동
//            if (roboViewModel.pois != null && roboViewModel.pois.size > 0) {
//                roboViewModel.pois[0].let { roboRepo.moveWithPoi(it) }
//
//                println("MOVE_DONE CHECK")
//                while (true) {
//                    println("MOVESTATE: ${roboViewModel.moveState.value}")
//                    if (roboViewModel.moveState.value == MoveState.MOVE_DONE) {
//                        break
//                    } else if (roboViewModel.moveState.value == MoveState.MOVE_FAIL) {
//                        Result.failure()
//                        break
//                    } else {
//                        delay(100)
//                    }
//                }
//            }

            delay(1000 * 30)
//            val outputCount = suspendCoroutine <Any> { continuation ->
//                roboRepo.monitoringMangerCallback.onEach {
//                    continuation.resume(it!!)
//                }
//            }
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