package com.lge.support.second.application.main.repository

import android.util.Log
import com.lge.support.second.application.main.data.chatbot.ChatRequest
import com.lge.support.second.application.main.data.chatbot.ChatbotApi
import com.lge.support.second.application.main.data.chatbot.dto.ChatbotResponseDto
import com.lge.support.second.application.main.data.chatbot.ChatbotData
import com.lge.support.second.application.main.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class ChatbotRepository constructor(
    private val api: ChatbotApi
) {
    val TAG = "ChatbotRepository"

    private suspend fun getQueryResult(param: ChatRequest): ChatbotResponseDto {
        val response = api.query(param)
        Log.d(
            TAG,
            "in_str : ${response.data.in_str},\n speech: ${response.data.result.fulfillment.speech}"
        )

        return response
    }

    operator fun invoke(param: ChatRequest): Flow<Resource<ChatbotData>> = flow {
        try {
            emit(Resource.Loading<ChatbotData>())
            val result = getQueryResult(param).serialize()
            Log.d(
                TAG,
                "invoked with param, ${result} "
            )
            emit(Resource.Success<ChatbotData>(result))
        } catch (e: HttpException) {
            emit(
                Resource.Error<ChatbotData>(
                    e.localizedMessage ?: "An unexpected error occured"
                )
            )
        } catch (e: IOException) {
            emit(Resource.Error<ChatbotData>("Couldn't reach server. Check your internet connection."))
        }
    }
}