package com.example.mvparch.mainModel.model

import com.example.mvparch.common.EventBus
import com.example.mvparch.common.SportEvent
import com.example.mvparch.common.getAdEventsInRealtime
import com.example.mvparch.common.getResultEventsInRealtime
import com.example.mvparch.common.someTime
import kotlinx.coroutines.delay

class MainRepository {
    suspend fun getEvents() {
        val events = getResultEventsInRealtime()
        events.forEach { event ->
            delay(someTime())
            publishEvent(event)
        }
    }

    suspend fun saveResult(result: SportEvent.ResultSuccess) {
        val response = if (result.isWarning)
            SportEvent.ResultError(30, "Error al guardar.")
        else SportEvent.SaveEvent
        publishEvent(response)
    }

    suspend fun registerAd() {
        val events = getAdEventsInRealtime()
        publishEvent(events.first())
    }

    suspend fun closeAd() {
        publishEvent(SportEvent.ClosedAdEvent)
    }

    private suspend fun publishEvent(event: SportEvent) {
        EventBus.instance().publish(event)
    }
}