package com.lge.support.second.application.data.chatbot.dto

import com.google.gson.annotations.SerializedName

data class Message(
    val image: List<Image>,
    @SerializedName("image_button") val imageButton: List<ImageButton>,
    val description: List<DescriptionDto>,
    val button: List<Button>,
    val temp_directYN: String
)