package com.lge.support.second.application.model

data class State(
    val isUnDocking: Boolean = false,
    val isDocking: Boolean = false,
    val batterySOC: Int = 0,
    val emergency: Boolean = false,
    val isMoving: Boolean = false,
    val isGkrProcessing: Boolean = false,
)
