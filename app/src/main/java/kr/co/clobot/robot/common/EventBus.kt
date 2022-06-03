package kr.co.clobot.robot.common

import io.reactivex.subjects.PublishSubject
import java.util.*

object EventBus {

    val instance = EventBus

    val NAVI_TOPIC: String = "NAVI_TOPIC"
    val POWER_TOPIC_MODE: String = "POWER_TOPIC_GET_MODE"
    val BATTERY_TOPIC: String = "BATTERY_TOPIC"
    val ERROR_TOPIC: String = "ERROR_TOPIC"

    private val subjectTable = Hashtable<String, PublishSubject<Any>>()

    init {
        subjectTable[NAVI_TOPIC] = PublishSubject.create()
        subjectTable[POWER_TOPIC_MODE] = PublishSubject.create()
        subjectTable[BATTERY_TOPIC] = PublishSubject.create()
        subjectTable[ERROR_TOPIC] = PublishSubject.create()
    }

    fun sendEvent(key: String, any: Any) {
        subjectTable[key]?.onNext(any)
    }

    fun receiveEvent(key: String): PublishSubject<Any> {
        synchronized(this) {
            if (subjectTable.containsKey(key).not()) {
                subjectTable[key] = PublishSubject.create()
            }
            return subjectTable[key]!!
        }
    }
}
