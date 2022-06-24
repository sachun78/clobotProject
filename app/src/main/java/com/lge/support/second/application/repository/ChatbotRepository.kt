package com.lge.support.second.application.repository

import android.util.Log
import com.lge.support.second.application.data.chatbot.ChatRequest
import com.lge.support.second.application.data.chatbot.ChatbotApi
import com.lge.support.second.application.data.chatbot.ChatbotData
import com.lge.support.second.application.data.chatbot.dto.ChatbotResponseDto
import com.lge.support.second.application.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class ChatbotRepository constructor(
    private val api: ChatbotApi
) {
    companion object {
        private const val TAG = "ChatbotRepository"
    }
    
    private suspend fun getQueryResult(param: ChatRequest): ChatbotResponseDto {
        val response = api.query(param)
        Log.d(
            TAG,
            "in_str : \"${response.data.in_str}\" \n speech: ${response.data.result.fulfillment.speech}"
        )

        return response
    }

    operator fun invoke(param: ChatRequest): Flow<Resource<ChatbotData>> = flow {
        try {
            emit(Resource.Loading())
            val result = getQueryResult(param).serialize()
            Log.d(
                TAG,
                "invoked with param, ${result} "
            )
            emit(Resource.Success(result))
        } catch (e: HttpException) {
            emit(
                Resource.Error(
                    e.localizedMessage ?: "An unexpected error occured"
                )
            )
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}